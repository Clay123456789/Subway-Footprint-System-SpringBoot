package com.subway_footprint_system.springboot_project.Controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.subway_footprint_system.springboot_project.Dao.Impl.ResultFactory;
import com.subway_footprint_system.springboot_project.Pojo.Award;
import com.subway_footprint_system.springboot_project.Pojo.Result;
import com.subway_footprint_system.springboot_project.Pojo.Treasure;
import com.subway_footprint_system.springboot_project.Service.Impl.AwardRecordServiceImpl;
import com.subway_footprint_system.springboot_project.Service.Impl.AwardServiceImpl;
import com.subway_footprint_system.springboot_project.Service.Impl.CreditRecordServiceImpl;
import com.subway_footprint_system.springboot_project.Service.Impl.TreasureServiceImpl;
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
    private AwardRecordServiceImpl awardRecordService;
    @Autowired
    private AwardServiceImpl awardService;
    @Autowired
    private CreditRecordServiceImpl creditRecordService;

    /*
     * 请求方式：post
     * 功能：用户藏宝
     * 路径 /treasure/buryTreasure
     * 传参(json) <String>aid（选择的奖品id） <int>num(藏宝的奖品个数) <int>credit(打开所需碳积分) <String>pid(站点id) <String>message(留言)
     * 返回值 (json--Result) code,message,data(str)
     * */
    @CrossOrigin
    @PostMapping(value = "/treasure/buryTreasure")
    @ResponseBody
    public Result buryTreasure(HttpServletRequest request, String aid, int num, int credit, String pid, String message) {
        try {
            //获取请求头中的token令牌
            String token = request.getHeader("token");
            // 根据token解析出uid;
            DecodedJWT decodedJWT = JWTUtil.getTokenInfo(token);
            String uid = decodedJWT.getClaim("uid").asString();
            String time = JWTUtil.getNowTime();
            Award award = awardService.getAward(aid);
            Treasure treasure = Treasure.builder()
                    .tid(uid + '-' + time)
                    .variety(award.getVariety())
                    .content(award.getContent())
                    .credit(credit)
                    .pid(pid)
                    .fromdate(time)
                    .todate(award.getTodate())
                    .status(0)
                    .uid(uid)
                    .message(message)
                    .build();
            if (!treasureService.insertTreasure(treasure) || !awardRecordService.addUserBuryAwardRecord(aid, uid, num, credit)) {
                return ResultFactory.buildFailResult("藏宝失败！");
            }
            return ResultFactory.buildSuccessResult("藏宝成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return ResultFactory.buildFailResult("藏宝失败！");
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
    @PostMapping(value = "/treasure/digTreasure")
    @ResponseBody
    public Result digTreasure(HttpServletRequest request, @Valid @RequestBody Treasure treasure) {
        try {
            //获取请求头中的token令牌
            String token = request.getHeader("token");
            // 根据token解析出uid;
            DecodedJWT decodedJWT = JWTUtil.getTokenInfo(token);
            String uid = decodedJWT.getClaim("uid").asString();
            Treasure treasure1 = treasureService.getTreasure(treasure.getTid());
            treasure1.setStatus(1);
            treasure1.setUid2(uid);
            String time = JWTUtil.getNowTime();
            treasure1.setGetdate(time);
            if (!treasureService.updateTreasure(treasure1)) {
                return ResultFactory.buildFailResult("挖宝失败");
            }
            return ResultFactory.buildSuccessResult("挖宝成功！");

        } catch (Exception e) {
            e.printStackTrace();
            return ResultFactory.buildFailResult("出现异常！");
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
    @PostMapping(value = "/treasure/openTreasure")
    @ResponseBody
    public Result openTreasure(HttpServletRequest request, @Valid @RequestBody Treasure treasure) {
        try {
            //获取请求头中的token令牌
            String token = request.getHeader("token");
            // 根据token解析出uid;
            DecodedJWT decodedJWT = JWTUtil.getTokenInfo(token);
            String uid = decodedJWT.getClaim("uid").asString();
            Treasure treasure1 = treasureService.getTreasure(treasure.getTid());
            treasure1.setStatus(2);
            if (!treasureService.updateTreasure(treasure1)) {
                return ResultFactory.buildFailResult("打开宝箱失败");
            }
            if (!creditRecordService.insertCreditRecord(0, uid, "打开宝箱", treasure1.getCredit())) {
                return ResultFactory.buildFailResult("信息同步失败！");
            }
            return ResultFactory.buildSuccessResult("打开宝箱成功！");

        } catch (Exception e) {
            e.printStackTrace();
            return ResultFactory.buildFailResult("出现异常！");
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
    @PostMapping(value = "/treasure/getTreasure")
    @ResponseBody
    public Result getTreasure(@Valid @RequestBody Treasure treasure) {
        try {
            Treasure treasure1 = treasureService.getTreasure(treasure.getTid());
            //log.info(treasure1.getTid()+treasure1.getStatus()+treasure1.getUid2());
            return ResultFactory.buildSuccessResult(treasure1);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultFactory.buildFailResult("出现异常！");
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
    @PostMapping(value = "/treasure/getPositionTreasure")
    @ResponseBody
    public Result getPositionTreasures(@Valid @RequestBody Treasure treasure) {
        try {
            List<Treasure> list = treasureService.getPositionTreasures(treasure.getPid());
            return ResultFactory.buildSuccessResult(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultFactory.buildFailResult("出现异常！");
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
    @PostMapping(value = "/treasure/getAllTreasure")
    @ResponseBody
    public Result getAllTreasures() {
        try {
            List<Treasure> list = treasureService.getAllTreasures();
            return ResultFactory.buildSuccessResult(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultFactory.buildFailResult("出现异常！");
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
    @PostMapping(value = "/treasure/getUserTreasure")
    @ResponseBody
    public Result getUserTreasures(HttpServletRequest request) {
        try {
            //获取请求头中的token令牌
            String token = request.getHeader("token");
            // 根据token解析出uid;
            DecodedJWT decodedJWT = JWTUtil.getTokenInfo(token);
            String uid2 = decodedJWT.getClaim("uid").asString();
            List<Treasure> list = treasureService.getUserTreasures(uid2);
            return ResultFactory.buildSuccessResult(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultFactory.buildFailResult("出现异常！");
        }
    }

    /*
     * 请求方式：post
     * 功能：修改地铁站宝箱概率
     * 路径 /treasure/changePositionTreasureProbability
     * 传参(json) String pid,float probability
     * 返回值 (json--Result) code,message,data(str)
     * */
    @CrossOrigin
    @PostMapping(value = "/treasure/changePositionTreasureProbability")
    @ResponseBody
    public Result changePositionTreasureProbability(HttpServletRequest request, String pid, float probability) {
        try {
            //获取请求头中的token令牌
            String token = request.getHeader("token");
            // 根据token解析出managerID;
            DecodedJWT decodedJWT = JWTUtil.getTokenInfo(token);
            String managerID = decodedJWT.getClaim("managerID").asString();
            if (null != managerID && treasureService.changePositionTreasureProbability(pid, probability)) {
                return ResultFactory.buildSuccessResult("修改地铁站宝箱概率成功！");
            }
            return ResultFactory.buildFailResult("修改地铁站宝箱概率失败！");
        } catch (Exception e) {
            e.printStackTrace();
            return ResultFactory.buildFailResult("出现异常！");
        }
    }


    /*
     * 请求方式：post
     * 功能：获取地铁站宝箱概率
     * 路径 /treasure/getPositionTreasureProbability
     * 传参(json) String pid
     * 返回值 (json--Result) code,message,data(str)
     * */
    @CrossOrigin
    @PostMapping(value = "/treasure/getPositionTreasureProbability")
    @ResponseBody
    public Result getPositionTreasureProbability(String pid) {
        try {
            return ResultFactory.buildSuccessResult(treasureService.getPositionTreasureProbability(pid));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultFactory.buildFailResult("出现异常！");
        }
    }
}
