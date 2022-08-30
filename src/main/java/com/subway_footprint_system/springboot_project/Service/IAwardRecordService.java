package com.subway_footprint_system.springboot_project.Service;

import com.subway_footprint_system.springboot_project.Pojo.AwardRecord;

import java.util.List;

public interface IAwardRecordService {
  // 增加用户藏宝记录
  boolean addUserBuryAwardRecord(String aid, String uid, int num, int credit);

  // 增加奖品进购物车
  boolean insertShoppingAwardRecord(String aid, String uid, int num);
  // 删除购物车某奖品
  boolean deleteShoppingAwardRecord(String arid);
  // 删除订单
  boolean deleteOrderAwardRecord(String arid);
  // 获取购物车某奖品信息
  AwardRecord getShoppingAwardRecord(String arid);
  // 获取某订单信息
  AwardRecord getOrderAwardRecord(String arid);

  // 获取任意状态的流水记录
  AwardRecord getAnyAwardRecord(String arid);

  // 从购物车奖品创建订单
  boolean createOrderAwardRecordByShopping(String arid);
  // 直接创建订单
  boolean createOrderAwardRecord(String aid, String uid, int num);
  // 订单结算
  boolean finishOrderAwardRecord(String arid);
  // 取消订单
  boolean cancelOrderAwardRecord(String arid);
  // 订单过期
  boolean expireOrder(String arid);
  // 获取用户所有奖品兑换记录
  List<AwardRecord> getExchangeAwardRecords(String uid, int group);
  // 获取用户购物车记录
  List<AwardRecord> getShoppingAwardRecords(String uid, int group);
  // 获取用户所有未支付订单记录
  List<AwardRecord> getOrderAwardRecords(String uid, int group);
  // 获取用户所有订单记录
  List<AwardRecord> getAllOrderAwardRecords(String uid, int group);
}
