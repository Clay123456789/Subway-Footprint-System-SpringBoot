package com.subway_footprint_system.springboot_project.Service;

import com.subway_footprint_system.springboot_project.Pojo.Subway;

import java.net.MalformedURLException;
import java.util.Map;

public interface ISubwayService {
  // 增删改查方法
  boolean insertSubway(Subway subway);

  boolean deleteSubway(String sid);

  boolean updateSubway(Subway subway);

  Subway getSubway(String sid);
  // 获取所有已开通地铁城市地铁信息
  Map<String, Object> getAllSubways();
  // 获取所有指定城市地铁信息
  Map<String, Object> getAllSubways(int code);

  // 其他业务逻辑
  // 上传所有所有已开通地铁城市地铁信息，数据来源：百度
  boolean uploadAllSubways() throws MalformedURLException, Exception;
}
