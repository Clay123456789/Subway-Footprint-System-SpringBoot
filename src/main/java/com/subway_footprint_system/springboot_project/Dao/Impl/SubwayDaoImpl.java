package com.subway_footprint_system.springboot_project.Dao.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.subway_footprint_system.springboot_project.Dao.ISubwayDao;
import com.subway_footprint_system.springboot_project.Pojo.Subway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Repository
public class SubwayDaoImpl implements ISubwayDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 添加一行数据：
     * 直接添加到mysql数据库,将缓存中数据所在表删除（为了保证selectAll获取数据一致）
     */
    @Override
    public boolean insertSubway(Subway subway) {
        //返回影响行数，为1即增加成功
        int result= jdbcTemplate.update("insert into subway(sid,code,cn_name,cename,cpre,l_xmlattr,p) values(?,?,?,?,?,?,?)",
                subway.getSid(),subway.getCode(),subway.getCn_name(),subway.getCename(),subway.getCpre(), JSONObject.toJSONString(subway.getL_xmlattr()),JSONObject.toJSONString(subway.getP()));
        if(result>0){
            // 判断是否缓存存在
            String key1 = "subway_list";
            String key2 = "subway_"+subway.getCode();
            Boolean hasKey1 = redisTemplate.hasKey(key1);
            Boolean hasKey2 = redisTemplate.hasKey(key2);
            // 缓存存在，进行删除
            if (hasKey1) {
                redisTemplate.delete(key1);
            }
            if (hasKey2) {
                redisTemplate.delete(key2);
            }
            return true;
        }
        else{
            return false;
        }

    }



    /**
     * 删除一行数据：
     * 先删除mysql数据库，再将缓存的数据删除即可
     */
    @Override
    public boolean deleteSubway(String sid) {
       Subway subway= getSubway(sid);
       if(subway!=null){
           int  result=   jdbcTemplate.update("delete from subway where sid = ?",sid);
           if(result!=0){
               // 判断是否缓存存在
               String key = "subway_" + subway.getSid();
               String key1 = "subway_list";
               String key2 = "subway_"+subway.getCode();
               Boolean hasKey = redisTemplate.hasKey(key);
               // 缓存存在，进行删除
               if (hasKey) {
                   redisTemplate.delete(key);
               }
               Boolean hasKey1 = redisTemplate.hasKey(key1);
               // 缓存存在，进行删除
               if (hasKey1) {
                   redisTemplate.delete(key1);
               }
               Boolean hasKey2 = redisTemplate.hasKey(key2);
               // 缓存存在，进行删除
               if (hasKey2) {
                   redisTemplate.delete(key2);
               }
               return true;
           }
           else{
               return false;
           }
       }
        return false;
    }


    /**
     * 修改一行数据：
     * 先修改mysql数据库，再将缓存的数据删除即可，不直接更新缓存，效率太低。
     */
    @Override
    public boolean updateSubway(Subway subway) {
        //返回影响行数，为1表示修改成功
        int result = jdbcTemplate.update("update subway set code=?,cn_name=?,cename=?,cpre,l_xmlattr=?,p=? where sid=? "
                ,subway.getCode(),subway.getCn_name(),subway.getCename(),subway.getCpre(), JSONObject.toJSONString(subway.getL_xmlattr()), JSONObject.toJSONString(subway.getP()),subway.getSid());
        if(result > 0){
            // 判断是否缓存存在
            String key = "subway_" + subway.getSid();
            String key1 = "subway_list";
            String key2 = "subway_"+subway.getCode();
            Boolean hasKey = redisTemplate.hasKey(key);
            // 缓存存在，进行删除
            if (hasKey) {
                redisTemplate.delete(key);
            }
            Boolean hasKey1 = redisTemplate.hasKey(key1);
            // 缓存存在，进行删除
            if (hasKey1) {
                redisTemplate.delete(key1);
            }
            Boolean hasKey2 = redisTemplate.hasKey(key2);
            // 缓存存在，进行删除
            if (hasKey2) {
                redisTemplate.delete(key2);
            }
            return true;
        }
        return false;
    }


    /**
     * 获取一行数据：
     * 如果缓存(redis)中存在，从缓存中获取信息
     * 如果缓存不存在，从 mysql中获取信息，然后插入缓存
     */
    @Override
    public Subway getSubway(String sid) {
        // 从缓存中 取出信息
        String key = "subway_" + sid;
        Boolean hasKey = redisTemplate.hasKey(key);

        ValueOperations operations = redisTemplate.opsForValue();
        // 缓存中存在
        if (hasKey) {
            String str = (String) operations.get(key);
            return new Gson().fromJson(str, Subway.class);
        }
        //缓存中不存在
        Map<String, Object> map = jdbcTemplate.queryForMap("select * from subway where sid = ?",sid);
        if(map==null){
            return null;
        }
        //sid,code,cn_name,cename,cpre,l_xmlattr,p
        List<Map<String,Object>> pmaplist = (List<Map<String,Object>>) JSONArray.parse((String) map.get("p"));
        Map<String, Object> l_xmlattrmap= JSONObject.parseObject((String)map.get("l_xmlattr"));
        Subway subway=new Subway(map.get("sid").toString(),Integer.parseInt(map.get("code").toString()),map.get("cn_name").toString(),map.get("cename").toString(),map.get("cpre").toString(),l_xmlattrmap,pmaplist);
        // 插入缓存中
        String str = new Gson().toJson(subway);
        operations.set(key, str,60*10, TimeUnit.SECONDS);//向redis里存入数据,设置缓存时间为10min

        return subway;
    }

    @Override
    public Map<String, Object> getAllSubways() {
        String key = "subway_list";
        Boolean hasKey = redisTemplate.hasKey(key);

        ValueOperations operations = redisTemplate.opsForValue();
        //缓存中存在
        if (hasKey) {
            return JSONObject.parseObject((String) operations.get(key));
        }
        //缓存中不存在
        List<Map<String, Object>> maplist =new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        //先找到所有城市代码
        List<Map<String, Object>> list1= jdbcTemplate.queryForList("select DISTINCT code from subway order by code ASC ");
       //依次找到线路和站点信息，并添加map
        for (Map<String, Object> temp : list1) {
            List<Map<String, Object>> sonmap1 =new ArrayList<Map<String, Object>>();
            Map<String, Object> map1 = new HashMap<String, Object>();
           List<Map<String, Object>> list2= jdbcTemplate.queryForList("select *  from subway where code=? order by sid ASC",temp.get("code"));
            //构建map数组
           for (int j = 0; j <list2.size(); j++) {
                List<Map<String,Object>> pmaplist = (List<Map<String,Object>>) JSONArray.parse((String) list2.get(j).get("p"));
                Map<String, Object> l_xmlattrmap= JSONObject.parseObject((String)list2.get(j).get("l_xmlattr"));
                Map<String, Object> sonmap = new HashMap<String, Object>();
                List<Map<String, Object>> sonmap2=new ArrayList<Map<String, Object>>();
                sonmap.put("p",pmaplist);
                sonmap2.add(l_xmlattrmap);
                sonmap.put("l_xmlattr", sonmap2);
                sonmap1.add(sonmap);
            }
            Map<String, Object> sonmap2 = new HashMap<String, Object>();
            sonmap2.put("code",list2.get(0).get("code"));
            sonmap2.put("cn_name",list2.get(0).get("cn_name"));
            sonmap2.put("cename",list2.get(0).get("cename"));
            map1.put("l",sonmap1);
            map1.put("sw_xmlattr",sonmap2);
            maplist.add(map1);
        }
        map.put("subways",maplist);
        // 加到缓存中
        //operations.set(key, maplist.toString(), 10*60, TimeUnit.SECONDS);
        return map;
    }

    @Override
    public Map<String, Object> getAllSubways(int code) {
        String key = "subway_"+code;
        Boolean hasKey = redisTemplate.hasKey(key);

        ValueOperations operations = redisTemplate.opsForValue();
        //缓存中存在
        if (hasKey) {
            return JSONObject.parseObject((String)operations.get(key));
        }
        //缓存中不存在
        List<Map<String, Object>> list= jdbcTemplate.queryForList("select * from subway where code = ? order by sid ASC ",code);
        //构建map数组
        List<Map<String, Object>> maplist =new ArrayList<Map<String, Object>>();
        for (int i = 0; i <list.size(); i++) {
            List<Map<String,Object>> pmaplist = (List<Map<String,Object>>) JSONArray.parse((String) list.get(i).get("p"));
            Map<String, Object> l_xmlattrmap= JSONObject.parseObject((String)list.get(i).get("l_xmlattr"));
            Map<String, Object> sonmap = new HashMap<String, Object>();
            List<Map<String, Object>> sonmap2=new ArrayList<Map<String, Object>>();
            sonmap.put("p",pmaplist);
            sonmap2.add(l_xmlattrmap);
            sonmap.put("l_xmlattr", sonmap2);
            maplist.add(sonmap);
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("l",maplist);
        String toJson = JSON.toJSONString(map);
        // 加到缓存中
        operations.set(key, toJson, 10*60, TimeUnit.SECONDS);
        return map;
    }
}
