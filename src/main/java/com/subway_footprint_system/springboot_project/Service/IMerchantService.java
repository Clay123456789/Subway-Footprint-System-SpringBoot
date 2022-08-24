package com.subway_footprint_system.springboot_project.Service;

import com.subway_footprint_system.springboot_project.Pojo.Merchant;
import com.subway_footprint_system.springboot_project.Pojo.MerchantVo;

import java.util.List;

public interface IMerchantService {
    //增
    boolean insertMerchant(MerchantVo merchantVo);
    //删
    boolean deleteMerchant(String mid);

    //改
    boolean updateMerchant(MerchantVo merchantVo);
    //更改密码
    boolean updatePassword(MerchantVo merchantVo);
    //查
    Merchant getMerchantByMid(String mid);
    Merchant getMerchantByEmail(String email);
    Merchant getMerchantByAccount(String account);
    //根据mid/account/email及密码判断用户是否存在
    boolean judgeByMid(MerchantVo merchantVo);
    boolean judgeByAccount(MerchantVo merchantVo);
    boolean judgeByEmail(MerchantVo merchantVo);
    List<Merchant> getAllMerchants();
}
