package com.subway_footprint_system.springboot_project.Service;

import com.subway_footprint_system.springboot_project.Pojo.MerchantVo;
import com.subway_footprint_system.springboot_project.Pojo.UserVo;

import javax.servlet.http.HttpSession;

public interface IEMailService {

    /**
     * 随机生成6位数的验证码
     */
    public String randomCode();

    /**
     * 给普通用户端输入的邮箱，发送验证码
     */
    public boolean sendRegistEmail_user(String email, HttpSession session);

    /**
     * 检验普通用户注册时验证码是否一致
     */
    public boolean registered_user(UserVo userVo);

    /**
     * 普通用户端发送找回密码邮件
     */
    public boolean findPassword_sendEmail_user(String email);

    /**
     * 普通用户端发送更改密码邮件
     */
    public boolean changePassword_user(UserVo userVo);

    /**
     * 给商户端输入的邮箱，发送验证码
     */
    public boolean sendRegistEmail_merchant(String email, HttpSession session);

    /**
     * 检验商户注册时验证码是否一致
     */
    public boolean registered_merchant(MerchantVo merchantVo);

    /**
     * 商户端发送找回密码邮件
     */
    public boolean findPassword_sendEmail_merchant(String email);

    /**
     * 商户端发送更改密码邮件
     */
    public boolean changePassword_merchant(MerchantVo merchantVo);


}