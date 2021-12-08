package com.subway_footprint_system.springboot_project.Controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.subway_footprint_system.springboot_project.Dao.Impl.StationDaoImpl;
import com.subway_footprint_system.springboot_project.Pojo.Station;
import com.subway_footprint_system.springboot_project.Pojo.User;
import com.subway_footprint_system.springboot_project.Utils.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


@EnableAutoConfiguration
@RestController
public class MainController {
        @Autowired
        private StationDaoImpl StationDao;

    @CrossOrigin
    @RequestMapping("/hello")
    public String hello(String username){

        return "Hello,"+username+"!";
    }
    /*测试Station的redis+mysql存储相关代码接口*/
    @CrossOrigin
    @RequestMapping("/addStation")
    public boolean addStation(String SID, String SName, float Longitude, float Latitude, String Route){

        return StationDao.insert(new Station(SID,SName,Longitude,Latitude,Route));
    }
    @CrossOrigin
    @RequestMapping("/updateStation")
    public boolean updateStation(String SID, String SName, float Longitude, float Latitude, String Route){

        return StationDao.update(new Station(SID,SName,Longitude,Latitude,Route));
    }
    @CrossOrigin
    @RequestMapping("/deleteStation")
    public boolean deleteStation(String SID, String SName, float Longitude, float Latitude, String Route){

        return StationDao.delete(new Station(SID,SName,Longitude,Latitude,Route));
    }
    @CrossOrigin
    @RequestMapping("/selectStation")
    public String selectStation(String SID, String SName, float Longitude, float Latitude, String Route){

        return (StationDao.select(new Station(SID,SName,Longitude,Latitude,Route))).toString();
    }
    @CrossOrigin
    @RequestMapping("/selectAllStation")
    public String selectAllStation(String SID, String SName, float Longitude, float Latitude, String Route){

        return (StationDao.selectAll(new Station(SID,SName,Longitude,Latitude,Route))).toString();
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

        wrapper.eq("username", user.getAccount());
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
        map.put("username",user.getAccount());
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
