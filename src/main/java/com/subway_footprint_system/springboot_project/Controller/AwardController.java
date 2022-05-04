package com.subway_footprint_system.springboot_project.Controller;

import com.subway_footprint_system.springboot_project.Dao.Impl.ResultFactory;
import com.subway_footprint_system.springboot_project.Pojo.Award;
import com.subway_footprint_system.springboot_project.Pojo.Result;
import com.subway_footprint_system.springboot_project.Service.Impl.AwardServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@EnableAutoConfiguration
@RestController
public class AwardController {
    @Autowired
    private AwardServiceImpl awardService;

    /*
     *待实现
     *
     * 请求方式：post
     * 功能：增加奖品(分管理员和商户)
     * 路径 /award/addAward
     * 传参(json) xxx
     * 返回值 (json--Result) code,message,data(str)
     * */



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
    public Result buryTreasure(int num) {
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


}