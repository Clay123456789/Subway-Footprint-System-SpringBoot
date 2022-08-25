package com.subway_footprint_system.springboot_project.Service;

import com.subway_footprint_system.springboot_project.Pojo.Manager;

public interface IManagerService {

    Manager getManagerByManagerId(String managerID);
    Manager getManagerByAccount(String account);
}
