package com.subway_footprint_system.springboot_project.Service;

import com.subway_footprint_system.springboot_project.Pojo.Manager;
import com.subway_footprint_system.springboot_project.Pojo.Merchant;

import java.util.List;

public interface IManagerService {

    Manager getManagerByManagerId(String managerID);

    Manager getManagerByAccount(String account);

    //获取所有注册的商户
    List<Merchant> getAllMerchants();

    //获取认证中的商户
    List<Merchant> getAllUnAuthenticatedMerchants();

    //对商户认证
    boolean checkAuthentication(String mid, boolean isApproved);
}
