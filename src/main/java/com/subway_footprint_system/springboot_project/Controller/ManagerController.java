package com.subway_footprint_system.springboot_project.Controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.subway_footprint_system.springboot_project.Dao.Impl.ResultFactory;
import com.subway_footprint_system.springboot_project.Pojo.Manager;
import com.subway_footprint_system.springboot_project.Pojo.Result;
import com.subway_footprint_system.springboot_project.Pojo.User;
import com.subway_footprint_system.springboot_project.Pojo.UserVo;
import com.subway_footprint_system.springboot_project.Service.Impl.ManagerServiceImpl;
import com.subway_footprint_system.springboot_project.Service.Impl.MerchantServiceImpl;
import com.subway_footprint_system.springboot_project.Utils.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@EnableAutoConfiguration
@RestController
public class ManagerController {
    @Autowired
    private ManagerServiceImpl managerService;
    @Autowired
    private MerchantServiceImpl merchantService;
    /*
     * 请求方式：post
     * 功能：登录
     * 路径 /manager/login
     * 传参(json) account,password
     * 返回值(json--Result) code,message,data(str)
     * */
    @CrossOrigin
    @RequestMapping(value = "/manager/login", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    @ResponseBody
    public Result login(@Valid @RequestBody Manager manager, BindingResult bindingResult) {
        if (manager.getAccount().equals("")||manager.getPassword().equals("")) {
            String message = String.format("账号或密码不能为空！");
            return ResultFactory.buildFailResult(message);
        }
        if (bindingResult.hasErrors()) {
            String message = String.format("登陆失败，详细信息[%s]。", bindingResult.getFieldError().getDefaultMessage());
            return ResultFactory.buildFailResult(message);
        }
        Manager manager1=managerService.getManagerByAccount(manager.getAccount());
        if(null!=manager1&&manager1.getPassword().equals(manager.getPassword())){
            //已注册
            Map<String, String> map = new HashMap<>(); //用来存放payload信息
            map.put("managerID",manager1.getManagerID());
            map.put("account",manager1.getAccount());
            // 生成token令牌
            String token = JWTUtil.generateToken(map);
            return ResultFactory.buildSuccessResult(token);
        }
        String message = String.format("登陆失败，账号/密码信息不正确。");
        return ResultFactory.buildFailResult(message);

    }



    /*
     * 请求方式：post
     * 功能：管理员审核商户认证信息
     * 路径 /manager/checkAuthentication
     * 传参(json) mid,authentication
     * 返回值 (json--Result) code,message,data(str)
     * */
    @CrossOrigin
    @PostMapping(value ="/manager/checkAuthentication")
    @ResponseBody
    public Result checkAuthentication(HttpServletRequest request,@Valid String mid,@Valid Boolean isApproved){
        try {
            //获取请求头中的token令牌
            String token = request.getHeader("token");
            // 根据token解析出managerID;
            DecodedJWT decodedJWT = JWTUtil.getTokenInfo(token);
            String managerID = decodedJWT.getClaim("managerID").asString();
            if(null!=managerID&&null!=merchantService.getMerchantByMid(mid)){
                if (managerService.checkAuthentication(mid,isApproved)) {
                    return ResultFactory.buildSuccessResult("已成功提交审核信息！");
                }
            }
            return ResultFactory.buildFailResult("提交审核信息失败！");
        }catch (Exception e){
            e.printStackTrace();
            return ResultFactory.buildFailResult("登陆状态异常！");
        }

    }
    /*
     * 请求方式：post
     * 功能：获取所有注册的商户
     * 路径 /manager/getAllMerchants
     * 传参(json) null
     * 返回值 (json--Result) code,message,data(list<Merchant>)
     * */
    @CrossOrigin
    @PostMapping(value ="/manager/getAllMerchants")
    @ResponseBody
    public Result getAllMerchants(HttpServletRequest request){
        try {
            //获取请求头中的token令牌
            String token = request.getHeader("token");
            // 根据token解析出managerID;
            DecodedJWT decodedJWT = JWTUtil.getTokenInfo(token);
            String managerID = decodedJWT.getClaim("managerID").asString();
            if(null!=managerID){
                    return ResultFactory.buildSuccessResult(managerService.getAllMerchants());
            }
            return ResultFactory.buildFailResult("获取商户信息失败！");
        }catch (Exception e){
            e.printStackTrace();
            return ResultFactory.buildFailResult("登陆状态异常！");
        }
    }
    /*
     * 请求方式：post
     * 功能：获取认证中的商户
     * 路径 /manager/getAllUnAuthenticatedMerchants
     * 传参(json) null
     * 返回值 (json--Result) code,message,data(list<Merchant>)
     * */
    @CrossOrigin
    @PostMapping(value ="/manager/getAllUnAuthenticatedMerchants")
    @ResponseBody
    public Result getAllUnAuthenticatedMerchants(HttpServletRequest request){
        try {
            //获取请求头中的token令牌
            String token = request.getHeader("token");
            // 根据token解析出managerID;
            DecodedJWT decodedJWT = JWTUtil.getTokenInfo(token);
            String managerID = decodedJWT.getClaim("managerID").asString();
            if(null!=managerID){
                return ResultFactory.buildSuccessResult(managerService.getAllUnAuthenticatedMerchants());
            }
            return ResultFactory.buildFailResult("获取商户信息失败！");
        }catch (Exception e){
            e.printStackTrace();
            return ResultFactory.buildFailResult("登陆状态异常！");
        }
    }
}
