package com.subway_footprint_system.springboot_project.Controller;

import com.alibaba.fastjson.JSON;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.subway_footprint_system.springboot_project.Dao.Impl.ResultFactory;
import com.subway_footprint_system.springboot_project.Pojo.*;
import com.subway_footprint_system.springboot_project.Service.Impl.*;
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
        private SubwayServiceImpl subwayService;
        @Autowired
        private UserServiceImpl userService;
        @Autowired
        private EMailServiceImpl eMailService;
        @Autowired
        private LightedStationServiceImpl lightedStationService;

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
    /*
     * 请求方式：get
     * 功能：获取指定/所有城市铁路信息
     * 路径 /Subway/getAllSubways
     * 传参(json) code(0代表所有)
     * 返回值(json) String（用Result封装json中会有转义符干扰，故此接口直接返回结果）
     * */
    @CrossOrigin
    @RequestMapping("/Subway/getAllSubways")
    public String getAllSubways(int code) throws Exception {
        Map<String, Object> map=null;
        if(code==0){
            map=subwayService.getAllSubways();
        }else{
            map=subwayService.getAllSubways(code);
        }
        if(map==null){
           // return ResultFactory.buildFailResult("发生错误，获取失败");
            return null;
        }else{
            String data=StringEscapeUtils.unescapeJavaScript(JSON.toJSONString(map));
            System.out.println(data);
            return data;
            //return ResultFactory.buildSuccessResult(data);
        }

    }
    /*
     * 请求方式：get
     * 功能：上传所有城市铁路信息
     * 路径 /Subway/uploadAllSubways
     * 传参(json) null
     * 返回值(json--Result) code,message,data(boolean)
     * */
    @CrossOrigin
    @RequestMapping("/Subway/uploadAllSubways")
    public Result uploadAllSubways() throws Exception {
        if(subwayService.uploadAllSubways()){
            return ResultFactory.buildSuccessResult(true);
        }
        return ResultFactory.buildFailResult("发生错误，上传失败");
    }

    /*
     * 请求方式：post
     * 功能：登陆
     * 路径 /user/login
     * 传参(json) username,password
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
        map.put("email",userVo.getEmail());
        // 生成token令牌
        String token = JWTUtil.generateToken(map);
        return ResultFactory.buildSuccessResult(token);
   }


    /*
     * 请求方式：post
     * 功能：发送注册邮箱
     * 路径 /user/sendRegisterEmail
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
     * 请求方式：post
     * 功能：注册新用户
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
     * 请求方式：post
     * 功能：找回密码
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
     * 请求方式：post
     * 功能：修改用户密码
     * 路径 /user/changePassword
     * 传参(json) email,Password,newPassword
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
     * 请求方式：post
     * 功能：获取用户信息
     * 路径 /user/getUser
     * 传参(json):null
     * 返回值(json--Result) code,message,data(User)一个完整的User类实例
     * */
    @CrossOrigin
    @PostMapping(value ="/user/getUser")
    @ResponseBody
    public Result getUser(HttpServletRequest request) {
        try {
            //获取请求头中的token令牌
            String token = request.getHeader("token");
            // 根据token解析出uid;
            DecodedJWT decodedJWT = JWTUtil.getTokenInfo(token);
            String uid = decodedJWT.getClaim("uid").asString();
            return ResultFactory.buildSuccessResult(userService.getUserByUid(uid));
        }catch (Exception e){
            return ResultFactory.buildFailResult("登陆状态异常！");
        }
    }

    /*
     * 请求方式：post
     * 功能：修改用户信息（不包含修改用户uid、密码、和邮箱）
     * 路径 /user/updateUser
     * 传参(json) （修改后的的User各属性）username,age,sex,tel,touxiang,qianming,credit
     * 返回值 (json--Result) code,message,data(str)
     * */
    @CrossOrigin
    @PostMapping(value ="/user/updateUser")
    @ResponseBody
    public Result updateUser(HttpServletRequest request,@Valid @RequestBody UserVo userVo){
        try {
            //获取请求头中的token令牌
            String token = request.getHeader("token");
            // 根据token解析出uid;
            DecodedJWT decodedJWT = JWTUtil.getTokenInfo(token);
            String uid = decodedJWT.getClaim("uid").asString();
            userVo.setUid(uid);
            if (userService.updateUser(userVo)) {
                return ResultFactory.buildSuccessResult("已成功修个人信息！");
            }
            return ResultFactory.buildFailResult("更改个人信息失败！");
        }catch (Exception e){
            return ResultFactory.buildFailResult("登陆状态异常！");
        }

    }
    /*
     * 请求方式：post
     * 功能：用户新增点亮站点
     * 路径 /user/addLightedStation
     * 传参(json) pid(站点id) credit(点亮获得碳积分) time(时间)
     * 返回值 (json--Result) code,message,data(str)
     * */
    @CrossOrigin
    @PostMapping(value ="/user/addLightedStation")
    @ResponseBody
    public Result addLightedStation(HttpServletRequest request,@Valid @RequestBody LightedStation lightedStation){
        try {
            //获取请求头中的token令牌
            String token = request.getHeader("token");
            // 根据token解析出uid;
            DecodedJWT decodedJWT = JWTUtil.getTokenInfo(token);
            String uid = decodedJWT.getClaim("uid").asString();
            lightedStation.setUid(uid);
            if (!lightedStationService.insertLightedStation(lightedStation)) {
                return ResultFactory.buildFailResult("新增点亮站点失败！");
            }
            return ResultFactory.buildSuccessResult("已成功新增点亮站点！");
        }catch (Exception e){
            return ResultFactory.buildFailResult("登陆状态异常！");
        }

    }
    /*
     * 请求方式：post
     * 功能：用户删除点亮站点
     * 路径 /user/deleteLightedStation
     * 传参(json) pid(站点id)
     * 返回值 (json--Result) code,message,data(str)
     * */
    @CrossOrigin
    @PostMapping(value ="/user/deleteLightedStation")
    @ResponseBody
    public Result deleteLightedStation(HttpServletRequest request,@Valid @RequestBody LightedStation lightedStation){
        try {
            //获取请求头中的token令牌
            String token = request.getHeader("token");
            // 根据token解析出uid;
            DecodedJWT decodedJWT = JWTUtil.getTokenInfo(token);
            String uid = decodedJWT.getClaim("uid").asString();
            lightedStation.setUid(uid);
            if (!lightedStationService.deleteLightedStation(lightedStation.getUid(),lightedStation.getPid())) {
                return ResultFactory.buildFailResult("删除点亮站点失败！");
            }
            return ResultFactory.buildSuccessResult("已成功删除点亮站点！");
        }catch (Exception e){
            return ResultFactory.buildFailResult("登陆状态异常！");
        }
    }
    /*
     * 请求方式：post
     * 功能：用户修改点亮站点
     * 路径 /user/updateLightedStation
     * 传参(json) pid(站点id) point(积分) time(时间)
     * 返回值 (json--Result) code,message,data(str)
     * */
    @CrossOrigin
    @PostMapping(value ="/user/updateLightedStation")
    @ResponseBody
    public Result updateLightedStation(HttpServletRequest request,@Valid @RequestBody LightedStation lightedStation){
        try {
            //获取请求头中的token令牌
            String token = request.getHeader("token");
            // 根据token解析出uid;
            DecodedJWT decodedJWT = JWTUtil.getTokenInfo(token);
            String uid = decodedJWT.getClaim("uid").asString();
            lightedStation.setUid(uid);
            if (!lightedStationService.updateLightedStation(lightedStation)) {
                return ResultFactory.buildFailResult("修改点亮站点失败！");
            }
            return ResultFactory.buildSuccessResult("已成功修改点亮站点！");
        }catch (Exception e){
            return ResultFactory.buildFailResult("登陆状态异常！");
        }

    }
    /*
     * 请求方式：post
     * 功能：用户获取指定点亮站点信息
     * 路径 /user/getLightedStation
     * 传参(json) pid(站点id)
     * 返回值 (json--Result) code,message,data(LightedStation)
     * */
    @CrossOrigin
    @PostMapping(value ="/user/getLightedStation")
    @ResponseBody
    public Result getLightedStation(HttpServletRequest request,@Valid @RequestBody LightedStation lightedStation){
        try {
            //获取请求头中的token令牌
            String token = request.getHeader("token");
            // 根据token解析出uid;
            DecodedJWT decodedJWT = JWTUtil.getTokenInfo(token);
            String uid = decodedJWT.getClaim("uid").asString();
            lightedStation.setUid(uid);
            LightedStation lightedStation1=lightedStationService.getLightedStation(lightedStation.getUid(),lightedStation.getPid());
            if (lightedStation1==null) {
                return ResultFactory.buildFailResult("获取点亮站点失败！");
            }
            return ResultFactory.buildSuccessResult(lightedStation1);
        }catch (Exception e){
            return ResultFactory.buildFailResult("登陆状态异常！");
        }
    }
    /*
     * 请求方式：post
     * 功能：用户获取个人全部点亮站点信息
     * 路径 /user/getUserLightedStations
     * 传参(json) null
     * 返回值 (json--Result) code,message,data(List<LightedStation>)
     * */
    @CrossOrigin
    @PostMapping(value ="/user/getUserLightedStations")
    @ResponseBody
    public Result getUserLightedStations(HttpServletRequest request,@Valid @RequestBody LightedStation lightedStation){
        try {
            //获取请求头中的token令牌
            String token = request.getHeader("token");
            // 根据token解析出uid;
            DecodedJWT decodedJWT = JWTUtil.getTokenInfo(token);
            String uid = decodedJWT.getClaim("uid").asString();
            lightedStation.setUid(uid);
            List<LightedStation> list=lightedStationService.getUserLightedStations(lightedStation.getUid());
            if (list==null) {
                return ResultFactory.buildFailResult("获取点亮站点失败！");
            }
            return ResultFactory.buildSuccessResult(list);
        }catch (Exception e){
            return ResultFactory.buildFailResult("登陆状态异常！");
        }
    }
}
