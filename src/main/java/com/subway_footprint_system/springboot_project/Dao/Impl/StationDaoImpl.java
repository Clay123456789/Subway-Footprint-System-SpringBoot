package com.subway_footprint_system.springboot_project.Dao.Impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.subway_footprint_system.springboot_project.Dao.IStationDao;
import com.subway_footprint_system.springboot_project.Pojo.Station;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Repository
public class StationDaoImpl implements IStationDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 添加一行数据：
     * 直接添加到mysql数据库,将缓存中数据所在表删除（为了保证selectAll获取数据一致）
     */
    @Override
    public boolean insert(Station station) {
        //返回影响行数，为1即增加成功
        int result= jdbcTemplate.update("insert into Station(SID,SName,Longitude,Latitude,Route) values(?,?,?,?,?)",
                station.getSID(),station.getSName(),station.getLongitude(),station.getLatitude(),station.getRoute());
        if(result>0){
            // 判断是否缓存存在
            String key = "station_List";
            Boolean hasKey = redisTemplate.hasKey(key);
            // 缓存存在，进行删除
            if (hasKey) {
                redisTemplate.delete(key);
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
    public boolean delete(Station station) {
        int  result=   jdbcTemplate.update("delete from Station where SID = ?",station.getSID());
        if(result!=0){
            // 判断是否缓存存在
            String key = "station_" + station.getSID();
            Boolean hasKey = redisTemplate.hasKey(key);
            // 缓存存在，进行删除
            if (hasKey) {
                redisTemplate.delete(key);
            }
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * 修改一行数据：
     * 先修改mysql数据库，再将缓存的数据删除即可，不直接更新缓存，效率太低。
     */
    @Override
    public boolean update(Station station) {

        //返回影响行数，为1表示修改成功
        int result= jdbcTemplate.update("insert into Station(SID,SName,Longitude,Latitude,Route) values(?,?,?,?,?)",
                station.getSID(),station.getSName(),station.getLongitude(),station.getLatitude(),station.getRoute());
        if(result > 0){
            // 判断是否缓存存在
            String key = "station_" + station.getSID();
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
    public Station select(Station station) {

        // 从缓存中 取出学生信息
        String key = "station_" + station.getSID();
        Boolean hasKey = redisTemplate.hasKey(key);

        ValueOperations operations = redisTemplate.opsForValue();
        // 缓存中存在
        if (hasKey) {
            String str = (String) operations.get(key);
            return new Gson().fromJson(str, Station.class);
        }
        //缓存中不存在
        RowMapper<Station> rowMapper = new BeanPropertyRowMapper<Station>(Station.class);
        Object object = null;
        //queryForObject会抛出非检查性异常DataAccessException，同时对返回值进行requiredSingleResult操作
        //requiredSingleResult会在查询结果为空的时候抛出EmptyResultDataAccessException异常，需要捕获后进行处理
        try {
            object = jdbcTemplate.queryForObject("select * from Station where SID = ?",rowMapper,station.getSID());
        } catch (EmptyResultDataAccessException e1) {
            //查询结果为空，返回null
            return null;
        }
        Station station1=(Station) object;
        // 插入缓存中
        String str = new Gson().toJson(station1);
        operations.set(key, str,60*10, TimeUnit.SECONDS);//向redis里存入数据,设置缓存时间为10min

        return station1;

    }

    @Override
    public List<Station> selectAll(Station station) {
        String key = "station_List";
        Boolean hasKey = redisTemplate.hasKey(key);

        ValueOperations operations = redisTemplate.opsForValue();
        //缓存中存在
        if (hasKey) {
            String redisList = (String) operations.get(key);

            Type type = new TypeToken<List<Station>>() {}.getType();
            List<Station> list =  new Gson().fromJson(redisList,type);
            return list;
        }
        //缓存中不存在
        RowMapper<Station> rowMapper = new BeanPropertyRowMapper<Station>(Station.class);
        List<Station> list = jdbcTemplate.query("select * from Station order by SID DESC ",rowMapper);
        String toJson = new Gson().toJson(list);
        // 加到缓存中
        operations.set(key, toJson, 10, TimeUnit.SECONDS);
        return list;
    }
}
