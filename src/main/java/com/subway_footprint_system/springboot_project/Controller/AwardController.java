package com.subway_footprint_system.springboot_project.Controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.subway_footprint_system.springboot_project.Dao.Impl.ResultFactory;
import com.subway_footprint_system.springboot_project.Pojo.Award;
import com.subway_footprint_system.springboot_project.Pojo.Result;
import com.subway_footprint_system.springboot_project.Service.Impl.AwardRecordServiceImpl;
import com.subway_footprint_system.springboot_project.Service.Impl.AwardServiceImpl;
import com.subway_footprint_system.springboot_project.Utils.FtpUtil;
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
public class AwardController {
    @Autowired
    private FtpUtil ftpUtil;
    @Autowired
    private AwardServiceImpl awardService;

    /*
     *待实现
     *
     * 请求方式：post
     * 功能：增加奖品(商户端和管理员端共用)
     * 路径 /award/addAward
     * 请求头中需携带mid(商户端)或managerID(管理员端)
     * 传参(json) variety,num,name,content,credit,todate
     * 返回值 (json--Result) code,message,data(str)
     * */
    @CrossOrigin
    @PostMapping(value = "/award/addAward")
    @ResponseBody
    public Result addAward(@Valid @RequestBody Award award,HttpServletRequest request) {
        award.setAid("aid-"+ftpUtil.getRandom(20));
        award.setFromdate(JWTUtil.getNowTime());
        award.setStatus(0);
        try {
            //获取请求头中的token令牌
            String token = request.getHeader("token");
            // 根据token解析出mid;
            DecodedJWT decodedJWT = JWTUtil.getTokenInfo(token);
            String token_mid = decodedJWT.getClaim("mid").asString();
            if(token_mid!=null){
                award.setMid(token_mid);
            }else if(null==decodedJWT.getClaim("managerID").asString()){
                return ResultFactory.buildFailResult("登陆状态异常！");
            }
            if(awardService.insertAward(award)){
                return ResultFactory.buildSuccessResult("已成功添加奖品");
            }
            return ResultFactory.buildFailResult("添加失败！");
        }catch (Exception e){
            e.printStackTrace();
            return ResultFactory.buildFailResult("登陆状态异常！");
        }

    }



    /*
     * 请求方式：post
     * 功能：获取随机若干（状态正常且不同批次的）奖品
     * 路径 /award/getSomeAwards
     * 传参(json)
     * 返回值 (json--Result) code,message,data(str)
     * */
    @CrossOrigin
    @PostMapping(value = "/award/getSomeAwards")
    @ResponseBody
    public Result getSomeAwards(int num) {
        List<Award> list=awardService.getSomeAwards(num);
        if(null==list){
            return ResultFactory.buildFailResult("获取失败！");
        }else{
            return ResultFactory.buildSuccessResult(list);
        }

    }

    /*
     * 请求方式：post
     * 功能：获取指定奖品的信息
     * 路径 /award/getAward
     * 传参(json) aid(奖品ID)
     * 返回值 (json--Result) code,message,data(str)
     * */
    @CrossOrigin
    @PostMapping(value = "/award/getAward")
    @ResponseBody
    public Result getAward(String aid) {
        Award award=awardService.getAward(aid);
        if(award==null){
            return ResultFactory.buildFailResult("获取失败！");
        }else{
            return ResultFactory.buildSuccessResult(award);
        }

    }

    /*
     * 请求方式：post
     * 功能：获取指定商户上传的全部奖品(商户端和管理员端共用)
     * 路径 /award/getMerchantAwards
     * 请求头中需携带mid(商户端)或managerID(管理员端)
     * 传参(json) mid(商户id,商户端调用该接口无需传参)
     * 返回值 (json--Result) code,message,data(str)
     * */
    @CrossOrigin
    @PostMapping(value = "/award/getMerchantAwards")
    @ResponseBody
    public Result getMerchantAwards(HttpServletRequest request,String mid) {
        try {
            //获取请求头中的token令牌
            String token = request.getHeader("token");
            // 根据token解析出mid;
            DecodedJWT decodedJWT = JWTUtil.getTokenInfo(token);
            String token_mid = decodedJWT.getClaim("mid").asString();
            if(token_mid!=null){
                List<Award> list=awardService.getMerchantAwards(token_mid);
                if(null!=list) {
                    return ResultFactory.buildSuccessResult(list);
                }
            }else if(decodedJWT.getClaim("managerID").asString()!=null&&!mid.equals("")){
                List<Award> list=awardService.getMerchantAwards(mid);
                if(null!=list) {
                    return ResultFactory.buildSuccessResult(list);
                }
            }
            return ResultFactory.buildFailResult("获取失败！");
        }catch (Exception e){
            e.printStackTrace();
            return ResultFactory.buildFailResult("登陆状态异常！");
        }

    }

    /*
     * 请求方式：post
     * 功能：获取全部奖品
     * 路径 /award/getAllAwards
     * 传参(json)
     * 返回值 (json--Result) code,message,data(str)
     * */
    @CrossOrigin
    @PostMapping(value = "/award/getAllAwards")
    @ResponseBody
    public Result getAllAwards() {
        List<Award> list=awardService.getAllAwards();
        if(null==list){
            return ResultFactory.buildFailResult("获取失败！");
        }else{
            return ResultFactory.buildSuccessResult(list);
        }

    }

    /*
     * 请求方式：post
     * 功能：修改指定奖品(商户端和管理员端共用,商户只能修改其上传的奖品)
     * 路径 /award/updateAward
     * 传参(json)aid, variety, num, name, content, credit,todate
     * 返回值 (json--Result) code,message,data(str)
     * */
    @CrossOrigin
    @PostMapping(value = "/award/updateAward")
    @ResponseBody
    public Result updateAward(@Valid @RequestBody Award award,HttpServletRequest request) {
       try {
            //获取请求头中的token令牌
            String token = request.getHeader("token");
            // 根据token解析出mid;
            DecodedJWT decodedJWT = JWTUtil.getTokenInfo(token);
            String token_mid = decodedJWT.getClaim("mid").asString();

            if(token_mid!=null&&token_mid.equals(awardService.getAward(award.getAid()).getMid())){
                award.setMid(token_mid);
               if(awardService.updateAward(award)){
                   return ResultFactory.buildSuccessResult("已成功修改奖品");
               }
            }else if(null!=decodedJWT.getClaim("managerID").asString()){
                award.setMid(null);
               if(awardService.updateAward(award)){
                   return ResultFactory.buildSuccessResult("已成功修改奖品");
               }
            }
            return ResultFactory.buildFailResult("修改失败！");
        }catch (Exception e){
            e.printStackTrace();
            return ResultFactory.buildFailResult("出现异常！");
        }
    }
    /*
     * 请求方式：post
     * 功能：删除指定奖品(商户端和管理员端共用,商户只能删除其上传的奖品)
     * 路径 /award/deleteAward
     * 传参(json) aid
     * 返回值 (json--Result) code,message,data(str)
     * */
    @CrossOrigin
    @PostMapping(value = "/award/deleteAward")
    @ResponseBody
    public Result deleteAward(@Valid String aid,HttpServletRequest request) {
        try {
            //获取请求头中的token令牌
            String token = request.getHeader("token");
            // 根据token解析出mid;
            DecodedJWT decodedJWT = JWTUtil.getTokenInfo(token);
            String token_mid = decodedJWT.getClaim("mid").asString();
            if(token_mid!=null&&token_mid.equals(awardService.getAward(aid).getMid())){
                if(awardService.deleteAward(aid)){
                    return ResultFactory.buildSuccessResult("已成功删除奖品");
                }
            }else if(null!=decodedJWT.getClaim("managerID").asString()){
                if(awardService.deleteAward(aid)){
                    return ResultFactory.buildSuccessResult("已成功删除奖品");
                }
            }
            return ResultFactory.buildFailResult("删除失败！");
        }catch (Exception e){
            e.printStackTrace();
            return ResultFactory.buildFailResult("出现异常！");
        }
    }
}