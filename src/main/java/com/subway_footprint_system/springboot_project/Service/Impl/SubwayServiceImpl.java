package com.subway_footprint_system.springboot_project.Service.Impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.*;
import com.subway_footprint_system.springboot_project.Dao.Impl.SubwayDaoImpl;
import com.subway_footprint_system.springboot_project.Pojo.Subway;
import com.subway_footprint_system.springboot_project.Service.ISubwayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import java.util.List;
import java.util.Map;

import static org.apache.http.protocol.HTTP.USER_AGENT;

@Service
public class SubwayServiceImpl implements ISubwayService {
    @Autowired
    private SubwayDaoImpl subwayDao;

    @Override
    public boolean insertSubway(Subway subway) {
        return subwayDao.insertSubway(subway);
    }

    @Override
    public boolean deleteSubway(String sid) {
        return  subwayDao.deleteSubway(sid);
    }

    @Override
    public boolean updateSubway(Subway subway) {
        if(null!=subway.getSid())
            return subwayDao.updateSubway(subway);
        return false;
    }

    @Override
    public Subway getSubway(String sid) {
        return  subwayDao.getSubway(sid);
    }

    @Override
    public Map<String, Object> getAllSubways() {
        return subwayDao.getAllSubways();
    }

    @Override
    public Map<String, Object> getAllSubways(int code) {
        return subwayDao.getAllSubways(code);
    }

    @Override
    public boolean uploadAllSubways() throws Exception  {
        //从百度api获取已开通地铁城市信息
        String url1 = "http://map.baidu.com/?qt=subwayscity&t=0000";
        URL obj1 = new URL(url1);
        HttpURLConnection con1 = (HttpURLConnection) obj1.openConnection();
        //GET请求
        con1.setRequestMethod("GET");
        //添加请求头
        con1.setRequestProperty("User-Agent", USER_AGENT);

        if(con1.getResponseCode()!=200){
            return false;
        }
        BufferedReader in1 = new BufferedReader(new InputStreamReader(con1.getInputStream()));
        String inputLine1;
        StringBuffer response1 = new StringBuffer();
        while ((inputLine1 = in1.readLine()) != null) {
            response1.append(inputLine1);
        }
        in1.close();
        JSONObject jsonObject1 = JSONObject.parseObject(response1.toString());
        String subways_city = jsonObject1.getString("subways_city");
        JSONArray cities=JSONObject.parseObject(subways_city).getJSONArray("cities");//获取数组
        for (int i = 0; i < cities.size(); i++) {
            JSONObject jsonObject3 = JSONObject.parseObject(cities.get(i).toString());
            String code=jsonObject3.getString("code");
            String cn_name=jsonObject3.getString("cn_name");
            String cename=jsonObject3.getString("cename");
            String cpre=jsonObject3.getString("cpre");

            //从百度api获取某城市地铁信息
            String url2 = "https://api.map.baidu.com/?qt=subways&c="+code+ "&format=json&ak=yZSTYLk9UUvs0ZqXqBbtTp8ViKk5vxLM&v=3.0&from=jsapi";
            URL obj2 = new URL(url2);
            HttpURLConnection con2 = (HttpURLConnection) obj2.openConnection();
            //GET请求
            con2.setRequestMethod("GET");
            //添加请求头
            con2.setRequestProperty("User-Agent", USER_AGENT);
            if(con2.getResponseCode()!=200){
                return false;
            }
            BufferedReader in2 = new BufferedReader(new InputStreamReader(con2.getInputStream()));
            String inputLine2;
            StringBuffer response2 = new StringBuffer();
            while ((inputLine2 = in2.readLine()) != null) {
                response2.append(inputLine2);
            }
            in2.close();
            JSONObject jsonObject4 = JSONObject.parseObject(response2.toString());
            String str=jsonObject4.getString("subways");
            if(!str.equals("[]")){//存在部分城市暂无地铁信息，获取到的为空数组[]（不知为何？）
                System.out.println("----------------");
                System.out.println(str);
                JSONObject jsonObject5 = JSONObject.parseObject(str);
                JSONArray l=null;
                Object object=jsonObject5.getJSONArray("l");
                if(object!=null){
                    l=(JSONArray)object;
                    for (int j = 0; j < l.size(); j++) {
                        JSONObject jsonObject6 = JSONObject.parseObject(l.get(j).toString());
                        Map<String,Object> l_xmlattr=JSONObject.parseObject(jsonObject6.getString("l_xmlattr"));

                        //List<Map<String,Object>> p=
                        JSONArray t = JSONObject.parseArray(jsonObject6.getString("p"));

                        List<Map<String,Object>> p = ( List<Map<String,Object>> )JSONObject.parse(t.toJSONString());

                        String lid=JSONObject.parseObject(jsonObject6.getString("l_xmlattr")).getString("lid");

                        if(subwayDao.getSubway(code+"_"+lid)==null){
                            if(!subwayDao.insertSubway(new Subway(code+"_"+lid,Integer.parseInt(code), cn_name, cename, cpre, l_xmlattr, p))){
                                return false;
                            }
                        }
                    }
                }
            }

        }
        return true;
    }
}
