package com.subway_footprint_system.springboot_project.Dao;

import com.subway_footprint_system.springboot_project.Pojo.LightedStation;

import java.util.List;

public interface ILightedStationDao {
    //用户点亮的站点表是否存在
    boolean TableExist(String uid);
    //增删改查方法
    boolean createLightedStationTable(String uid);
    boolean insertLightedStation(LightedStation lightedStation);
    boolean deleteLightedStationTable(String uid);
    boolean deleteLightedStation(String uid,String pid);
    boolean updateLightedStation(LightedStation lightedStation);
    LightedStation getLightedStation(String uid,String pid);
    List<LightedStation> getUserLightedStations(String uid);

}
