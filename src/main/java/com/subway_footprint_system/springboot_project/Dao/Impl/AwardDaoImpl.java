package com.subway_footprint_system.springboot_project.Dao.Impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.subway_footprint_system.springboot_project.Dao.IAwardDao;
import com.subway_footprint_system.springboot_project.Pojo.Award;
import com.subway_footprint_system.springboot_project.Pojo.Subway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Repository
public class AwardDaoImpl implements IAwardDao {
    /*
     * 对于该表数据而言，查询频率较低，故不使用redis，直接对mysql操作
     * */
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 添加一行数据：
     * 直接添加到mysql数据库
     */
    @Override
    public boolean insertAward(Award award) {
        //返回影响行数，为1即增加成功
        try {
            int result= jdbcTemplate.update("insert into award(aid, variety, num, name, content, credit, fromdate, todate, mid) values(?,?,?,?,?,?,?,?,?)",
                    award.getAid(),award.getVariety(),award.getNum(),award.getName(),award.getContent(),award.getCredit(),award.getFromdate(),award.getTodate(),award.getMid());
            return result == 1;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }

    /**
     * 删除一行数据：
     * 先删除mysql数据库，再将缓存的数据删除即可
     */
    @Override
    public boolean deleteAward(String aid) {
        Award award= getAward(aid);
        if(award!=null){
            int result= jdbcTemplate.update("delete from award where aid = ?",aid);
            if(result!=0){
                // 判断是否缓存存在
                String key = "award_" + aid;
                Boolean hasKey = redisTemplate.hasKey(key);
                // 缓存存在，进行删除
                if (hasKey) {
                    redisTemplate.delete(key);
                }
                return true;
            }
        }
        return false;
    }
    /**
     * 修改一行数据：
     * 先修改mysql数据库，再将缓存的数据删除即可，不直接更新缓存，效率太低。
     */
    @Override
    public boolean updateAward(Award award) {
        //返回影响行数，为1表示修改成功
        int result = jdbcTemplate.update("update award set variety=?, num=?, name=?, content=?, credit=?,todate=? where aid=? ",
                award.getVariety(),award.getNum(),award.getName(),award.getContent(),award.getCredit(),award.getTodate(),award.getAid());
        if(result!=0){
            // 判断是否缓存存在
            String key = "award_" + award.getAid();
            Boolean hasKey = redisTemplate.hasKey(key);
            // 缓存存在，进行删除
            if (hasKey) {
                redisTemplate.delete(key);
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
    public Award getAward(String aid) {
        // 从缓存中 取出信息
        String key = "award_" + aid;
        Boolean hasKey = redisTemplate.hasKey(key);

        ValueOperations operations = redisTemplate.opsForValue();
        // 缓存中存在
        if (hasKey) {
            String str = (String) operations.get(key);
            return new Gson().fromJson(str, Award.class);
        }
        //缓存中不存在
        try {
            RowMapper<Award> rowMapper = new BeanPropertyRowMapper<Award>(Award.class);
            Object object = jdbcTemplate.queryForObject("select * from award where aid=?",rowMapper,aid);
            // 插入缓存中
            String str = new Gson().toJson(object);
            operations.set(key, str, 60 * 10, TimeUnit.SECONDS);//向redis里存入数据,设置缓存时间为10min
            return (Award) object;
        }catch (EmptyResultDataAccessException e) {
            //查询结果为空
            return null;
        }
    }

    @Override
    public List<Award> getSomeAwards(Integer num) {
        List<Award> list=null;
        List<Award> list2=new ArrayList<>();
        try{
            RowMapper<Award> rowMapper = new BeanPropertyRowMapper<Award>(Award.class);
            list= jdbcTemplate.query("select * from award where status=0 and num>0  ",rowMapper);
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
        for (int i = 0; i < num; i++) {
            int j = (int) (Math.random() * (list.size()-1));
            list2.add(list.get(j));
            list.remove(j);
        }
        return list2;
    }

}
