package com.subway_footprint_system.springboot_project.Service.Impl;

import com.subway_footprint_system.springboot_project.Dao.Impl.ManagerDaoImpl;
import com.subway_footprint_system.springboot_project.Pojo.Manager;
import com.subway_footprint_system.springboot_project.Service.IManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ManagerServiceImpl implements IManagerService {

    @Autowired
    private ManagerDaoImpl managerDao;
    @Override
    public Manager getManagerByManagerId(String managerID) {
        return managerDao.getManagerByManagerId(managerID);
    }

    @Override
    public Manager getManagerByAccount(String account) {
        return managerDao.getManagerByAccount(account);
    }
}
