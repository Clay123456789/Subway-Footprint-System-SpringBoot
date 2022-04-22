package com.subway_footprint_system.springboot_project.Controller;

import org.springframework.web.bind.annotation.*;
import com.alibaba.fastjson.JSON;
import com.subway_footprint_system.springboot_project.Dao.Impl.ResultFactory;
import com.subway_footprint_system.springboot_project.Pojo.Result;
import com.subway_footprint_system.springboot_project.Service.Impl.SubwayServiceImpl;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import java.util.Map;

@EnableAutoConfiguration
@RestController
public class SubwayController {

    @Autowired
    private SubwayServiceImpl subwayService;

    /*
     * 请求方式：get
     * 功能：获取指定/所有城市铁路信息
     * 路径 /Subway/getAllSubways
     * 传参(json) code(0代表所有)
     * 返回值(json) String（用Result封装json中会有转义符干扰，故此接口直接返回结果）
     * */
    @CrossOrigin
    @RequestMapping("/Subway/getAllSubways")
    public String getAllSubways(int code) throws Exception {
        Map<String, Object> map=null;
        if(code==0){
            map=subwayService.getAllSubways();
        }else{
            map=subwayService.getAllSubways(code);
        }
        if(map==null){
            // return ResultFactory.buildFailResult("发生错误，获取失败");
            return null;
        }else{
            String data= StringEscapeUtils.unescapeJavaScript(JSON.toJSONString(map));
            System.out.println(data);
            return data;
            //return ResultFactory.buildSuccessResult(data);
        }

    }
    /*
     * 请求方式：get
     * 功能：上传所有城市铁路信息
     * 路径 /Subway/uploadAllSubways
     * 传参(json) null
     * 返回值(json--Result) code,message,data(boolean)
     * */
    @CrossOrigin
    @RequestMapping("/Subway/uploadAllSubways")
    public Result uploadAllSubways() throws Exception {
        if(subwayService.uploadAllSubways()){
            return ResultFactory.buildSuccessResult(true);
        }
        return ResultFactory.buildFailResult("发生错误，上传失败");
    }
}
