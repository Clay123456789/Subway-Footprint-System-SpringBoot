package com.subway_footprint_system.springboot_project.Dao;

import com.subway_footprint_system.springboot_project.Pojo.Merchant;

import java.util.List;

public interface IMerchantDao {
  // 增删改查方法
  boolean insertMerchant(Merchant merchant);

  boolean deleteMerchant(String mid);
  // 更改商户信息（不含mid,password,email)
  boolean updateMerchant(Merchant merchant);

  boolean updateAuthentication(Merchant merchant);
  // 更改password
  boolean changePassword(String mid, String password);

  Merchant getMerchantByMid(String mid);

  Merchant getMerchantByEmail(String email);

  Merchant getMerchantByAccount(String account);

  List<Merchant> getAllMerchants();

  List<Merchant> getAllUnAuthenticatedMerchants();
}
