package com.subway_footprint_system.springboot_project.Dao;

import com.subway_footprint_system.springboot_project.Pojo.AwardRecord;

import java.util.List;

public interface IAwardRecordDao {

    //增删改查方法
    boolean insertAwardRecord(AwardRecord awardRecord);
    boolean deleteAwardRecord(String arid);
    AwardRecord getAwardRecord(String arid);
    //获取用户所有奖品兑换记录
    List<AwardRecord> getExchangeAwardRecords(String uid);
    //获取用户所有奖品藏宝记录
    List<AwardRecord> getBuryAwardRecords(String uid);

}
