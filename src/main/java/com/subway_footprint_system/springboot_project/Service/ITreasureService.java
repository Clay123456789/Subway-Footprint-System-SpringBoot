package com.subway_footprint_system.springboot_project.Service;

import com.subway_footprint_system.springboot_project.Pojo.Treasure;

import java.util.List;

public interface ITreasureService {
    //增删改查方法
    boolean insertTreasure(Treasure treasure);

    boolean deleteTreasure(String tid);

    boolean updateTreasure(Treasure treasure);

    Treasure getTreasure(String tid);

    List<Treasure> getPositionTreasures(String pid);

    List<Treasure> getAllTreasures();

    List<Treasure> getUserTreasures(String uid);

    List<Treasure> getMerchantTreasures(String mid);
}
