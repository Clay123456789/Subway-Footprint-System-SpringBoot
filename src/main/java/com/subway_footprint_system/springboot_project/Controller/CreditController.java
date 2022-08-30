package com.subway_footprint_system.springboot_project.Controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.subway_footprint_system.springboot_project.Dao.Impl.ResultFactory;
import com.subway_footprint_system.springboot_project.Pojo.CreditRecord;
import com.subway_footprint_system.springboot_project.Pojo.Result;
import com.subway_footprint_system.springboot_project.Pojo.User;
import com.subway_footprint_system.springboot_project.Pojo.UserVo;
import com.subway_footprint_system.springboot_project.Service.Impl.CreditRecordServiceImpl;
import com.subway_footprint_system.springboot_project.Service.Impl.UserServiceImpl;
import com.subway_footprint_system.springboot_project.Utils.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@EnableAutoConfiguration
@RestController
public class CreditController {

    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private CreditRecordServiceImpl creditRecordService;
    /*
     * 请求方式：post
     * 功能：获取碳积分排行榜（前10位）
     * 路径 /user/getRankingList
     * 传参(json) null
     * 返回值 (json--Result) code,message,data(List<map>)
     *  map有rank、touxiang、username、credit四个key值
     * */
    @CrossOrigin
    @PostMapping(value ="/user/getRankingList")
    @ResponseBody
    public Result getRankingList(){
        List<Map<String, Object>> list = userService.getRankingList();
        return ResultFactory.buildSuccessResult(list);
    }

    /*
     * 请求方式：post
     * 功能：获取个人碳积分排行
     * 路径 /user/getPersonalCreditRank
     * 传参(json) null
     * 返回值 (json--Result) code,message,data(int)
     * */
    @CrossOrigin
    @PostMapping(value ="/user/getPersonalCreditRank")
    @ResponseBody
    public Result getPersonalCreditRank(HttpServletRequest request){
        try {
            //获取请求头中的token令牌
            String token = request.getHeader("token");
            // 根据token解析出uid;
            DecodedJWT decodedJWT = JWTUtil.getTokenInfo(token);
            String uid = decodedJWT.getClaim("uid").asString();
            int rank = userService.getPersonalCreditRank(uid);
            return ResultFactory.buildSuccessResult(rank);
        }catch (Exception e){
            e.printStackTrace();
            return ResultFactory.buildFailResult("登陆状态异常！");
        }
    }
    /*
     * 请求方式：post
     * 功能：获取碳积分历史
     * 路径 /user/getUserCreditRecords
     * 传参(json) group(第几组，一组六个)
     * 返回值 (json--Result) code,message,data(List<CreditRecord>)
     * */
    @CrossOrigin
    @PostMapping(value ="/user/getUserCreditRecords")
    @ResponseBody
    public Result getUserCreditRecords(HttpServletRequest request,int group){
        try {
            //获取请求头中的token令牌
            String token = request.getHeader("token");
            // 根据token解析出uid;
            DecodedJWT decodedJWT = JWTUtil.getTokenInfo(token);
            String uid = decodedJWT.getClaim("uid").asString();
            List<CreditRecord> list=creditRecordService.getUserCreditRecords(uid,group);
            return ResultFactory.buildSuccessResult(list);
        }catch (Exception e){
            e.printStackTrace();
            return ResultFactory.buildFailResult("登陆状态异常！");
        }
    }
    /*
     * 请求方式：post
     * 功能：新增一条碳积分记录（点亮站点获得碳积分会自动调用该接口，注意不要重复调用）
     * 路径 /user/addCreditRecord
     * 传参(json) operation,way,num
     * 返回值 (json--Result) code,message,data(str)
     * */
    @CrossOrigin
    @PostMapping(value ="/user/addCreditRecord")
    @ResponseBody
    public Result addCreditRecord(HttpServletRequest request,@Valid @RequestBody CreditRecord creditRecord){
        try {
            //获取请求头中的token令牌
            String token = request.getHeader("token");
            // 根据token解析出uid;
            DecodedJWT decodedJWT = JWTUtil.getTokenInfo(token);
            String uid = decodedJWT.getClaim("uid").asString();
            if (creditRecordService.insertCreditRecord(creditRecord.getOperation(),uid,creditRecord.getWay(),creditRecord.getNum())) {
                return ResultFactory.buildFailResult("新增碳积分记录失败！");
            }
            return ResultFactory.buildSuccessResult("新增碳积分记录成功！");
        }catch (Exception e){
            e.printStackTrace();
            return ResultFactory.buildFailResult("登陆状态异常！");
        }
    }
}
