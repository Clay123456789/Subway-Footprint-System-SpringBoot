package com.subway_footprint_system.springboot_project.Dao;

import com.subway_footprint_system.springboot_project.Pojo.Station;

import java.util.List;

public interface IStationDao {
    //增删改查方法
    boolean insert(Station station);
    boolean delete(Station station);
    boolean update(Station station);
    Station select(Station station);
    List<Station> selectAll(Station station);
}
