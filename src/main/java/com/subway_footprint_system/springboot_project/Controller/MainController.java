package com.subway_footprint_system.springboot_project.Controller;

import com.alibaba.fastjson.JSON;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.subway_footprint_system.springboot_project.Dao.Impl.ResultFactory;
import com.subway_footprint_system.springboot_project.Pojo.*;
import com.subway_footprint_system.springboot_project.Service.Impl.EMailServiceImpl;
import com.subway_footprint_system.springboot_project.Service.Impl.StationServiceImpl;
import com.subway_footprint_system.springboot_project.Service.Impl.SubwayServiceImpl;
import com.subway_footprint_system.springboot_project.Service.Impl.UserServiceImpl;
import com.subway_footprint_system.springboot_project.Utils.JWTUtil;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@EnableAutoConfiguration
@RestController
public class MainController {
        @Autowired
        private StationServiceImpl stationService;
        @Autowired
        private SubwayServiceImpl subwayService;
        @Autowired
        private UserServiceImpl userService;
        @Autowired
        private EMailServiceImpl eMailService;

    @CrossOrigin
    @RequestMapping("/hello")
    public String hello(String username){

        return "Hello,"+username+"!";
    }
    /*测试Station的redis+mysql存储相关代码接口*/
    @CrossOrigin
    @RequestMapping("/addStation")
    public boolean addStation(String sid, String sname, float longitude, float latitude, String route){

        return stationService.insert(new Station(sid,sname,longitude,latitude,route));
    }
    @CrossOrigin
    @RequestMapping("/updateStation")
    public boolean updateStation(String sid, String sname, float longitude, float latitude, String route){

        return stationService.update(new Station(sid,sname,longitude,latitude,route));
    }
    @CrossOrigin
    @RequestMapping("/deleteStation")
    public boolean deleteStation(String sid, String sname, float longitude, float latitude, String route){

        return stationService.delete(new Station(sid,sname,longitude,latitude,route));
    }
    @CrossOrigin
    @RequestMapping("/selectStation")
    public Station selectStation(String sid, String sname, float longitude, float latitude, String route){

        return stationService.select(new Station(sid,sname,longitude,latitude,route));
    }
    @CrossOrigin
    @RequestMapping("/selectAllStation")
    public List<Station> selectAllStation(String sid, String sname, float longitude, float latitude, String route){

        return stationService.selectAll(new Station(sid,sname,longitude,latitude,route));
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
    /*
     * 获取指定/所有城市铁路信息
     * 路径 /Subway/getAllSubways
     * 传参(json) code(0代表所有)
     * 返回值(json) String
     * */
    @CrossOrigin
    @RequestMapping("/getAllSubways")
    public String getAllSubways(int code) throws Exception {
        Map<String, Object> map=null;
        if(code==0){
            map=subwayService.getAllSubways();
        }else{
            map=subwayService.getAllSubways(code);
        }
        if(map==null){
            //return ResultFactory.buildFailResult("发生错误，获取失败");
            return null;
        }else{
            String data=StringEscapeUtils.unescapeJavaScript(JSON.toJSONString(map));
            System.out.println(data);
            return data;
           // return ResultFactory.buildSuccessResult(data);
        }

    }
    /*
     * 上传所有城市铁路信息
     * 路径 /Subway/uploadAllSubways
     * 传参(json) null
     * 返回值(json--Result) code,message,data(boolean)
     * */
    @CrossOrigin
    @RequestMapping("/uploadAllSubways")
    public Result uploadAllSubways() throws Exception {
        if(subwayService.uploadAllSubways()){
            return ResultFactory.buildSuccessResult(true);
        }
        return ResultFactory.buildFailResult("发生错误，上传失败");
    }

    /*
     * 登陆
     * 路径 /user/login
     * 传参(json) uid
     * 返回值(json--Result) code,message,data(str)
     * */
    @CrossOrigin
    @RequestMapping(value = "/user/login", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    @ResponseBody
    public Result login(@Valid @RequestBody UserVo userVo, BindingResult bindingResult) {
        if (userVo.getUsername().equals("")||userVo.getPassword().equals("")) {
            String message = String.format("账号或密码不能为空！");
            return ResultFactory.buildFailResult(message);
        }
        if (bindingResult.hasErrors()) {
            String message = String.format("登陆失败，详细信息[%s]。", bindingResult.getFieldError().getDefaultMessage());
            return ResultFactory.buildFailResult(message);
        }
        String uid=null;
        if (!userService.judgeByUsername(userVo)) {
            //用户名不存在，判断是否为Emial登录用户
                userVo.setEmail(userVo.getUsername());
            if (!userService.judgeByEmail(userVo)) {
                String message = String.format("登陆失败，账号/密码信息不正确。");
                return ResultFactory.buildFailResult(message);
            }
            User user=userService.getUserByEmail(userVo.getEmail());
            uid=user.getUid();
        }
        uid=userService.getUserByUsername(userVo.getUsername()).getUid();
        //已注册
        Map<String, String> map = new HashMap<>(); //用来存放payload信息
        map.put("uid",uid);
        // 生成token令牌
        String token = JWTUtil.generateToken(map);
        return ResultFactory.buildSuccessResult(token);
   }


    /*
     * 发送注册邮箱
     * 路径 /api/sendRegisterEmail
     * 传参(json) email
     * 返回值(json--Result) code,message,data(str)
     * */
    @CrossOrigin
    @PostMapping(value = "/user/sendRegistEmail")
    @ResponseBody
    public Result sendRegistEmail(@Valid @RequestBody UserVo userVo , HttpSession httpSession ) {
        /*
         * 使用HttpSession在服务器与浏览器建立对话，以验证邮箱验证码
         * */
        if (!eMailService.sendRegistEmail(userVo.getEmail(), httpSession)) {
            return ResultFactory.buildFailResult("发送失败！邮箱已注册或不可用");
        }
        return ResultFactory.buildSuccessResult("已发送验证码至邮箱！");
    }

    /*
     * 注册新用户
     * 路径 /user/regist
     * 传参(json) username,password,email,code
     * 返回值(json--Result) code,message,data(str)
     * */
    @CrossOrigin
    @PostMapping(value = "/user/regist")
    @ResponseBody
    public Result regist(@Valid @RequestBody UserVo userVo) {

        if (!eMailService.registered(userVo)) {
            return ResultFactory.buildFailResult("注册失败！验证码不一致");
        }
        return ResultFactory.buildSuccessResult("注册成功！");
    }



    /*
     * 找回密码
     * 路径 /user/findPassword
     * 传参(json) email
     * 返回值(json--Result) code,message,data(str)
     * */
    @CrossOrigin
    @PostMapping(value = "/user/findPassword")
    @ResponseBody
    public Result findPassWord(@Valid @RequestBody UserVo userVo){
        if(!eMailService.findPassword_sendEmail(userVo.getEmail())){
            return ResultFactory.buildFailResult("此邮箱非您注册时使用的邮箱,找回失败！");
        }
        return ResultFactory.buildSuccessResult("找回成功,密码已发送至您的邮箱！");
    }


    /*
     * 修改用户密码
     * 路径 /user/changePassword
     * 传参(json) email,Password,newPassword,newPasswordRepeat
     * 返回值(json--Result) code,message,data(Str)
     * */
    @CrossOrigin
    @PostMapping(value = "/user/changePassword")
    @ResponseBody
    public Result changePassword(@Valid @RequestBody UserVo userVo){
        if(!eMailService.changePassword(userVo)){
            return ResultFactory.buildFailResult("信息有误,修改失败！");
        }
        return ResultFactory.buildSuccessResult("修改密码成功！");
    }

    /*
     * 获取用户头像url
     * 路径 /user/getUserTouxiang
     * 传参(json):username/email
     * 返回值(json--Result) code,message,data(url)
     * 功能：登陆时加载用户头像
     * */
    @CrossOrigin
    @PostMapping(value ="/user/getUserTouxiang")
    @ResponseBody
    public Result getUserTouxiang(@Valid @RequestBody User user){
        return ResultFactory.buildSuccessResult(userService.getUserTouxiang(user.getUsername()));
    }

    /*
     * 获取用户信息
     * 路径 /user/getUser
     * 传参(json):uid/email
     * 返回值(json--Result) code,message,data((json)user)一个完整的User类实例
     * 功能：得到用户信息
     * */
    @CrossOrigin
    @PostMapping(value ="/user/getUser")
    @ResponseBody
    public Result getUser(@Valid @RequestBody User user) {
        if(user.getUid()!=null){
            return ResultFactory.buildSuccessResult(userService.getUserByUid(user.getUid()));
        }else if(user.getEmail()!=null){
            return ResultFactory.buildSuccessResult(userService.getUserByEmail(user.getEmail()));
        }
        return ResultFactory.buildFailResult("获取信息失败！");
    }

    /*
     * 修改用户信息
     * 路径 /user/updateUser
     * 传参(json) uid(定位需要修改的人） #修改属性#(newUsername,newEmail,newTel等等）
     * 返回值 (json--Result) code,message,data(str)
     * */
    @CrossOrigin
    @PostMapping(value ="/user/updateUser")
    @ResponseBody
    public Result updateUser(@Valid @RequestBody UserVo userVo){
        if (!userService.updateUser(userVo)) {
            return ResultFactory.buildFailResult("更改个人信息失败！");
        }
        return ResultFactory.buildSuccessResult("已成功修个人信息！");
    }
}
