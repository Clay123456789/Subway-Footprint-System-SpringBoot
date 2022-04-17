package com.subway_footprint_system.springboot_project.Service;

import com.subway_footprint_system.springboot_project.Pojo.LightedStation;

import java.util.List;

public interface ILightedStationService {
    //增删改查方法
    boolean insertLightedStation(LightedStation lightedStation);
    boolean deleteLightedStation(String uid,String pid);
    boolean updateLightedStation(LightedStation lightedStation);
    LightedStation getLightedStation(String uid,String pid);
    List<LightedStation> getUserLightedStations(String uid);
}
