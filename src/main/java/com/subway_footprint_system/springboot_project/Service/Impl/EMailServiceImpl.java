package com.subway_footprint_system.springboot_project.Service.Impl;

import com.subway_footprint_system.springboot_project.Pojo.MerchantVo;
import com.subway_footprint_system.springboot_project.Pojo.UserVo;
import com.subway_footprint_system.springboot_project.Service.IEMailService;
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
    private UserServiceImpl userService;

    @Autowired
    private MerchantServiceImpl merchantService;

    @Autowired
    private HttpSession httpSession_user, httpSession_merchant;
    //application.properties中已配置的值
    @Value("${spring.mail.username}")
    private String from;

    /**
     * 随机生成6位数的验证码
     */
    @Override
    public String randomCode(){
        StringBuilder str = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            str.append(random.nextInt(10));
        }
        return str.toString();
    }


    /**
     * 给普通用户端输入的邮箱，发送验证码
     */
    @Override
    public boolean sendRegistEmail_user(String email, HttpSession session) {

        //该邮箱已经注册
        if(userService.getUserByEmail(email)!=null){
            return false;
        }
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setSubject("验证码邮件");//主题
            //生成随机数
            String code = randomCode();
            this.httpSession_user =session;
            //将随机数放置到session中
            httpSession_user.setAttribute("email",email);
            httpSession_user.setAttribute("code",code);

            mailMessage.setText("[地铁足迹]验证码"+code+"，用于普通用户注册。泄露有风险，如非本人操作，请忽略本条信息。");//内容

            mailMessage.setTo(email);//发给谁

            mailMessage.setFrom(from);//服务器邮箱

            mailSender.send(mailMessage);//发送
            return  true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 检验普通用户注册时验证码是否一致
     */
    @Override
    public boolean registered_user(UserVo userVo){
        //获取session中的验证信息
        String email = (String) httpSession_user.getAttribute("email");
        String code = (String) httpSession_user.getAttribute("code");

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
        if(null==userVo.getUsername()){
            userVo.setUsername(userVo.getEmail());
        }
        //如果用户名或邮箱已存在，注册失败
        if(null!=userService.getUserByEmail(email)||null!=userService.getUserByUsername(userVo.getUsername())){
            return false;
        }
        //将数据写入数据库
        userService.insertUser(userVo);

        //跳转成功页面
        return true;
    }

    /**
     *普通用户端发送找回密码邮件
     */
    @Override
    public boolean findPassword_sendEmail_user(String email) {
        if(userService.getUserByEmail(email)!=null){
            try {
                SimpleMailMessage mailMessage = new SimpleMailMessage();
                mailMessage.setSubject("找回密码邮件");//主题
                //保存密码
                String password=userService.getUserByEmail(email).getPassword();
                mailMessage.setText("[地铁足迹]您的密码是："+ password+"。阅读后建议删除本邮件。请确认是否本人操作，注意账号安全。");

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

    /**
     *普通用户端发送更改密码邮件
     */
    @Override
    public boolean changePassword_user(UserVo userVo) {
         if(userService.updatePassword(userVo)){
                SimpleMailMessage mailMessage = new SimpleMailMessage();
                mailMessage.setSubject("修改密码邮件");//主题
                //保存密码
                String password=userService.getUserByEmail(userVo.getEmail()).getPassword();
                mailMessage.setText("[地铁足迹]您的密码已修改，请确认是否本人操作，注意账号安全。");

                mailMessage.setTo(userVo.getEmail());//发给谁

                mailMessage.setFrom(from);//服务器邮箱

                mailSender.send(mailMessage);//发送
                return true;
            }
            else
                return false;
    }
    /**
     * 给商户端输入的邮箱，发送验证码
     */
    @Override
    public boolean sendRegistEmail_merchant(String email, HttpSession session) {
        //该邮箱已经注册
        if(merchantService.getMerchantByEmail(email)!=null){
            return false;
        }
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setSubject("验证码邮件");//主题
            //生成随机数
            String code = randomCode();
            this.httpSession_merchant=session;
            //将随机数放置到session中
            httpSession_merchant.setAttribute("email",email);
            httpSession_merchant.setAttribute("code",code);

            mailMessage.setText("[地铁足迹]验证码"+code+"，用于商户端注册。泄露有风险，如非本人操作，请忽略本条信息。");//内容

            mailMessage.setTo(email);//发给谁

            mailMessage.setFrom(from);//服务器邮箱

            mailSender.send(mailMessage);//发送
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 检验商户注册时验证码是否一致
     */
    @Override
    public boolean registered_merchant(MerchantVo merchantVo) {
        //获取session中的验证信息
        String email = (String) httpSession_merchant.getAttribute("email");
        String code = (String) httpSession_merchant.getAttribute("code");

        //获取表单中的提交的验证信息
        String voCode = merchantVo.getCode();

        //如果email数据为空，或者不一致，注册失败
        if (email == null || email.isEmpty()||!email.equals(merchantVo.getEmail())){
            //return "error,请重新注册";
            return false;
        }else if (!code.equals(voCode)){
            //return "error,请重新注册";
            return false;
        }

        //将邮箱作为mid和默认账号
        merchantVo.setMid(merchantVo.getEmail());
        if(null==merchantVo.getAccount()){
            merchantVo.setAccount(merchantVo.getEmail());
        }
        //如果用户名或邮箱已存在，注册失败
        if(null!=merchantService.getMerchantByEmail(email)||null!=merchantService.getMerchantByAccount(merchantVo.getAccount())){
            return false;
        }
        //将数据写入数据库
        merchantService.insertMerchant(merchantVo);

        //跳转成功页面
        return true;
    }

    /**
     * 商户端发送找回密码邮件
     */
    @Override
    public boolean findPassword_sendEmail_merchant(String email) {
        if(merchantService.getMerchantByEmail(email)!=null){
            try {
                SimpleMailMessage mailMessage = new SimpleMailMessage();
                mailMessage.setSubject("找回密码邮件");//主题
                //保存密码
                String password=merchantService.getMerchantByEmail(email).getPassword();
                mailMessage.setText("[地铁足迹]您的密码是："+ password+"。阅读后建议删除本邮件。请确认是否本人操作，注意账号安全。");

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

    /**
    * 商户端发送修改密码邮件
    */
    @Override
    public boolean changePassword_merchant(MerchantVo merchantVo) {
        if(merchantService.updatePassword(merchantVo)){
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setSubject("修改密码邮件");//主题
            //保存密码
            String password=merchantService.getMerchantByEmail(merchantVo.getEmail()).getPassword();
            mailMessage.setText("[地铁足迹]您的密码已修改，请确认是否本人操作，注意账号安全。");

            mailMessage.setTo(merchantVo.getEmail());//发给谁

            mailMessage.setFrom(from);//服务器邮箱

            mailSender.send(mailMessage);//发送
            return true;
        }
        else
            return false;
    }


}
