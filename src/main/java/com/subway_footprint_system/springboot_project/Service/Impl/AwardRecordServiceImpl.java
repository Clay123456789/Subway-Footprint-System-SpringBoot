package com.subway_footprint_system.springboot_project.Service.Impl;

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

    @Override
    public boolean addBuryAwardRecord(String aid,String uid,int num,int credit) {
        AwardRecord awardRecord=new AwardRecord(uid+aid,0,uid,null,aid,num, JWTUtil.getNowTime(),credit);
        return awardRecordDao.insertAwardRecord(awardRecord);
    }
}
