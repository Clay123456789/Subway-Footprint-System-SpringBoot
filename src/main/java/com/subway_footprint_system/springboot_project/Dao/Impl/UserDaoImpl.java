package com.subway_footprint_system.springboot_project.Dao.Impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.subway_footprint_system.springboot_project.Dao.IUserDao;
import com.subway_footprint_system.springboot_project.Pojo.User;
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
public class UserDaoImpl implements IUserDao {
    @Autowired
    private LightedStationDaoImpl lightedStationDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 添加一行数据：
     * 直接添加到mysql数据库,将缓存中数据所在表删除（为了保证selectAll获取数据一致）
     */
    @Override
    public boolean insertUser(User user) {
        //返回影响行数，为1即增加成功
        int result = jdbcTemplate.update("insert into user(uid,username,password,email,age,sex,tel,touxiang,qianming,stationtable) values(?,?,?,?,?,?,?,?,?,?)",
                user.getUid(),user.getUsername(),user.getPassword(),user.getEmail(),user.getAge(),user.getSex(),user.getTel(),user.getTouxiang(),user.getQianming(),user.getStationtable());
        if(result>0){
            // 判断是否缓存存在
            String key = "user_list";
            Boolean hasKey = redisTemplate.hasKey(key);
            // 缓存存在，进行删除
            if (hasKey) {
                redisTemplate.delete(key);
            }
            //创建user后需要创建lightedStation表
            if(!lightedStationDao.TableExist(user.getUid())) {
                lightedStationDao.createLightedStationTable(user.getUid());
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
    public boolean deleteUser(String uid) {
        int result = jdbcTemplate.update("delete from user where uid = ?",uid);
        if(result!=0){
            // 判断是否缓存存在
            String key1 = "user_" + uid;
            String key2 = "user_list";
            Boolean hasKey1 = redisTemplate.hasKey(key1);
            // 缓存存在，进行删除
            if (hasKey1) {
                redisTemplate.delete(key1);
            }
            // 判断是否缓存存在
            Boolean hasKey2 = redisTemplate.hasKey(key2);
            // 缓存存在，进行删除
            if (hasKey2) {
                redisTemplate.delete(key2);
            }
            //删除user后需要级联删除lightedStation表
            if(lightedStationDao.TableExist(uid)) {
                return lightedStationDao.deleteLightedStationTable(uid);
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
    public boolean updateUser(User user) {

        //返回影响行数，为1表示修改成功
        int result = jdbcTemplate.update("update user set username=?,password=?,email=?,age=?,sex=?,tel=?,touxiang=?,qianming=?,stationtable=? where uid=?"
                ,user.getUsername(),user.getPassword(),user.getEmail(),user.getAge(),user.getSex(),user.getTel(),user.getTouxiang(),user.getQianming(),user.getUid());
        if(result > 0){
            // 判断是否缓存存在
            String key1 = "user_" + user.getUid();
            String key2 = "user_list";
            Boolean hasKey1 = redisTemplate.hasKey(key1);
            // 缓存存在，进行删除
            if (hasKey1) {
                redisTemplate.delete(key1);
            }
            // 判断是否缓存存在
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
    public User getUserByUid(String uid) {

        // 从缓存中 取出信息
        String key = "user_" + uid;
        Boolean hasKey = redisTemplate.hasKey(key);

        ValueOperations operations = redisTemplate.opsForValue();
        // 缓存中存在
        if (hasKey) {
            String str = (String) operations.get(key);
            return new Gson().fromJson(str, User.class);
        }
        //缓存中不存在
        RowMapper<User> rowMapper = new BeanPropertyRowMapper<User>(User.class);
        Object object = null;
        //queryForObject会抛出非检查性异常DataAccessException，同时对返回值进行requiredSingleResult操作
        //requiredSingleResult会在查询结果为空的时候抛出EmptyResultDataAccessException异常，需要捕获后进行处理
        try {
            object = jdbcTemplate.queryForObject("select * from user where uid = ?",rowMapper,uid);
        } catch (EmptyResultDataAccessException e1) {
            //查询结果为空，返回null
            return null;
        }
        User user=(User) object;
        // 插入缓存中
        String str = new Gson().toJson(user);
        operations.set(key, str,60*10, TimeUnit.SECONDS);//向redis里存入数据,设置缓存时间为10min

        return user;

    }

    @Override
    public User getUserByEmail(String email) {

        // 从缓存中 取出学生信息
        String key = "user_" + email;
        Boolean hasKey = redisTemplate.hasKey(key);

        ValueOperations operations = redisTemplate.opsForValue();
        // 缓存中存在
        if (hasKey) {
            String str = (String) operations.get(key);
            return new Gson().fromJson(str, User.class);
        }
        //缓存中不存在
        RowMapper<User> rowMapper = new BeanPropertyRowMapper<User>(User.class);
        Object object = null;
        //queryForObject会抛出非检查性异常DataAccessException，同时对返回值进行requiredSingleResult操作
        //requiredSingleResult会在查询结果为空的时候抛出EmptyResultDataAccessException异常，需要捕获后进行处理
        try {
            object = jdbcTemplate.queryForObject("select * from user where email = ?",rowMapper,email);
        } catch (EmptyResultDataAccessException e1) {
            //查询结果为空，返回null
            return null;
        }
        User user=(User) object;
        // 插入缓存中
        String str = new Gson().toJson(user);
        operations.set(key, str,60*10, TimeUnit.SECONDS);//向redis里存入数据,设置缓存时间为10min

        return user;

    }
    @Override
    public User getUserByUsername(String username) {

        // 从缓存中 取出学生信息
        String key = "user_" + username;
        Boolean hasKey = redisTemplate.hasKey(key);

        ValueOperations operations = redisTemplate.opsForValue();
        // 缓存中存在
        if (hasKey) {
            String str = (String) operations.get(key);
            return new Gson().fromJson(str, User.class);
        }
        //缓存中不存在
        RowMapper<User> rowMapper = new BeanPropertyRowMapper<User>(User.class);
        Object object = null;
        //queryForObject会抛出非检查性异常DataAccessException，同时对返回值进行requiredSingleResult操作
        //requiredSingleResult会在查询结果为空的时候抛出EmptyResultDataAccessException异常，需要捕获后进行处理
        try {
            object = jdbcTemplate.queryForObject("select * from user where username = ?",rowMapper,username);
        } catch (EmptyResultDataAccessException e1) {
            //查询结果为空，返回null
            return null;
        }
        User user=(User) object;
        // 插入缓存中
        String str = new Gson().toJson(user);
        operations.set(key, str,60*10, TimeUnit.SECONDS);//向redis里存入数据,设置缓存时间为10min

        return user;

    }

    @Override
    public List<User> getAllUsers() {
        String key = "user_list";
        Boolean hasKey = redisTemplate.hasKey(key);

        ValueOperations operations = redisTemplate.opsForValue();
        //缓存中存在
        if (hasKey) {
            String redisList = (String) operations.get(key);

            Type type = new TypeToken<List<User>>() {}.getType();
            List<User> list =  new Gson().fromJson(redisList,type);
            return list;
        }
        //缓存中不存在
        RowMapper<User> rowMapper = new BeanPropertyRowMapper<User>(User.class);
        List<User> list = jdbcTemplate.query("select * from user order by uid ASC ",rowMapper);
        String toJson = new Gson().toJson(list);
        // 加到缓存中
        operations.set(key, toJson, 60*10, TimeUnit.SECONDS);
        return list;
    }
}
