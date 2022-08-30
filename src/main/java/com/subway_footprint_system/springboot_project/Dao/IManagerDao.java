package com.subway_footprint_system.springboot_project.Dao;

import com.subway_footprint_system.springboot_project.Pojo.Manager;

public interface IManagerDao {
  Manager getManagerByManagerId(String managerID);

  Manager getManagerByAccount(String account);
}
