package com.subway_footprint_system.springboot_project.Service.Impl;

import com.subway_footprint_system.springboot_project.Dao.Impl.LightedStationDaoImpl;
import com.subway_footprint_system.springboot_project.Pojo.LightedStation;
import com.subway_footprint_system.springboot_project.Service.ILightedStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LightedStationServiceImpl implements ILightedStationService {
    @Autowired
    private LightedStationDaoImpl lightedStationDao;

    @Override
    public boolean insertLightedStation(LightedStation lightedStation) {
        return lightedStationDao.insertLightedStation(lightedStation);
    }

    @Override
    public boolean deleteLightedStation(String uid, String pid) {
        return lightedStationDao.deleteLightedStation(uid,pid);
    }

    @Override
    public LightedStation getLightedStation(String uid, String pid) {
        return lightedStationDao.getLightedStation(uid,pid);
    }

    @Override
    public List<LightedStation> getUserLightedStations(String uid) {
        return lightedStationDao.getUserLightedStations(uid);
    }
}
