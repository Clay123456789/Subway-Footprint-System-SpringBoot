package com.subway_footprint_system.springboot_project.Controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.subway_footprint_system.springboot_project.Dao.Impl.ResultFactory;
import com.subway_footprint_system.springboot_project.Pojo.*;
import com.subway_footprint_system.springboot_project.Utils.JWTUtil;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 *
 * 在这可以写一些测试接口
 *
 */
@EnableAutoConfiguration
@RestController
public class TestController {

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
    @Autowired
    private StringEncryptor encryptor;

    /**
     * 手动生成密文
     * @param str 原文
     * @return Result data里为密文
     */
    @CrossOrigin
    @RequestMapping("/encrypt")
    public Result encrypt(String str){
        String s=encryptor.encrypt(str);
        System.out.println("密文：" +s );
        return ResultFactory.buildSuccessResult(s);
    }

}
