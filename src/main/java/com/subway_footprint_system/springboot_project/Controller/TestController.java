package com.subway_footprint_system.springboot_project.Controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.subway_footprint_system.springboot_project.Dao.Impl.ResultFactory;
import com.subway_footprint_system.springboot_project.Pojo.AwardRecord;
import com.subway_footprint_system.springboot_project.Pojo.Result;
import com.subway_footprint_system.springboot_project.Service.Impl.AwardRecordServiceImpl;
import com.subway_footprint_system.springboot_project.Utils.JWTUtil;
import com.subway_footprint_system.springboot_project.Utils.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * 在这可以写一些测试接口
 */
@Slf4j
@EnableAutoConfiguration
@RestController
public class TestController {

    @Autowired
    private StringEncryptor encryptor;
    @Resource
    private WebSocketServer webSocketServer;
    @Autowired
    private AwardRecordServiceImpl awardRecordService;


    @CrossOrigin
    @RequestMapping("/hello")
    public String hello(String username) {

        return "Hello," + username + "!";
    }

    @PostMapping("/test")
    @CrossOrigin
    @ResponseBody
    public Map<String, String> test(@RequestBody Object object, HttpServletRequest request) {

        Map<String, String> map = new HashMap<>();
        //处理自己业务逻辑
        String token = request.getHeader("token");
        DecodedJWT claims = JWTUtil.getTokenInfo(token);
        map.put("code", "200");
        map.put("msg", "请求成功!");
        map.put("data", object.toString());
        return map;
    }

    /**
     * 手动生成密文
     *
     * @param str 原文
     * @return Result data里为密文
     */
    @CrossOrigin
    @RequestMapping("/encrypt")
    public Result encrypt(String str) {
        return ResultFactory.buildSuccessResult(encryptor.encrypt(str));
    }

    /**
     * 解密
     *
     * @param str 密文
     * @return Result data里为原文
     */
    @CrossOrigin
    @RequestMapping("/decrypt")
    public Result decrypt(String str) {
        return ResultFactory.buildSuccessResult(encryptor.decrypt(str));
    }

    @GetMapping("/scanSuccess/{deliveryCode}")
    public String scanSuccess(@PathVariable("deliveryCode") String deliveryCode) throws ParseException {
        int num = deliveryCode.lastIndexOf('_');
        String time = deliveryCode.substring(num + 1);
        String arid = deliveryCode.substring(0, num);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");
        long sub = formatter.parse(JWTUtil.getNowTime()).getTime() - formatter.parse(time).getTime();
        if (sub > 3000) {
            return "二维码已过期";
        }
        AwardRecord awardRecord = awardRecordService.getAnyAwardRecord(arid);
        if (null != awardRecord && 1 == awardRecord.getOperation()) {

        }
        //有效期以内
        webSocketServer.sendOneMessage(deliveryCode, "成功");
        return "success";
    }


}
