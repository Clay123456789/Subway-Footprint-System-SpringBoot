package com.subway_footprint_system.springboot_project.Dao.Impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.subway_footprint_system.springboot_project.Dao.ILightedStationDao;
import com.subway_footprint_system.springboot_project.Pojo.LightedStation;
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
public class LightedStationDaoImpl implements ILightedStationDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 判断用户点亮的站点表是否存在：
     * 如果缓存(redis)中存在，返回真
     * 如果缓存不存在，从 mysql中判断，存在返回真并插入缓存
     */
    public boolean TableExist(String uid){
        String tablename="lightedstation_"+ uid;
        // 从缓存中判断是否存在
        String key = "lightedstation_has_" + tablename;
        Boolean hasKey = redisTemplate.hasKey(key);
        ValueOperations operations = redisTemplate.opsForValue();
        // 缓存中存在
        if (hasKey) {
            return true;
        }
        //缓存中不存在
        //判断mysql数据库中存不存在
        //catch异常的笨蛋方法，待改进...
        try{
            String sql="SELECT COUNT(*) from " + "`"+tablename+ "`";
            RowMapper<LightedStation> rowMapper = new BeanPropertyRowMapper<LightedStation>(LightedStation.class);
            jdbcTemplate.query(sql,rowMapper);
        }catch (Exception e){
            return false;
        }
        //存在
        // 插入缓存中
        operations.set(key, true,60*10, TimeUnit.SECONDS);//向redis里存入数据,设置缓存时间为10min
        return true;
    }

    @Override
    public boolean createLightedStationTable(String uid) {
        if(TableExist(uid)) {
            return false;
        }
        String tablename="lightedstation_"+ uid;
        StringBuffer sb = new StringBuffer("");
        sb.append("CREATE TABLE `" + tablename + "` (");
        sb.append(" `pid` varchar(64) NOT NULL ,");
        sb.append(" `point` varchar(64) DEFAULT '',");
        sb.append(" `time` varchar(64) DEFAULT '',");
        sb.append(" PRIMARY KEY (`pid`)");
        sb.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8");
        try {
            jdbcTemplate.update(sb.toString());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 添加一行数据：
     * 直接添加到mysql数据库,将缓存中数据所在表删除（为了保证selectAll获取数据一致）
     */
    @Override
    public boolean insertLightedStation(LightedStation lightedStation) {
        //先判断表是否存在，不在则新建
        if(!TableExist(lightedStation.getUid())){
            createLightedStationTable(lightedStation.getUid());
        }
        String tablename="lightedstation_"+ lightedStation.getUid();
        String sql="insert into "+"`"+tablename+"`"+" (pid,point,time) values(?,?,?)";
        //返回影响行数，为1即增加成功
        int result= jdbcTemplate.update(sql,lightedStation.getPid(),lightedStation.getPoint(),lightedStation.getTime());
        if(result>0){
            // 判断是否缓存存在
            String key = tablename+"_list";
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

    @Override
    public boolean deleteLightedStationTable(String uid) {
        //先判断表是否存在
        if(!TableExist(uid)){
            return false;
        }
        String tablename="lightedstation_"+ uid;
        String sql = "DROP TABLE "+tablename;
        try {
            int result=jdbcTemplate.update(sql);
            if(result>0){
                // 判断是否缓存存在
                String key = "lightedstation_has_" + tablename;
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除一行数据：
     * 先删除mysql数据库，再将缓存的数据删除即可
     */
    @Override
    public boolean deleteLightedStation(String uid,String pid) {
        //先判断表是否存在
        if(!TableExist(uid)){
            return false;
        }
        String tablename="lightedstation_"+ uid;
        String sql="delete from "+tablename+" where pid = ?";
        int result= jdbcTemplate.update(sql,pid);
        if(result!=0){
            // 判断是否缓存存在
            String key = tablename+"_"+pid;
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
    public boolean updateLightedStation(LightedStation lightedStation) {
        //先判断表是否存在
        if(!TableExist(lightedStation.getUid())){
            return false;
        }
        String tablename="lightedstation_"+ lightedStation.getUid();
        String sql="update "+ "`"+tablename+ "`"+" set point=? where pid = ?";
        //返回影响行数，为1表示修改成功
        int result = jdbcTemplate.update(sql,lightedStation.getPoint(),lightedStation.getPid());
        if(result > 0){
            // 判断是否缓存存在
            String key1 = tablename+"_" + lightedStation.getPid();
            String key2 = tablename+"_list";
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
        return false;
    }

    /**
     * 获取一行数据：
     * 如果缓存(redis)中存在，从缓存中获取信息
     * 如果缓存不存在，从 mysql中获取信息，然后插入缓存
     */
    @Override
    public LightedStation getLightedStation(String uid,String pid) {
        //先判断表是否存在
        if(!TableExist(uid)){
            return null;
        }
        // 从缓存中 取出信息
        String tablename="lightedstation_"+uid;
        String key = tablename+"_"+pid;
        Boolean hasKey = redisTemplate.hasKey(key);
        ValueOperations operations = redisTemplate.opsForValue();
        // 缓存中存在
        if (hasKey) {
            String str = (String) operations.get(key);
            return new Gson().fromJson(str, LightedStation.class);
        }
        //缓存中不存在
        RowMapper<LightedStation> rowMapper = new BeanPropertyRowMapper<LightedStation>(LightedStation.class);
        String sql="select * from "+ "`"+tablename+ "`"+" where pid = ?";
        Object object = null;
        //queryForObject会抛出非检查性异常DataAccessException，同时对返回值进行requiredSingleResult操作
        //requiredSingleResult会在查询结果为空的时候抛出EmptyResultDataAccessException异常，需要捕获后进行处理
        try {
            object = jdbcTemplate.queryForObject(sql,rowMapper,pid);
        } catch (EmptyResultDataAccessException e1) {
            //查询结果为空，返回null
            return null;
        }
        LightedStation lightedStation=(LightedStation) object;
        lightedStation.setUid(uid);
        // 插入缓存中
        String str = new Gson().toJson(lightedStation);
        operations.set(key, str,60*10, TimeUnit.SECONDS);//向redis里存入数据,设置缓存时间为10min

        return lightedStation;
    }

    @Override
    public List<LightedStation> getUserLightedStations(String uid) {
        //先判断表是否存在
        if(!TableExist(uid)){
            return null;
        }
        // 从缓存中 取出信息
        String tablename="lightedstation_"+uid;
        String key = tablename+"_list";
        Boolean hasKey = redisTemplate.hasKey(key);
        ValueOperations operations = redisTemplate.opsForValue();
        // 缓存中存在
        if (hasKey) {
            String redisList = (String) operations.get(key);
            Type type = new TypeToken<List<LightedStation>>() {}.getType();
            List<LightedStation> list =  new Gson().fromJson(redisList,type);
            return list;
        }
        //缓存中不存在
        String sql="select * from "+ "`"+tablename+ "`"+" order by pid ASC";
        RowMapper<LightedStation> rowMapper = new BeanPropertyRowMapper<LightedStation>(LightedStation.class);
        List<LightedStation> list = jdbcTemplate.query(sql,rowMapper);
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setUid(uid);
        }
        // 插入缓存中
        String str = new Gson().toJson(list);
        operations.set(key, str,60*10, TimeUnit.SECONDS);//向redis里存入数据,设置缓存时间为10min

        return list;
    }
}
