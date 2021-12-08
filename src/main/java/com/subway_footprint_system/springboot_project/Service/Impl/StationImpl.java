package com.subway_footprint_system.springboot_project.Service.Impl;

import com.subway_footprint_system.springboot_project.Dao.Impl.StationDaoImpl;
import com.subway_footprint_system.springboot_project.Pojo.Station;
import com.subway_footprint_system.springboot_project.Service.IStationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class StationImpl implements IStationService {
    @Autowired
    private StationDaoImpl stationDao;
    @Override
    public boolean insert(Station station) {

        return stationDao.insert(station);
    }

    @Override
    public boolean delete(Station station) {
        return stationDao.delete(station);
    }

    @Override
    public boolean update(Station station) {
        return stationDao.update(station);
    }

    @Override
    public Station select(Station station) {
        return stationDao.select(station);
    }

    @Override
    public List<Station> selectAll(Station station) {
        return stationDao.selectAll(station);
    }
}
