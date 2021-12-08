package com.subway_footprint_system.springboot_project.Service;

import com.subway_footprint_system.springboot_project.Pojo.Station;

import java.util.List;

public interface IStationService {
    //增删改查方法
    boolean insert(Station station);
    boolean delete(Station station);
    boolean update(Station station);
    Station select(Station station);
    List<Station> selectAll(Station station);

    //其他业务逻辑

}
