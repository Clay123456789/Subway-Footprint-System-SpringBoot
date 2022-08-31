package com.subway_footprint_system.springboot_project.Dao;

import com.subway_footprint_system.springboot_project.Pojo.AwardRecord;

import java.util.List;

public interface IAwardRecordDao {


    /**
     * 增加奖品流水记录
     * 用户：增加购物车/增加兑换奖品记录
     * 商户：增加藏宝记录等
     */
    boolean insertMysqlAwardRecord(AwardRecord awardRecord);

    //创建redis
    boolean insertRedisAwardRecord(AwardRecord awardRecord);


    /**
     * 删除奖品流水记录
     * 用户：删除购物车某奖品；删除订单
     */
    boolean deleteMysqlAwardRecord(String arid);

    boolean deleteRedisAwardRecord(String arid);

    AwardRecord getMysqlAwardRecord(String arid, int operation);

    AwardRecord getRedisAwardRecord(String arid);

    //更新奖品流水记录
    boolean updateMysqlAwardRecord(AwardRecord awardRecord);

    //获取奖品流水记录(时间倒序，指定组数，一组6个)
    List<AwardRecord> getAwardRecords(int operation, String uid, int group);


}
