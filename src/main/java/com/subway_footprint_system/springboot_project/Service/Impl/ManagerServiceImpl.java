package com.subway_footprint_system.springboot_project.Service.Impl;

import com.subway_footprint_system.springboot_project.Dao.Impl.ManagerDaoImpl;
import com.subway_footprint_system.springboot_project.Dao.Impl.MerchantDaoImpl;
import com.subway_footprint_system.springboot_project.Pojo.Manager;
import com.subway_footprint_system.springboot_project.Pojo.Merchant;
import com.subway_footprint_system.springboot_project.Service.IManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ManagerServiceImpl implements IManagerService {

    @Autowired
    private ManagerDaoImpl managerDao;
    @Autowired
    private MerchantDaoImpl merchantDao;
    @Override
    public Manager getManagerByManagerId(String managerID) {
        return managerDao.getManagerByManagerId(managerID);
    }

    @Override
    public Manager getManagerByAccount(String account) {
        return managerDao.getManagerByAccount(account);
    }

    @Override
    public List<Merchant> getAllMerchants() {
        return merchantDao.getAllMerchants();
    }

    @Override
    public List<Merchant> getAllUnAuthenticatedMerchants() {
        return merchantDao.getAllUnAuthenticatedMerchants();
    }

    @Override
    public boolean checkAuthentication(String mid, boolean isApproved) {
        Merchant merchant=merchantDao.getMerchantByMid(mid);
        if(null!=merchant&&0==merchant.getAuthenticated()){
            merchant.setAuthenticated(isApproved?1:-2);
            return merchantDao.updateAuthentication(merchant);
        }
        return false;
    }

}
