package com.subway_footprint_system.springboot_project.Controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.subway_footprint_system.springboot_project.Dao.Impl.ResultFactory;
import com.subway_footprint_system.springboot_project.Pojo.*;
import com.subway_footprint_system.springboot_project.Service.Impl.*;
import com.subway_footprint_system.springboot_project.Utils.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

/**
 *
 * 在这可以写一些测试接口
 *
 */
@EnableAutoConfiguration
@RestController
public class TestController {


    @Autowired
    private TreasureServiceImpl treasureService;

    /*
     * 请求方式：post
     * 功能：用户/商户藏宝
     * 路径 /user/buryTreasure
     * 传参(json) variety(宝箱种类) content(宝箱内容) credit(打开所需碳积分) pid(站点id)
     * 返回值 (json--Result) code,message,data(str)
     * */
    @CrossOrigin
    @PostMapping(value ="/user/buryTreasure")
    @ResponseBody
    public Result addLightedStation(HttpServletRequest request, @Valid @RequestBody Treasure treasure){
        try {
            //获取请求头中的token令牌
            String token = request.getHeader("token");
            // 根据token解析出uid;
            DecodedJWT decodedJWT = JWTUtil.getTokenInfo(token);
            String uid = decodedJWT.getClaim("uid").asString();
            treasure.setUid(uid);
            String time=JWTUtil.getNowTime();
            treasure.setFromdate(time);
            treasure.setTid(uid+'-'+time);
            treasure.setStatus(0);
            if(!treasureService.insertTreasure(treasure)){
                return ResultFactory.buildFailResult("藏宝失败！");
            }
            return ResultFactory.buildFailResult("藏宝成功！");

        }catch (Exception e){
            e.printStackTrace();
            return ResultFactory.buildFailResult("登陆状态异常！");
        }

    }

    @CrossOrigin
    @RequestMapping("/hello")
    public String hello(String username){

        return "Hello,"+username+"!";
    }

    @PostMapping("/test")
    @CrossOrigin
    @ResponseBody
    public Map<String,String> test(@RequestBody Object object, HttpServletRequest request){

        Map<String,String> map = new HashMap<>();
        //处理自己业务逻辑
        String token = request.getHeader("token");
        DecodedJWT claims = JWTUtil.getTokenInfo(token);
        map.put("code","200");
        map.put("msg","请求成功!");
        map.put("data",object.toString());
        return map;
    }
}
