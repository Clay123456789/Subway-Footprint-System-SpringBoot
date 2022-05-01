package com.subway_footprint_system.springboot_project.Controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.subway_footprint_system.springboot_project.Dao.Impl.ResultFactory;
import com.subway_footprint_system.springboot_project.Pojo.*;
import com.subway_footprint_system.springboot_project.Service.Impl.TreasureServiceImpl;
import com.subway_footprint_system.springboot_project.Utils.FtpUtil;
import com.subway_footprint_system.springboot_project.Utils.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 *
 * 在这可以写一些测试接口
 *
 */
@Slf4j
@EnableAutoConfiguration
@RestController
public class TestController {

    @Autowired
    private StringEncryptor encryptor;
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

    /**
     * 手动生成密文
     * @param str 原文
     * @return Result data里为密文
     */
    @CrossOrigin
    @RequestMapping("/encrypt")
    public Result encrypt(String str){
        String s=encryptor.encrypt(str);
        log.info("密文：" +s );
        //System.out.println("密文：" +s );
        System.out.println("原文：" +encryptor.decrypt(s) );
        return ResultFactory.buildSuccessResult(s);
    }

}
