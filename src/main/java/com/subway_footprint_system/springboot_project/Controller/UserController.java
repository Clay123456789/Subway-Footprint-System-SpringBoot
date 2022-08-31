package com.subway_footprint_system.springboot_project.Controller;


import com.auth0.jwt.interfaces.DecodedJWT;
import com.subway_footprint_system.springboot_project.Dao.Impl.ResultFactory;
import com.subway_footprint_system.springboot_project.Pojo.Result;
import com.subway_footprint_system.springboot_project.Pojo.User;
import com.subway_footprint_system.springboot_project.Pojo.UserVo;
import com.subway_footprint_system.springboot_project.Service.Impl.EMailServiceImpl;
import com.subway_footprint_system.springboot_project.Service.Impl.UserServiceImpl;
import com.subway_footprint_system.springboot_project.Utils.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@EnableAutoConfiguration
@RestController
public class UserController {
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private EMailServiceImpl eMailService;

    /*
     * 请求方式：post
     * 功能：登录
     * 路径 /user/login
     * 传参(json) username,password
     * 返回值(json--Result) code,message,data(str)
     * */
    @CrossOrigin
    @RequestMapping(value = "/user/login", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    @ResponseBody
    public Result login(@Valid @RequestBody UserVo userVo, BindingResult bindingResult) {
        if (userVo.getUsername().equals("") || userVo.getPassword().equals("")) {
            String message = String.format("账号或密码不能为空！");
            return ResultFactory.buildFailResult(message);
        }
        if (bindingResult.hasErrors()) {
            String message = String.format("登陆失败，详细信息[%s]。", bindingResult.getFieldError().getDefaultMessage());
            return ResultFactory.buildFailResult(message);
        }
        String uid = null;
        if (!userService.judgeByUsername(userVo)) {
            //用户名不存在，判断是否为Emial登录用户
            userVo.setEmail(userVo.getUsername());
            if (!userService.judgeByEmail(userVo)) {
                String message = String.format("登陆失败，账号/密码信息不正确。");
                return ResultFactory.buildFailResult(message);
            }
            User user = userService.getUserByEmail(userVo.getEmail());
            uid = user.getUid();
        } else {
            uid = userService.getUserByUsername(userVo.getUsername()).getUid();
        }
        //已注册
        Map<String, String> map = new HashMap<>(); //用来存放payload信息
        map.put("uid", uid);
        map.put("email", userVo.getEmail());
        // 生成token令牌
        String token = JWTUtil.generateToken(map);
        return ResultFactory.buildSuccessResult(token);
    }


    /*
     * 请求方式：post
     * 功能：发送注册邮箱
     * 路径 /user/sendRegistEmail
     * 传参(json) email
     * 返回值(json--Result) code,message,data(str)
     * */
    @CrossOrigin
    @PostMapping(value = "/user/sendRegistEmail")
    @ResponseBody
    public Result sendRegistEmail(@Valid @RequestBody UserVo userVo, HttpSession httpSession) {
        /*
         * 使用HttpSession在服务器与浏览器建立对话，以验证邮箱验证码
         * */
        if (!eMailService.sendRegistEmail_user(userVo.getEmail(), httpSession)) {
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

        if (!eMailService.registered_user(userVo)) {
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
    public Result findPassWord(@Valid @RequestBody UserVo userVo) {
        if (!eMailService.findPassword_sendEmail_user(userVo.getEmail())) {
            return ResultFactory.buildFailResult("此邮箱非您注册时使用的邮箱,找回失败！");
        }
        return ResultFactory.buildSuccessResult("找回成功,密码已发送至您的邮箱！");
    }


    /*
     * 请求方式：post
     * 功能：修改用户密码
     * 路径 /user/changePassword
     * 传参(json) email,password,newPassword
     * 返回值(json--Result) code,message,data(Str)
     * */
    @CrossOrigin
    @PostMapping(value = "/user/changePassword")
    @ResponseBody
    public Result changePassword(@Valid @RequestBody UserVo userVo) {
        if (!eMailService.changePassword_user(userVo)) {
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
    @PostMapping(value = "/user/getUser")
    @ResponseBody
    public Result getUser(HttpServletRequest request) {
        try {
            //获取请求头中的token令牌
            String token = request.getHeader("token");
            // 根据token解析出uid;
            DecodedJWT decodedJWT = JWTUtil.getTokenInfo(token);
            String uid = decodedJWT.getClaim("uid").asString();
            return ResultFactory.buildSuccessResult(userService.getUserByUid(uid));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultFactory.buildFailResult("出现异常！");
        }
    }

    /*
     * 请求方式：post
     * 功能：修改用户信息（不包含修改用户uid、密码、和邮箱,以及碳积分）
     * 路径 /user/updateUser
     * 传参(json) （修改后的的User各属性）username,age,sex,tel,touxiang,qianming
     * 返回值 (json--Result) code,message,data(str)
     * */
    @CrossOrigin
    @PostMapping(value = "/user/updateUser")
    @ResponseBody
    public Result updateUser(HttpServletRequest request, @Valid @RequestBody UserVo userVo) {
        try {
            //获取请求头中的token令牌
            String token = request.getHeader("token");
            // 根据token解析出uid;
            DecodedJWT decodedJWT = JWTUtil.getTokenInfo(token);
            String uid = decodedJWT.getClaim("uid").asString();
            userVo.setUid(uid);
            userVo.setCredit(userService.getUserByUid(uid).getCredit());
            if (userService.updateUser(userVo)) {
                return ResultFactory.buildSuccessResult("已成功修个人信息！");
            }
            return ResultFactory.buildFailResult("更改个人信息失败！");
        } catch (Exception e) {
            e.printStackTrace();
            return ResultFactory.buildFailResult("出现异常！");
        }

    }
}
