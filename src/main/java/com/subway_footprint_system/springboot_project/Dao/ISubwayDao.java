package com.subway_footprint_system.springboot_project.Dao;

import com.google.gson.JsonArray;
import com.subway_footprint_system.springboot_project.Pojo.Subway;

import java.util.List;
import java.util.Map;

public interface ISubwayDao {
    //增删改查方法
    boolean insertSubway(Subway subway);
    boolean deleteSubway(String sid);
    boolean updateSubway(Subway subway);
    Subway getSubway(String sid);
    //获取所有已开通地铁城市地铁信息
    Map<String, Object> getAllSubways();
    //获取所有指定城市地铁信息
    Map<String, Object> getAllSubways(int code);
}
