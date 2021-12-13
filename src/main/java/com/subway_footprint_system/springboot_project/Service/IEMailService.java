package com.subway_footprint_system.springboot_project.Service;

import com.subway_footprint_system.springboot_project.Pojo.UserVo;

import javax.servlet.http.HttpSession;

public interface IEMailService {

    /**
     * 给前端输入的邮箱，发送验证码
     */
    public boolean sendRegistEmail(String email, HttpSession session);

    /**
     * 随机生成6位数的验证码
     */
    public String randomCode();

    /**
     * 检验验证码是否一致
     */
    public boolean registered(UserVo userVo);
    //发送找回密码提示邮件
    public boolean findPassword_sendEmail(String email);
    //发送更改密码提示邮件
    public boolean changePassword(UserVo userVo);
}