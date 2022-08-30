package com.subway_footprint_system.springboot_project.Service.Impl;

import com.subway_footprint_system.springboot_project.Dao.Impl.CreditRecordDaoImpl;
import com.subway_footprint_system.springboot_project.Pojo.CreditRecord;
import com.subway_footprint_system.springboot_project.Pojo.User;
import com.subway_footprint_system.springboot_project.Pojo.UserVo;
import com.subway_footprint_system.springboot_project.Service.ICreditRecordService;
import com.subway_footprint_system.springboot_project.Utils.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CreditRecordServiceImpl implements ICreditRecordService {
  @Autowired private CreditRecordDaoImpl creditRecordDao;
  @Autowired private UserServiceImpl userService;
  /*
  *
  *  private String crid;//碳积分流水记录id
      private String uid;//用户id
      private int operation;//操作类型（获得1/消耗0）
      private String way;//获得/消耗途径
      private int num;//获得/消耗数量
      private int balance;//剩余碳积分数目
      private String time;//操作时间
  * */
  @Override
  public boolean insertIncreaseCredit(String uid, String way, int num) {
    String time = JWTUtil.getNowTime();
    String crid = uid + "-" + time;
    User user = userService.getUserByUid(uid);
    int balance = user.getCredit() + num;
    CreditRecord creditRecord = new CreditRecord(crid, uid, 1, way, num, balance, time);
    user.setCredit(balance);
    return creditRecordDao.insertCreditRecord(creditRecord)
        && userService.updateUser(new UserVo(user));
  }

  @Override
  public boolean insertReduceCredit(String uid, String way, int num) {
    String time = JWTUtil.getNowTime();
    String crid = uid + "-" + time;
    User user = userService.getUserByUid(uid);
    int balance = user.getCredit() - num;
    if (balance < 0) {
      return false;
    }
    CreditRecord creditRecord = new CreditRecord(crid, uid, 0, way, num, balance, time);
    user.setCredit(balance);
    return creditRecordDao.insertCreditRecord(creditRecord)
        && userService.updateUser(new UserVo(user));
  }

  @Override
  public boolean insertCreditRecord(int operation, String uid, String way, int num) {
    if (1 == operation) {
      return insertIncreaseCredit(uid, way, num);
    } else if (0 == operation) {
      return insertReduceCredit(uid, way, num);
    }
    return false;
  }

  @Override
  public boolean deleteCreditRecord(String crid) {
    return creditRecordDao.deleteCreditRecord(crid);
  }

  @Override
  public CreditRecord getCreditRecord(String crid) {
    return creditRecordDao.getCreditRecord(crid);
  }

  @Override
  public List<CreditRecord> getUserCreditRecords(String uid, int group) {
    return creditRecordDao.getUserCreditRecords(uid, group);
  }
}
