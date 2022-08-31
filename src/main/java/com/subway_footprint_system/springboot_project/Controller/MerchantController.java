package com.subway_footprint_system.springboot_project.Controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.subway_footprint_system.springboot_project.Dao.Impl.ResultFactory;
import com.subway_footprint_system.springboot_project.Pojo.*;
import com.subway_footprint_system.springboot_project.Service.Impl.*;
import com.subway_footprint_system.springboot_project.Utils.FtpUtil;
import com.subway_footprint_system.springboot_project.Utils.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EnableAutoConfiguration
@RestController
public class MerchantController {
    @Autowired
    private MerchantServiceImpl merchantService;
    @Autowired
    private EMailServiceImpl eMailService;
    @Autowired
    private FtpUtil ftpUtil;
    @Autowired
    private TreasureServiceImpl treasureService;
    @Autowired
    private AwardServiceImpl awardService;
    @Autowired
    private AwardRecordServiceImpl awardRecordService;

    /*
     * 请求方式：post
     * 功能：登录
     * 路径 /merchant/login
     * 传参(json) account,password
     * 返回值(json--Result) code,message,data(str)
     * */
    @CrossOrigin
    @RequestMapping(value = "/merchant/login", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    @ResponseBody
    public Result login(@Valid @RequestBody MerchantVo merchantVo, BindingResult bindingResult) throws ParseException {
        if (merchantVo.getAccount().equals("") || merchantVo.getPassword().equals("")) {
            String message = String.format("账号或密码不能为空！");
            return ResultFactory.buildFailResult(message);
        }
        if (bindingResult.hasErrors()) {
            String message = String.format("登陆失败，详细信息[%s]。", bindingResult.getFieldError().getDefaultMessage());
            return ResultFactory.buildFailResult(message);
        }
        String mid = null;
        if (!merchantService.judgeByAccount(merchantVo)) {
            //账号不存在，判断是否为Emial登录用户
            merchantVo.setEmail(merchantVo.getAccount());
            if (!merchantService.judgeByEmail(merchantVo)) {
                String message = String.format("登陆失败，账号/密码信息不正确。");
                return ResultFactory.buildFailResult(message);
            }
            Merchant merchant = merchantService.getMerchantByEmail(merchantVo.getEmail());
            mid = merchant.getMid();
        } else {
            mid = merchantService.getMerchantByAccount(merchantVo.getAccount()).getMid();
        }
        merchantService.checkAuthentication(mid);
        //已注册
        Map<String, String> map = new HashMap<>(); //用来存放payload信息
        map.put("mid", mid);
        map.put("email", merchantVo.getEmail());
        // 生成token令牌
        String token = JWTUtil.generateToken(map);
        return ResultFactory.buildSuccessResult(token);
    }

    /*
     * 请求方式：post
     * 功能：发送注册邮箱
     * 路径 /merchant/sendRegistEmail
     * 传参(json) email
     * 返回值(json--Result) code,message,data(str)
     * */
    @CrossOrigin
    @PostMapping(value = "/merchant/sendRegistEmail")
    @ResponseBody
    public Result sendRegistEmail(@Valid @RequestBody MerchantVo merchantVo, HttpSession httpSession) {
        /*
         * 使用HttpSession在服务器与浏览器建立对话，以验证邮箱验证码
         * */
        if (!eMailService.sendRegistEmail_merchant(merchantVo.getEmail(), httpSession)) {
            return ResultFactory.buildFailResult("发送失败！邮箱已注册或不可用");
        }
        return ResultFactory.buildSuccessResult("已发送验证码至邮箱！");
    }

    /*
     * 请求方式：post
     * 功能：注册新商户
     * 路径 /merchant/regist
     * 传参(json) account,password,email,code
     * 返回值(json--Result) code,message,data(str)
     * */
    @CrossOrigin
    @PostMapping(value = "/merchant/regist")
    @ResponseBody
    public Result regist(@Valid @RequestBody MerchantVo merchantVo) {

        if (!eMailService.registered_merchant(merchantVo)) {
            return ResultFactory.buildFailResult("注册失败！验证码不一致");
        }
        return ResultFactory.buildSuccessResult("注册成功！");
    }

    /*
     * 请求方式：post
     * 功能：找回商户密码
     * 路径 /merchant/findPassword
     * 传参(json) email
     * 返回值(json--Result) code,message,data(str)
     * */
    @CrossOrigin
    @PostMapping(value = "/merchant/findPassword")
    @ResponseBody
    public Result findPassWord(@Valid @RequestBody MerchantVo merchantVo) {
        if (!eMailService.findPassword_sendEmail_merchant(merchantVo.getEmail())) {
            return ResultFactory.buildFailResult("此邮箱非您注册时使用的邮箱,找回失败！");
        }
        return ResultFactory.buildSuccessResult("找回成功,密码已发送至您的邮箱！");
    }

    /*
     * 请求方式：post
     * 功能：修改商户密码
     * 路径 /merchant/changePassword
     * 传参(json) email,password,newPassword
     * 返回值(json--Result) code,message,data(Str)
     * */
    @CrossOrigin
    @PostMapping(value = "/merchant/changePassword")
    @ResponseBody
    public Result changePassword(@Valid @RequestBody MerchantVo merchantVo) {
        if (!eMailService.changePassword_merchant(merchantVo)) {
            return ResultFactory.buildFailResult("信息有误,修改失败！");
        }
        return ResultFactory.buildSuccessResult("修改密码成功！");
    }

    /*
     * 请求方式：post
     * 功能：获取商户信息
     * 路径 /merchant/getMerchant
     * 传参(json):null
     * 返回值(json--Result) code,message,data(User)一个完整的Merchant类实例
     * */
    @CrossOrigin
    @PostMapping(value = "/merchant/getMerchant")
    @ResponseBody
    public Result getMerchant(HttpServletRequest request) {
        try {
            //获取请求头中的token令牌
            String token = request.getHeader("token");
            // 根据token解析出mid;
            DecodedJWT decodedJWT = JWTUtil.getTokenInfo(token);
            String mid = decodedJWT.getClaim("mid").asString();
            return ResultFactory.buildSuccessResult(merchantService.getMerchantByMid(mid));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultFactory.buildFailResult("出现异常！");
        }
    }

    /*
     * 请求方式：post
     * 功能：修改商户信息（不包含修改商户mid、密码、和邮箱及认证信息）
     * 路径 /merchant/updateMerchant
     * 传参(json) （修改后的商户各属性）account,name,tel,info
     * 返回值 (json--Result) code,message,data(str)
     * */
    @CrossOrigin
    @PostMapping(value = "/merchant/updateMerchant")
    @ResponseBody
    public Result updateMerchant(HttpServletRequest request, @Valid @RequestBody MerchantVo merchantVo) {
        try {
            //获取请求头中的token令牌
            String token = request.getHeader("token");
            // 根据token解析出mid;
            DecodedJWT decodedJWT = JWTUtil.getTokenInfo(token);
            String mid = decodedJWT.getClaim("mid").asString();
            merchantVo.setMid(mid);
            if (merchantService.updateMerchant(merchantVo)) {
                return ResultFactory.buildSuccessResult("已成功修改信息！");
            }
            return ResultFactory.buildFailResult("更改信息失败！");
        } catch (Exception e) {
            e.printStackTrace();
            return ResultFactory.buildFailResult("出现异常！");
        }

    }

    /*
     * 请求方式：post
     * 功能：商户提交认证信息
     * 路径 /merchant/submitAuthentication
     * 传参(json) authentication
     * 返回值 (json--Result) code,message,data(str)
     * */
    @CrossOrigin
    @PostMapping(value = "/merchant/submitAuthentication")
    @ResponseBody
    public Result submitAuthentication(HttpServletRequest request, String authentication) {
        try {
            //获取请求头中的token令牌
            String token = request.getHeader("token");
            // 根据token解析出mid;
            DecodedJWT decodedJWT = JWTUtil.getTokenInfo(token);
            String mid = decodedJWT.getClaim("mid").asString();
            if (null != mid && ftpUtil.isImage(ftpUtil.getFileExtention(authentication))) {
                Merchant merchant = new Merchant();
                merchant.setMid(mid);
                merchant.setAuthentication(authentication);
                if (merchantService.submitAuthentication(merchant)) {
                    return ResultFactory.buildSuccessResult("已成功提交认证信息！");
                }
            }
            return ResultFactory.buildFailResult("提交认证信息失败！");
        } catch (Exception e) {
            e.printStackTrace();
            return ResultFactory.buildFailResult("出现异常！");
        }

    }

    /*
     * 请求方式：post
     * 功能：商户藏宝
     * 路径 /merchant/buryTreasure
     * 传参(json) <String>aid（选择的奖品id） <int>num(藏宝的奖品个数) <int>credit(打开所需碳积分) <String>pid(站点id) <String>message(留言)
     * 返回值 (json--Result) code,message,data(str)
     * */
    @CrossOrigin
    @PostMapping(value = "/merchant/buryTreasure")
    @ResponseBody
    public Result buryTreasure(HttpServletRequest request, String aid, int num, int credit, String pid, String message) {
        try {
            //获取请求头中的token令牌
            String token = request.getHeader("token");
            // 根据token解析出uid;
            DecodedJWT decodedJWT = JWTUtil.getTokenInfo(token);
            String mid = decodedJWT.getClaim("mid").asString();
            String time = JWTUtil.getNowTime();
            Award award = awardService.getAward(aid);
            Treasure treasure = Treasure.builder()
                    .tid(mid + '-' + time)
                    .variety(award.getVariety())
                    .content(award.getContent())
                    .credit(credit)
                    .pid(pid)
                    .fromdate(time)
                    .todate(award.getTodate())
                    .status(0)
                    .mid(mid)
                    .message(message)
                    .build();
            if (!treasureService.insertTreasure(treasure) || !awardRecordService.addMerchantBuryAwardRecord(aid, mid, num, credit)) {
                return ResultFactory.buildFailResult("藏宝失败！");
            }
            return ResultFactory.buildSuccessResult("藏宝成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return ResultFactory.buildFailResult("出现异常！");
        }

    }
    /*
     * 请求方式：post
     * 功能：获取商户的所有藏宝记录
     * 路径 /merchant/getMerchantTreasures
     * 传参(json) null
     * 返回值 (json--Result) code,message,data(str)
     * */

    @CrossOrigin
    @PostMapping(value = "/merchant/getMerchantTreasures")
    @ResponseBody
    public Result getMerchantTreasures(HttpServletRequest request) {
        try {
            //获取请求头中的token令牌
            String token = request.getHeader("token");
            // 根据token解析出uid;
            DecodedJWT decodedJWT = JWTUtil.getTokenInfo(token);
            String mid = decodedJWT.getClaim("mid").asString();
            List<Treasure> list = treasureService.getMerchantTreasures(mid);
            return ResultFactory.buildSuccessResult(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultFactory.buildFailResult("出现异常！");
        }
    }
}
