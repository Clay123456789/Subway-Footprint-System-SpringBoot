package com.subway_footprint_system.springboot_project.Dao;

import com.subway_footprint_system.springboot_project.Pojo.LightedStation;

import java.util.List;

public interface ILightedStationDao {
    //增删改查方法
    boolean insertLightedStation(LightedStation lightedStation);

    boolean deleteLightedStation(String uid, String pid);

    LightedStation getLightedStation(String uid, String pid);

    List<LightedStation> getUserLightedStations(String uid);

}
