package com.subway_footprint_system.springboot_project.Dao;

import com.subway_footprint_system.springboot_project.Pojo.Treasure;

import java.util.List;

public interface ITreasureDao {
    //增删改查方法
    boolean insertTreasure(Treasure treasure);

    boolean deleteTreasure(String tid);

    boolean updateTreasure(Treasure treasure);

    Treasure getTreasure(String tid);

    List<Treasure> getPositionTreasure(String pid);

    List<Treasure> getAllTreasure();

    List<Treasure> getUserTreasure(String uid2);
}
