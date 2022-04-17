package com.subway_footprint_system.springboot_project.Service.Impl;

import com.subway_footprint_system.springboot_project.Pojo.User;
import com.subway_footprint_system.springboot_project.Pojo.UserVo;
import com.subway_footprint_system.springboot_project.Service.IEMailService;
import com.subway_footprint_system.springboot_project.Service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Random;

@Service
public class EMailServiceImpl implements IEMailService {
    @Autowired
    private JavaMailSender mailSender;//一定要用@Autowired

    @Autowired
    private IUserService userService;

    @Autowired
    private HttpSession httpSession;
    //application.properties中已配置的值
    @Value("${spring.mail.username}")
    private String from;

    /**
     * 给前端输入的邮箱，发送验证码
     */
    public boolean sendRegistEmail(String email, HttpSession session) {

        //该邮箱已经注册
        if(userService.getUserByEmail(new User(email).getEmail())!=null){
            return false;
        }
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setSubject("验证码邮件");//主题
            //生成随机数
            String code = randomCode();
            this.httpSession=session;
            //将随机数放置到session中
            httpSession.setAttribute("email",email);
            httpSession.setAttribute("code",code);

            mailMessage.setText("您收到的验证码是："+code);//内容

            mailMessage.setTo(email);//发给谁

            mailMessage.setFrom(from);//你自己的邮箱

            mailSender.send(mailMessage);//发送
            return  true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 随机生成6位数的验证码
     */
    public String randomCode(){
        StringBuilder str = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            str.append(random.nextInt(10));
        }
        return str.toString();
    }

    /**
     * 检验验证码是否一致
     */
    public boolean registered(UserVo userVo){
        //获取session中的验证信息
        String email = (String) httpSession.getAttribute("email");
        String code = (String) httpSession.getAttribute("code");

        //获取表单中的提交的验证信息
        String voCode = userVo.getCode();

        //如果email数据为空，或者不一致，注册失败
        if (email == null || email.isEmpty()||!email.equals(userVo.getEmail())){
            //return "error,请重新注册";
            return false;
        }else if (!code.equals(voCode)){
            //return "error,请重新注册";
            return false;
        }
        //将邮箱作为uid和默认用户名
        userVo.setUid(userVo.getEmail());
        userVo.setUsername(userVo.getEmail());
        //将数据写入数据库
        userService.insertUser(userVo);

        //跳转成功页面
        return true;
    }
    @Override
    public boolean findPassword_sendEmail(String email) {
        if(userService.getUserByEmail(new User(email).getEmail())!=null){
            try {
                SimpleMailMessage mailMessage = new SimpleMailMessage();
                mailMessage.setSubject("找回密码邮件");//主题
                //保存密码
                String password=userService.getUserByEmail(new User(email).getEmail()).getPassword();
                mailMessage.setText("您的密码是："+ password);

                mailMessage.setTo(email);//发给谁

                mailMessage.setFrom(from);//服务器邮箱

                mailSender.send(mailMessage);//发送
                return  true;
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }
        else
            return false;
    }

    @Override
    public boolean changePassword(UserVo userVo) {
         if(userService.updatePassword(userVo)){
                SimpleMailMessage mailMessage = new SimpleMailMessage();
                mailMessage.setSubject("修改密码邮件");//主题
                //保存密码
                String password=userService.getUserByEmail(userVo.getEmail()).getPassword();
                mailMessage.setText("您的账号已修改了密码，请确认是否本人所为，注意账号安全");

                mailMessage.setTo(userVo.getEmail());//发给谁

                mailMessage.setFrom(from);//服务器邮箱

                mailSender.send(mailMessage);//发送
                return true;
            }
            else
                return false;
    }



}
