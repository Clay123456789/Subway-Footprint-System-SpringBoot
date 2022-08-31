package com.subway_footprint_system.springboot_project.Service.Impl;

import com.subway_footprint_system.springboot_project.Dao.Impl.AwardDaoImpl;
import com.subway_footprint_system.springboot_project.Dao.Impl.AwardRecordDaoImpl;
import com.subway_footprint_system.springboot_project.Dao.Impl.MerchantDaoImpl;
import com.subway_footprint_system.springboot_project.Dao.Impl.UserDaoImpl;
import com.subway_footprint_system.springboot_project.Pojo.Award;
import com.subway_footprint_system.springboot_project.Pojo.AwardRecord;
import com.subway_footprint_system.springboot_project.Service.IAwardRecordService;
import com.subway_footprint_system.springboot_project.Utils.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AwardRecordServiceImpl implements IAwardRecordService {
    @Autowired
    private AwardRecordDaoImpl awardRecordDao;
    @Autowired
    private CreditRecordServiceImpl creditRecordService;
    @Autowired
    private UserDaoImpl userDao;
    @Autowired
    private AwardDaoImpl awardDao;
    @Autowired
    private MerchantDaoImpl merchantDao;

    @Override
    public boolean addUserBuryAwardRecord(String aid, String uid, int num, int credit) {
        Award award = awardDao.getAward(aid);
        if (null == award || null == userDao.getUserByUid(uid)) {
            return false;
        }
        String time = JWTUtil.getNowTime();
        String mid = award.getMid();
        AwardRecord awardRecord = new AwardRecord(uid + "-" + aid + "-" + time, 0, uid, mid, aid, num, time, credit);
        //award数量需相应减少
        if (0 == award.getStatus() && award.getNum() >= num) {
            award.setNum(Math.max(award.getNum() - num, 0));
            //若award剩余数量为0，状态改为已售空（2）
            if (0 == award.getNum()) {
                award.setStatus(2);
            }
            awardDao.updateAward(award);
            return awardRecordDao.insertMysqlAwardRecord(awardRecord);
        }
        return false;

    }

    @Override
    public boolean addMerchantBuryAwardRecord(String aid, String mid, int num, int credit) {
        Award award = awardDao.getAward(aid);
        if (null == award || null == (merchantDao.getMerchantByMid(mid)) || !mid.equals(award.getMid())) {
            return false;
        }
        String time = JWTUtil.getNowTime();
        AwardRecord awardRecord = new AwardRecord(mid + "-" + aid + "-" + time, 0, null, mid, aid, num, time, credit);
        //award数量需相应减少
        if (0 == award.getStatus() && award.getNum() >= num) {
            award.setNum(Math.max(award.getNum() - num, 0));
            //若award剩余数量为0，状态改为已售空（2）
            if (0 == award.getNum()) {
                award.setStatus(2);
            }
            awardDao.updateAward(award);
            return awardRecordDao.insertMysqlAwardRecord(awardRecord);
        }
        return false;
    }

    @Override
    public AwardRecord isExistShoppingAwardRecord(String aid, String uid) {
        return awardRecordDao.isExistShoppingAwardRecord(aid, uid);
    }

    @Override
    public boolean insertShoppingAwardRecord(String aid, String uid, int num) {
        //查找购物车中是否已有该奖品
        AwardRecord awardRecord = isExistShoppingAwardRecord(aid, uid);
        if (null != awardRecord) {//有
            awardRecord.setNum(awardRecord.getNum() + 1);
            awardRecord.setTime(JWTUtil.getNowTime());
            return awardRecordDao.updateMysqlAwardRecord(awardRecord);
        } else {//无
            Award award = awardDao.getAward(aid);
            if (null == award || null == userDao.getUserByUid(uid) || 0 != award.getStatus()) {
                return false;
            }
            String time = JWTUtil.getNowTime();
            String mid = award.getMid();
            int credit = num * award.getCredit();
            AwardRecord awardRecord2 = new AwardRecord(uid + "-" + aid + "-" + time, -1, uid, mid, aid, num, time, credit);
            return awardRecordDao.insertMysqlAwardRecord(awardRecord2);
        }

    }

    @Override
    public boolean deleteShoppingAwardRecord(String arid) {
        return awardRecordDao.deleteMysqlAwardRecord(arid);
    }

    @Override
    public boolean deleteOrderAwardRecord(String arid) {
        AwardRecord awardRecord = awardRecordDao.getMysqlAwardRecord(arid, 99);
        //只能删除状态为3,4的订单
        if (null != awardRecord && (3 == awardRecord.getOperation() || 4 == awardRecord.getOperation())) {
            return awardRecordDao.deleteMysqlAwardRecord(arid);
        }
        return false;
    }

    @Override
    public AwardRecord getShoppingAwardRecord(String arid) {
        return awardRecordDao.getMysqlAwardRecord(arid, -1);
    }

    @Override
    public AwardRecord getOrderAwardRecord(String arid) {
        return awardRecordDao.getRedisAwardRecord(arid);
    }

    @Override
    public AwardRecord getAnyAwardRecord(String arid) {
        return awardRecordDao.getMysqlAwardRecord(arid, 99);
    }

    @Override
    public boolean createOrderAwardRecordByShopping(String arid) {
        AwardRecord awardRecord = awardRecordDao.getMysqlAwardRecord(arid, -1);
        Award award = awardDao.getAward(awardRecord.getAid());
        //award数量需相应减少
        if (0 == award.getStatus() && award.getNum() >= awardRecord.getNum()) {
            //将数据库状态从购物车改为订单中
            awardRecord.setOperation(2);
            if (!awardRecordDao.updateMysqlAwardRecord(awardRecord)) {
                return false;
            }
            awardRecord.setTime(JWTUtil.getNowTime());
            award.setNum(award.getNum() - awardRecord.getNum());
            //若award剩余数量为0，状态改为已售空（2）
            if (0 == award.getNum()) {
                award.setStatus(2);
            }
            awardDao.updateAward(award);
            return awardRecordDao.insertRedisAwardRecord(awardRecord);
        }
        return false;
    }

    @Override
    public boolean createOrderAwardRecord(String aid, String uid, int num) {
        Award award = awardDao.getAward(aid);
        if (null == award || null == userDao.getUserByUid(uid) || 0 != award.getStatus() || award.getNum() < num) {
            return false;
        }
        String time = JWTUtil.getNowTime();
        String mid = award.getMid();
        int credit = num * award.getCredit();
        AwardRecord awardRecord = new AwardRecord(uid + "-" + aid + "-" + time, 2, uid, mid, aid, num, time, credit);
        award.setNum(award.getNum() - awardRecord.getNum());
        //若award剩余数量为0，状态改为已售空（2）
        if (0 == award.getNum()) {
            award.setStatus(2);
        }
        return awardDao.updateAward(award) && awardRecordDao.insertMysqlAwardRecord(awardRecord) && awardRecordDao.insertRedisAwardRecord(awardRecord);
    }

    @Override
    public boolean finishOrderAwardRecord(String arid) {
        AwardRecord awardRecord = awardRecordDao.getRedisAwardRecord(arid);
        if (null == awardRecord) {//订单不存在
            return false;
        }
        if (null == awardDao.getAward(awardRecord.getAid())) {//商品不存在
            return false;
        }
        //用户扣除碳积分
        if (!creditRecordService.insertReduceCredit(awardRecord.getUid(), "兑换奖品", awardRecord.getNum())) {
            //用户碳积分余额不足，订单结算失败
            return false;
        }
        //更改数据库状态,从“订单中”变已兑换
        awardRecord.setOperation(1);
        if (!awardRecordDao.updateMysqlAwardRecord(awardRecord)) {
            return false;
        }
        //将redis订单删除
        return awardRecordDao.deleteRedisAwardRecord(arid);
    }

    @Override
    public boolean cancelOrderAwardRecord(String arid) {

        AwardRecord awardRecord = awardRecordDao.getRedisAwardRecord(arid);
        if (null == awardRecord) {//订单不存在
            return false;
        }
        Award award = awardDao.getAward(awardRecord.getAid());
        if (null == award) {//商品不存在
            return false;
        }
        //将商品“释放”回去
        award.setNum(award.getNum() + awardRecord.getNum());
        if (2 == award.getStatus()) {//状态为已售空，恢复正常
            award.setStatus(0);
        }
        //更改数据库状态，从“订单中”变“取消”
        awardRecord.setOperation(3);
        if (!awardRecordDao.updateMysqlAwardRecord(awardRecord)) {
            return false;
        }
        //删除redis订单
        return awardRecordDao.deleteRedisAwardRecord(arid);
    }

    @Override
    public boolean expireOrder(String arid) {
        AwardRecord awardRecord = awardRecordDao.getMysqlAwardRecord(arid, 2);
        Award award = awardDao.getAward(awardRecord.getAid());
        if (null == award) {
            return false;
        }
        //将商品“释放”回去
        award.setNum(award.getNum() + awardRecord.getNum());
        if (2 == award.getStatus()) {//状态为已售空，恢复正常
            award.setStatus(0);
        }
        //数据库状态变“订单超时”
        awardRecord.setOperation(4);
        awardRecordDao.updateMysqlAwardRecord(awardRecord);
        return true;
    }

    @Override
    public List<AwardRecord> getExchangeAwardRecords(String uid, int group) {
        return awardRecordDao.getAwardRecords(1, uid, group);
    }

    @Override
    public List<AwardRecord> getShoppingAwardRecords(String uid, int group) {
        return awardRecordDao.getAwardRecords(-1, uid, group);
    }

    @Override
    public List<AwardRecord> getOrderAwardRecords(String uid, int group) {
        return awardRecordDao.getAwardRecords(2, uid, group);
    }

    @Override
    public List<AwardRecord> getAllOrderAwardRecords(String uid, int group) {
        return awardRecordDao.getAwardRecords(99, uid, group);
    }
}
