package com.subway_footprint_system.springboot_project.Controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.subway_footprint_system.springboot_project.Dao.Impl.ResultFactory;
import com.subway_footprint_system.springboot_project.Pojo.LightedStation;
import com.subway_footprint_system.springboot_project.Pojo.Result;
import com.subway_footprint_system.springboot_project.Service.Impl.CreditRecordServiceImpl;
import com.subway_footprint_system.springboot_project.Service.Impl.LightedStationServiceImpl;
import com.subway_footprint_system.springboot_project.Service.Impl.UserServiceImpl;
import com.subway_footprint_system.springboot_project.Utils.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@EnableAutoConfiguration
@RestController
public class LightStationController {

    @Autowired
    private LightedStationServiceImpl lightedStationService;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private CreditRecordServiceImpl creditRecordService;

    /*
     * 请求方式：post
     * 功能：用户新增点亮站点(成功后自动触发增加碳积分)
     * 路径 /user/addLightedStation
     * 传参(json) pid(站点id) credit(点亮获得碳积分)
     * 返回值 (json--Result) code,message,data(str)
     * */
    @CrossOrigin
    @PostMapping(value = "/user/addLightedStation")
    @ResponseBody
    public Result addLightedStation(HttpServletRequest request, @Valid @RequestBody LightedStation lightedStation) {
        try {
            //获取请求头中的token令牌
            String token = request.getHeader("token");
            // 根据token解析出uid;
            DecodedJWT decodedJWT = JWTUtil.getTokenInfo(token);
            String uid = decodedJWT.getClaim("uid").asString();
            lightedStation.setUid(uid);
            String time = JWTUtil.getNowTime();
            lightedStation.setTime(time);
            if (!lightedStationService.insertLightedStation(lightedStation)) {
                return ResultFactory.buildFailResult("新增点亮站点失败！");
            }
            if (!creditRecordService.insertCreditRecord(1, uid, "点亮站点获得", lightedStation.getCredit())) {
                return ResultFactory.buildFailResult("新增点亮站点成功！增加碳积分失败！");
            }

            return ResultFactory.buildSuccessResult("已成功新增点亮站点并增加相应碳积分！");
        } catch (Exception e) {
            e.printStackTrace();
            return ResultFactory.buildFailResult("出现异常！");
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
    @PostMapping(value = "/user/deleteLightedStation")
    @ResponseBody
    public Result deleteLightedStation(HttpServletRequest request, @Valid @RequestBody LightedStation lightedStation) {
        try {
            //获取请求头中的token令牌
            String token = request.getHeader("token");
            // 根据token解析出uid;
            DecodedJWT decodedJWT = JWTUtil.getTokenInfo(token);
            String uid = decodedJWT.getClaim("uid").asString();
            lightedStation.setUid(uid);
            if (!lightedStationService.deleteLightedStation(lightedStation.getUid(), lightedStation.getPid())) {
                return ResultFactory.buildFailResult("删除点亮站点失败！");
            }
            return ResultFactory.buildSuccessResult("已成功删除点亮站点！");
        } catch (Exception e) {
            e.printStackTrace();
            return ResultFactory.buildFailResult("出现异常！");
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
    @PostMapping(value = "/user/getLightedStation")
    @ResponseBody
    public Result getLightedStation(HttpServletRequest request, @Valid @RequestBody LightedStation lightedStation) {
        try {
            //获取请求头中的token令牌
            String token = request.getHeader("token");
            // 根据token解析出uid;
            DecodedJWT decodedJWT = JWTUtil.getTokenInfo(token);
            String uid = decodedJWT.getClaim("uid").asString();
            lightedStation.setUid(uid);
            LightedStation lightedStation1 = lightedStationService.getLightedStation(lightedStation.getUid(), lightedStation.getPid());
            return ResultFactory.buildSuccessResult(lightedStation1);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultFactory.buildFailResult("出现异常！");
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
    @PostMapping(value = "/user/getUserLightedStations")
    @ResponseBody
    public Result getUserLightedStations(HttpServletRequest request) {
        try {
            //获取请求头中的token令牌
            String token = request.getHeader("token");
            // 根据token解析出uid;
            DecodedJWT decodedJWT = JWTUtil.getTokenInfo(token);
            String uid = decodedJWT.getClaim("uid").asString();
            LightedStation lightedStation = LightedStation.builder().build();
            lightedStation.setUid(uid);
            List<LightedStation> list = lightedStationService.getUserLightedStations(lightedStation.getUid());
            return ResultFactory.buildSuccessResult(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultFactory.buildFailResult("出现异常！");
        }
    }
}
