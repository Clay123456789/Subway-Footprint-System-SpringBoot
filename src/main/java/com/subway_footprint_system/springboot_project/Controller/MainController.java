package com.subway_footprint_system.springboot_project.Controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.subway_footprint_system.springboot_project.Pojo.User;
import com.subway_footprint_system.springboot_project.Utils.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


@EnableAutoConfiguration
@RestController
public class MainController {


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


    @PostMapping(value = "/user/login")
    @CrossOrigin
    @ResponseBody
    public Map<String, String> userLogin(@RequestBody User user) {

        QueryWrapper<User> wrapper = new QueryWrapper<>();

        wrapper.eq("username", user.getUsername());
        wrapper.eq("password",user.getPassword());

        HashMap<String, String> result = new HashMap<>(); // 返回结果信息给前端
        // 数据库中查询用户信息
        /*User one = userService.getOne(wrapper);


        if (one == null){
            result.put("code","401");
            result.put("msg","用户名或密码错误");
        }*/

        Map<String, String> map = new HashMap<>(); //用来存放payload信息

        map.put("id","1");
        map.put("username",user.getUsername());
        map.put("role","student");

        // 生成token令牌
        String token = JWTUtil.generateToken(map);

        // 返回前端token
        result.put("code","200");
        result.put("msg","登录成功");
        result.put("token",token);
        return result;
    }
}
