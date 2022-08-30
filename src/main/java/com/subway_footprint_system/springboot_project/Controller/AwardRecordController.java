package com.subway_footprint_system.springboot_project.Controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.subway_footprint_system.springboot_project.Dao.Impl.ResultFactory;
import com.subway_footprint_system.springboot_project.Pojo.AwardRecord;
import com.subway_footprint_system.springboot_project.Pojo.Result;
import com.subway_footprint_system.springboot_project.Service.Impl.AwardRecordServiceImpl;
import com.subway_footprint_system.springboot_project.Utils.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@EnableAutoConfiguration
@RestController
public class AwardRecordController {

  @Autowired private AwardRecordServiceImpl awardRecordService;

  /*
   * 请求方式：post
   * 功能：增加奖品进购物车
   * 路径 /user/insertShoppingAwardRecord
   * 传参(json) aid,num
   * 返回值 (json--Result) code,message,data(str)
   * */
  @CrossOrigin
  @PostMapping(value = "/user/insertShoppingAwardRecord")
  @ResponseBody
  public Result insertShoppingAwardRecord(HttpServletRequest request, String aid, int num) {
    try {
      // 获取请求头中的token令牌
      String token = request.getHeader("token");
      // 根据token解析出uid;
      DecodedJWT decodedJWT = JWTUtil.getTokenInfo(token);
      String uid = decodedJWT.getClaim("uid").asString();
      if (awardRecordService.insertShoppingAwardRecord(aid, uid, num)) {
        return ResultFactory.buildSuccessResult("成功加入购物车！");
      }
      return ResultFactory.buildFailResult("加入购物车失败！");
    } catch (Exception e) {
      e.printStackTrace();
      return ResultFactory.buildFailResult("出现异常！");
    }
  }

  /*
   * 请求方式：post
   * 功能：删除购物车某奖品
   * 路径 /user/deleteShoppingAwardRecord
   * 传参(json) arid
   * 返回值 (json--Result) code,message,data(str)
   * */
  @CrossOrigin
  @PostMapping(value = "/user/deleteShoppingAwardRecord")
  @ResponseBody
  public Result deleteShoppingAwardRecord(HttpServletRequest request, String arid) {
    try {
      // 获取请求头中的token令牌
      String token = request.getHeader("token");
      // 根据token解析出uid;
      DecodedJWT decodedJWT = JWTUtil.getTokenInfo(token);
      String uid = decodedJWT.getClaim("uid").asString();
      if (null != uid
          && uid.equals(awardRecordService.getShoppingAwardRecord(arid).getUid())
          && awardRecordService.deleteShoppingAwardRecord(arid)) {
        return ResultFactory.buildSuccessResult("成功将奖品从购物车删除！");
      }
      return ResultFactory.buildFailResult("删除失败！");
    } catch (Exception e) {
      e.printStackTrace();
      return ResultFactory.buildFailResult("出现异常！");
    }
  }
  /*
   * 请求方式：post
   * 功能：获取购物车某奖品信息
   * 路径 /user/getShoppingAwardRecord
   * 传参(json) arid
   * 返回值 (json--Result) code,message,data(AwardRecord)
   * */
  @CrossOrigin
  @PostMapping(value = "/user/getShoppingAwardRecord")
  @ResponseBody
  public Result getShoppingAwardRecord(HttpServletRequest request, String arid) {
    try {
      // 获取请求头中的token令牌
      String token = request.getHeader("token");
      // 根据token解析出uid;
      DecodedJWT decodedJWT = JWTUtil.getTokenInfo(token);
      String uid = decodedJWT.getClaim("uid").asString();
      AwardRecord awardRecord = awardRecordService.getShoppingAwardRecord(arid);
      if (null != uid && null != awardRecord && uid.equals(awardRecord.getUid())) {
        return ResultFactory.buildSuccessResult(awardRecord);
      }
      return ResultFactory.buildFailResult("获取失败！");
    } catch (Exception e) {
      e.printStackTrace();
      return ResultFactory.buildFailResult("出现异常！");
    }
  }
  /*
   * 请求方式：post
   * 功能：获取某订单信息
   * 路径 /user/getOrderAwardRecord
   * 传参(json) arid
   * 返回值 (json--Result) code,message,data(AwardRecord)
   * */
  @CrossOrigin
  @PostMapping(value = "/user/getOrderAwardRecord")
  @ResponseBody
  public Result getOrderAwardRecord(HttpServletRequest request, String arid) {
    try {
      // 获取请求头中的token令牌
      String token = request.getHeader("token");
      // 根据token解析出uid;
      DecodedJWT decodedJWT = JWTUtil.getTokenInfo(token);
      String uid = decodedJWT.getClaim("uid").asString();
      AwardRecord awardRecord = awardRecordService.getOrderAwardRecord(arid);
      if (null != uid && null != awardRecord && uid.equals(awardRecord.getUid())) {
        return ResultFactory.buildSuccessResult(awardRecord);
      }
      return ResultFactory.buildFailResult("获取失败！");
    } catch (Exception e) {
      e.printStackTrace();
      return ResultFactory.buildFailResult("出现异常！");
    }
  }
  /*
   * 请求方式：post
   * 功能：从购物车奖品创建订单
   * 路径 /user/createOrderAwardRecordByShopping
   * 传参(json) arid
   * 返回值 (json--Result) code,message,data(str)
   * */
  @CrossOrigin
  @PostMapping(value = "/user/createOrderAwardRecordByShopping")
  @ResponseBody
  public Result createOrderAwardRecordByShopping(HttpServletRequest request, String arid) {
    try {
      // 获取请求头中的token令牌
      String token = request.getHeader("token");
      // 根据token解析出uid;
      DecodedJWT decodedJWT = JWTUtil.getTokenInfo(token);
      String uid = decodedJWT.getClaim("uid").asString();
      AwardRecord awardRecord = awardRecordService.getShoppingAwardRecord(arid);
      if (null != uid && null != awardRecord && uid.equals(awardRecord.getUid())) {
        if (awardRecordService.createOrderAwardRecordByShopping(arid)) {
          return ResultFactory.buildSuccessResult("成功创建订单！");
        }
      }
      return ResultFactory.buildFailResult("创建订单失败！");
    } catch (Exception e) {
      e.printStackTrace();
      return ResultFactory.buildFailResult("出现异常！");
    }
  }
  /*
   * 请求方式：post
   * 功能：直接创建订单
   * 路径 /user/createOrderAwardRecord
   * 传参(json) aid,num
   * 返回值 (json--Result) code,message,data(str)
   * */
  @CrossOrigin
  @PostMapping(value = "/user/createOrderAwardRecord")
  @ResponseBody
  public Result createOrderAwardRecord(HttpServletRequest request, String aid, int num) {
    try {
      // 获取请求头中的token令牌
      String token = request.getHeader("token");
      // 根据token解析出uid;
      DecodedJWT decodedJWT = JWTUtil.getTokenInfo(token);
      String uid = decodedJWT.getClaim("uid").asString();
      if (awardRecordService.createOrderAwardRecord(aid, uid, num)) {
        return ResultFactory.buildSuccessResult("成功创建订单！");
      }
      return ResultFactory.buildFailResult("创建订单失败！");
    } catch (Exception e) {
      e.printStackTrace();
      return ResultFactory.buildFailResult("出现异常！");
    }
  }
  /*
   * 请求方式：post
   * 功能：结算订单
   * 路径 /user/finishOrderAwardRecord
   * 传参(json) arid
   * 返回值 (json--Result) code,message,data(str)
   * */
  @CrossOrigin
  @PostMapping(value = "/user/finishOrderAwardRecord")
  @ResponseBody
  public Result finishOrderAwardRecord(HttpServletRequest request, String arid) {
    try {
      // 获取请求头中的token令牌
      String token = request.getHeader("token");
      // 根据token解析出uid;
      DecodedJWT decodedJWT = JWTUtil.getTokenInfo(token);
      String uid = decodedJWT.getClaim("uid").asString();
      AwardRecord awardRecord = awardRecordService.getOrderAwardRecord(arid);
      if (null != uid && null != awardRecord && uid.equals(awardRecord.getUid())) {
        if (awardRecordService.finishOrderAwardRecord(arid)) {
          return ResultFactory.buildSuccessResult("成功结算订单！");
        }
      }
      return ResultFactory.buildFailResult("结算订单失败！");
    } catch (Exception e) {
      e.printStackTrace();
      return ResultFactory.buildFailResult("出现异常！");
    }
  }
  /*
   * 请求方式：post
   * 功能：取消订单
   * 路径 /user/cancelOrderAwardRecord
   * 传参(json) arid
   * 返回值 (json--Result) code,message,data(str)
   * */
  @CrossOrigin
  @PostMapping(value = "/user/cancelOrderAwardRecord")
  @ResponseBody
  public Result cancelOrderAwardRecord(HttpServletRequest request, String arid) {
    try {
      // 获取请求头中的token令牌
      String token = request.getHeader("token");
      // 根据token解析出uid;
      DecodedJWT decodedJWT = JWTUtil.getTokenInfo(token);
      String uid = decodedJWT.getClaim("uid").asString();
      AwardRecord awardRecord = awardRecordService.getOrderAwardRecord(arid);
      if (null != uid && null != awardRecord && uid.equals(awardRecord.getUid())) {
        if (awardRecordService.cancelOrderAwardRecord(arid)) {
          return ResultFactory.buildSuccessResult("成功取消订单！");
        }
      }
      return ResultFactory.buildFailResult("取消订单失败！");
    } catch (Exception e) {
      e.printStackTrace();
      return ResultFactory.buildFailResult("出现异常！");
    }
  }
  /*
   * 请求方式：post
   * 功能：删除订单（不可删除支付中的订单）
   * 路径 /user/deleteOrderAwardRecord
   * 传参(json) arid
   * 返回值 (json--Result) code,message,data(str)
   * */
  @CrossOrigin
  @PostMapping(value = "/user/deleteOrderAwardRecord")
  @ResponseBody
  public Result deleteOrderAwardRecord(HttpServletRequest request, String arid) {
    try {
      // 获取请求头中的token令牌
      String token = request.getHeader("token");
      // 根据token解析出uid;
      DecodedJWT decodedJWT = JWTUtil.getTokenInfo(token);
      String uid = decodedJWT.getClaim("uid").asString();
      AwardRecord awardRecord = awardRecordService.getAnyAwardRecord(arid);
      if (null != uid && null != awardRecord && uid.equals(awardRecord.getUid())) {
        if (awardRecordService.deleteOrderAwardRecord(arid)) {
          return ResultFactory.buildSuccessResult("成功删除订单！");
        }
      }
      return ResultFactory.buildFailResult("删除失败！");
    } catch (Exception e) {
      e.printStackTrace();
      return ResultFactory.buildFailResult("出现异常！");
    }
  }
  /*
   * 请求方式：post
   * 功能：获取用户所有奖品兑换记录
   * 路径 /user/getExchangeAwardRecords
   * 传参(json) <int>group 一组六个
   * 返回值 (json--Result) code,message,data(List<AwardRecord>)
   * */
  @CrossOrigin
  @PostMapping(value = "/user/getExchangeAwardRecords")
  @ResponseBody
  public Result getExchangeAwardRecords(HttpServletRequest request, int group) {
    try {
      // 获取请求头中的token令牌
      String token = request.getHeader("token");
      // 根据token解析出uid;
      DecodedJWT decodedJWT = JWTUtil.getTokenInfo(token);
      String uid = decodedJWT.getClaim("uid").asString();
      if (null != uid) {
        List<AwardRecord> list = awardRecordService.getExchangeAwardRecords(uid, group);
        if (null != list) {
          return ResultFactory.buildSuccessResult(list);
        }
      }
      return ResultFactory.buildFailResult("获取用户奖品兑换记录失败！");
    } catch (Exception e) {
      e.printStackTrace();
      return ResultFactory.buildFailResult("出现异常！");
    }
  }
  /*
   * 请求方式：post
   * 功能：获取用户购物车记录
   * 路径 /user/getShoppingAwardRecords
   * 传参(json) <int>group 一组六个
   * 返回值 (json--Result) code,message,data(List<AwardRecord>)
   * */
  @CrossOrigin
  @PostMapping(value = "/user/getShoppingAwardRecords")
  @ResponseBody
  public Result getShoppingAwardRecords(HttpServletRequest request, int group) {
    try {
      // 获取请求头中的token令牌
      String token = request.getHeader("token");
      // 根据token解析出uid;
      DecodedJWT decodedJWT = JWTUtil.getTokenInfo(token);
      String uid = decodedJWT.getClaim("uid").asString();
      if (null != uid) {
        List<AwardRecord> list = awardRecordService.getShoppingAwardRecords(uid, group);
        if (null != list) {
          return ResultFactory.buildSuccessResult(list);
        }
      }
      return ResultFactory.buildFailResult("获取用户购物车记录失败！");
    } catch (Exception e) {
      e.printStackTrace();
      return ResultFactory.buildFailResult("出现异常！");
    }
  }
  /*
   * 请求方式：post
   * 功能：获取用户所有未支付订单记录
   * 路径 /user/getOrderAwardRecords
   * 传参(json) <int>group 一组六个
   * 返回值 (json--Result) code,message,data(List<AwardRecord>)
   * */
  @CrossOrigin
  @PostMapping(value = "/user/getOrderAwardRecords")
  @ResponseBody
  public Result getOrderAwardRecords(HttpServletRequest request, int group) {
    try {
      // 获取请求头中的token令牌
      String token = request.getHeader("token");
      // 根据token解析出uid;
      DecodedJWT decodedJWT = JWTUtil.getTokenInfo(token);
      String uid = decodedJWT.getClaim("uid").asString();
      if (null != uid) {
        List<AwardRecord> list = awardRecordService.getOrderAwardRecords(uid, group);
        if (null != list) {
          return ResultFactory.buildSuccessResult(list);
        }
      }
      return ResultFactory.buildFailResult("获取用户未支付订单记录失败！");
    } catch (Exception e) {
      e.printStackTrace();
      return ResultFactory.buildFailResult("出现异常！");
    }
  }
  /*
   * 请求方式：post
   * 功能：获取用户所有订单记录
   * 路径 /user/getAllOrderAwardRecords
   * 传参(json) <int>group 一组六个
   * 返回值 (json--Result) code,message,data(List<AwardRecord>)
   * */
  @CrossOrigin
  @PostMapping(value = "/user/getAllOrderAwardRecords")
  @ResponseBody
  public Result getAllOrderAwardRecords(HttpServletRequest request, int group) {
    try {
      // 获取请求头中的token令牌
      String token = request.getHeader("token");
      // 根据token解析出uid;
      DecodedJWT decodedJWT = JWTUtil.getTokenInfo(token);
      String uid = decodedJWT.getClaim("uid").asString();
      if (null != uid) {
        List<AwardRecord> list = awardRecordService.getAllOrderAwardRecords(uid, group);
        if (null != list) {
          return ResultFactory.buildSuccessResult(list);
        }
      }
      return ResultFactory.buildFailResult("获取用户所有订单记录失败！");
    } catch (Exception e) {
      e.printStackTrace();
      return ResultFactory.buildFailResult("出现异常！");
    }
  }
}
