package com.subway_footprint_system.springboot_project.Controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.subway_footprint_system.springboot_project.Dao.Impl.ResultFactory;
import com.subway_footprint_system.springboot_project.Pojo.Result;
import com.subway_footprint_system.springboot_project.Pojo.Treasure;
import com.subway_footprint_system.springboot_project.Service.Impl.TreasureServiceImpl;
import com.subway_footprint_system.springboot_project.Utils.FtpConfig;
import com.subway_footprint_system.springboot_project.Utils.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@EnableAutoConfiguration
@RestController
public class TreasureController {
    @Autowired
    private TreasureServiceImpl treasureService;
    @Autowired
    FtpConfig ftpConfig;
    public void f(){
      //  log.info();
    }
    /*
     * 请求方式：post
     * 功能：用户/商户藏宝
     * 路径 /user/buryTreasure
     * 传参(json) variety(宝箱种类) content(宝箱内容) credit(打开所需碳积分) pid(站点id)
     * 返回值 (json--Result) code,message,data(str)
     * */
    @CrossOrigin
    @PostMapping(value ="/treasure/buryTreasure")
    @ResponseBody
    public Result buryTreasure(HttpServletRequest request, @Valid @RequestBody Treasure treasure){
        try {
            //获取请求头中的token令牌
            String token = request.getHeader("token");
            // 根据token解析出uid;
            DecodedJWT decodedJWT = JWTUtil.getTokenInfo(token);
            String uid = decodedJWT.getClaim("uid").asString();
            treasure.setUid(uid);
            String time=JWTUtil.getNowTime();
            treasure.setFromdate(time);
            treasure.setTid(uid+'-'+time);
            treasure.setTodate("2035年3月3日00:00:00");
            treasure.setStatus(0);
            if(!treasureService.insertTreasure(treasure)){
                return ResultFactory.buildFailResult("藏宝失败！");
            }
            return ResultFactory.buildSuccessResult("藏宝成功！");

        }catch (Exception e){
            e.printStackTrace();
            return ResultFactory.buildFailResult("登陆状态异常！");
        }

    }


    /*
     * 请求方式：post
     * 功能：用户挖宝
     * 路径 /treasure/digTreasure
     * 传参(json) tid(宝箱id)
     * 返回值 (json--Result) code,message,data(str)
     * */
    @CrossOrigin
    @PostMapping(value ="/treasure/digTreasure")
    @ResponseBody
    public Result digTreasure(HttpServletRequest request, @Valid @RequestBody Treasure treasure){
        try {
            //获取请求头中的token令牌
            String token = request.getHeader("token");
            // 根据token解析出uid;
            DecodedJWT decodedJWT = JWTUtil.getTokenInfo(token);
            String uid = decodedJWT.getClaim("uid").asString();
            Treasure treasure1=treasureService.getTreasure(treasure.getTid());
            treasure1.setStatus(1);
            treasure1.setUid2(uid);
            String time=JWTUtil.getNowTime();
            treasure1.setGetdate(time);
            if(!treasureService.updateTreasure(treasure1)){
                return ResultFactory.buildFailResult("挖宝失败");
            }
            return ResultFactory.buildSuccessResult("挖宝成功！");

        }catch (Exception e){
            e.printStackTrace();
            return ResultFactory.buildFailResult("登陆状态异常！");
        }

    }


    /*
     * 请求方式：post
     * 功能：用户打开宝箱
     * 路径 /treasure/openTreasure
     * 传参(json) tid(宝箱id)
     * 返回值 (json--Result) code,message,data(str)
     * */
    @CrossOrigin
    @PostMapping(value ="/treasure/openTreasure")
    @ResponseBody
    public Result openTreasure(HttpServletRequest request, @Valid @RequestBody Treasure treasure){
        try {
            //获取请求头中的token令牌
            String token = request.getHeader("token");
            // 根据token解析出uid;
            DecodedJWT decodedJWT = JWTUtil.getTokenInfo(token);
            String uid = decodedJWT.getClaim("uid").asString();
            Treasure treasure1=treasureService.getTreasure(treasure.getTid());
            treasure1.setStatus(2);
            if(!treasureService.updateTreasure(treasure1)){
                return ResultFactory.buildFailResult("打开宝箱失败");
            }
            return ResultFactory.buildSuccessResult("打开宝箱成功！");

        }catch (Exception e){
            e.printStackTrace();
            return ResultFactory.buildFailResult("登陆状态异常！");
        }

    }


    /*
     * 请求方式：post
     * 功能：查询单个宝箱
     * 路径 /treasure/getTreasure
     * 传参(json) tid(宝箱id)
     * 返回值 (json--Result) code,message,data(str)
     * */
    @CrossOrigin
    @PostMapping(value ="/treasure/getTreasure")
    @ResponseBody
    public Result getTreasure(@Valid @RequestBody Treasure treasure){
        try {
            Treasure treasure1=treasureService.getTreasure(treasure.getTid());
            //log.info(treasure1.getTid()+treasure1.getStatus()+treasure1.getUid2());
            return ResultFactory.buildSuccessResult(treasure1);
        }catch (Exception e){
            e.printStackTrace();
            return ResultFactory.buildFailResult("登陆状态异常！");
        }
    }

    /*
     * 请求方式：post
     * 功能：查询某一站点的所有未挖宝箱
     * 路径 /treasure/getPositionTreasure
     * 传参(json) pid(站点id)
     * 返回值 (json--Result) code,message,data(str)
     * */
    @CrossOrigin
    @PostMapping(value ="/treasure/getPositionTreasure")
    @ResponseBody
    public Result getPositionTreasure(@Valid @RequestBody Treasure treasure){
        try {
            List<Treasure> list=treasureService.getPositionTreasure(treasure.getPid());
            return ResultFactory.buildSuccessResult(list);
        }catch (Exception e){
            e.printStackTrace();
            return ResultFactory.buildFailResult("登陆状态异常！");
        }
    }


    /*
     * 请求方式：post
     * 功能：查询所有未挖宝箱
     * 路径 /treasure/getAllTreasure
     * 传参(json) null
     * 返回值 (json--Result) code,message,data(str)
     * */
    @CrossOrigin
    @PostMapping(value ="/treasure/getAllTreasure")
    @ResponseBody
    public Result getAllTreasure(){
        try {
            List<Treasure> list=treasureService.getAllTreasure();
            return ResultFactory.buildSuccessResult(list);
        }catch (Exception e){
            e.printStackTrace();
            return ResultFactory.buildFailResult("登陆状态异常！");
        }
    }

    /*
     * 请求方式：post
     * 功能：获取属于某一用户的所有未打开宝箱
     * 路径 /treasure/getUserTreasure
     * 传参(json) null
     * 返回值 (json--Result) code,message,data(str)
     * */

    @CrossOrigin
    @PostMapping(value ="/treasure/getUserTreasure")
    @ResponseBody
    public Result getUserTreasure(HttpServletRequest request){
        try {
            //获取请求头中的token令牌
            String token = request.getHeader("token");
            // 根据token解析出uid;
            DecodedJWT decodedJWT = JWTUtil.getTokenInfo(token);
            String uid2 = decodedJWT.getClaim("uid").asString();
            List<Treasure> list=treasureService.getUserTreasure(uid2);
            return ResultFactory.buildSuccessResult(list);
        }catch (Exception e){
            e.printStackTrace();
            return ResultFactory.buildFailResult("登陆状态异常！");
        }
    }

}
