package com.subway_footprint_system.springboot_project.Service.Impl;

import com.subway_footprint_system.springboot_project.Dao.Impl.AwardDaoImpl;
import com.subway_footprint_system.springboot_project.Dao.Impl.AwardRecordDaoImpl;
import com.subway_footprint_system.springboot_project.Pojo.Award;
import com.subway_footprint_system.springboot_project.Pojo.AwardRecord;
import com.subway_footprint_system.springboot_project.Service.IAwardRecordService;
import com.subway_footprint_system.springboot_project.Utils.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AwardRecordServiceImpl implements IAwardRecordService {
    @Autowired
    private AwardRecordDaoImpl awardRecordDao;
    @Autowired
    private AwardDaoImpl awardDao;
    @Override
    public boolean addBuryAwardRecord(String aid,String uid,int num,int credit) {
        AwardRecord awardRecord=new AwardRecord(uid+"-"+aid+"-"+JWTUtil.getNowTime(),0,uid,null,aid,num, JWTUtil.getNowTime(),credit);
        //award数量需相应减少
        Award award=awardDao.getAward(aid);
        if(0==award.getStatus()){
            award.setNum(Math.max(award.getNum() - num, 0));
            //若award剩余数量为0，状态改为已售空（2）
            if(0==award.getNum()){
                award.setStatus(2);
            }
            awardDao.updateAward(award);
            return awardRecordDao.insertAwardRecord(awardRecord);
        }
        return false;

    }
}
