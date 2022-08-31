package com.subway_footprint_system.springboot_project.Dao.Impl;

import com.google.gson.Gson;
import com.subway_footprint_system.springboot_project.Dao.IUserDao;
import com.subway_footprint_system.springboot_project.Pojo.User;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Repository
public class UserDaoImpl implements IUserDao {

    @Autowired
    private StringEncryptor encryptor;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 添加一行数据：
     * 直接添加到mysql数据库
     */
    @Override
    public boolean insertUser(User user) {
        try {
            //返回影响行数，为1即增加成功
            int result = jdbcTemplate.update("insert into user(uid,username,password,email,age,sex,tel,touxiang,qianming,credit) values(?,?,?,?,?,?,?,?,?,?)",
                    user.getUid(), user.getUsername(), encryptor.encrypt(user.getPassword()), user.getEmail(), user.getAge(), user.getSex(), user.getTel(), user.getTouxiang(), user.getQianming(), user.getCredit());
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 删除一行数据：
     * 先删除mysql数据库，再将缓存的数据删除即可
     */
    @Override
    public boolean deleteUser(String uid) {
        int result = jdbcTemplate.update("delete from user where uid = ?", uid);
        if (result != 0) {
            // 判断是否缓存存在
            String key1 = "user_" + uid;
            Boolean hasKey1 = redisTemplate.hasKey(key1);
            // 缓存存在，进行删除
            if (hasKey1) {
                redisTemplate.delete(key1);
            }
            return true;
        } else {
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
        int result = jdbcTemplate.update("update user set username=?,age=?,sex=?,tel=?,touxiang=?,qianming=?,credit=? where uid=?"
                , user.getUsername(), user.getAge(), user.getSex(), user.getTel(), user.getTouxiang(), user.getQianming(), user.getCredit(), user.getUid());
        if (result > 0) {
            // 判断是否缓存存在
            String key1 = "user_" + user.getUid();
            Boolean hasKey1 = redisTemplate.hasKey(key1);
            // 缓存存在，进行删除
            if (hasKey1) {
                redisTemplate.delete(key1);
            }

            return true;
        }
        return false;
    }

    @Override
    public boolean changePassword(String uid, String password) {
        //返回影响行数，为1表示修改成功
        int result = jdbcTemplate.update("update user set password=? where uid=?", encryptor.encrypt(password), uid);
        if (result > 0) {
            // 判断是否缓存存在
            String key1 = "user_" + uid;
            Boolean hasKey1 = redisTemplate.hasKey(key1);
            // 缓存存在，进行删除
            if (hasKey1) {
                redisTemplate.delete(key1);
            }

            return true;
        }
        return false;
    }

    /**
     * 通过uid获取一行数据：
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
            User user = new Gson().fromJson(str, User.class);
            user.setPassword(encryptor.decrypt(user.getPassword()));
            return user;
        }
        //缓存中不存在
        RowMapper<User> rowMapper = new BeanPropertyRowMapper<User>(User.class);
        Object object = null;
        //queryForObject会抛出非检查性异常DataAccessException，同时对返回值进行requiredSingleResult操作
        //requiredSingleResult会在查询结果为空的时候抛出EmptyResultDataAccessException异常，需要捕获后进行处理
        try {
            object = jdbcTemplate.queryForObject("select * from user where uid = ?", rowMapper, uid);
        } catch (EmptyResultDataAccessException e1) {
            //查询结果为空，返回null
            return null;
        }
        User user = (User) object;
        // 插入缓存中
        String str = new Gson().toJson(user);
        operations.set(key, str, 60 * 10, TimeUnit.SECONDS);//向redis里存入数据,设置缓存时间为10min
        user.setPassword(encryptor.decrypt(user.getPassword()));
        return user;

    }

    /*
     * 通过Email获取用户数据，因发生频率较低，故直接查询mysql
     * */
    @Override
    public User getUserByEmail(String email) {

        RowMapper<User> rowMapper = new BeanPropertyRowMapper<User>(User.class);
        Object object = null;
        //queryForObject会抛出非检查性异常DataAccessException，同时对返回值进行requiredSingleResult操作
        //requiredSingleResult会在查询结果为空的时候抛出EmptyResultDataAccessException异常，需要捕获后进行处理
        try {
            object = jdbcTemplate.queryForObject("select * from user where email = ?", rowMapper, email);
        } catch (EmptyResultDataAccessException e1) {
            //查询结果为空，返回null
            return null;
        }
        User user = (User) object;
        user.setPassword(encryptor.decrypt(user.getPassword()));
        return user;

    }

    /*
     * 通过用户名获取用户数据，因发生频率较低，故直接查询mysql
     * */
    @Override
    public User getUserByUsername(String username) {

        RowMapper<User> rowMapper = new BeanPropertyRowMapper<User>(User.class);
        Object object = null;
        //queryForObject会抛出非检查性异常DataAccessException，同时对返回值进行requiredSingleResult操作
        //requiredSingleResult会在查询结果为空的时候抛出EmptyResultDataAccessException异常，需要捕获后进行处理
        try {
            object = jdbcTemplate.queryForObject("select * from user where username = ?", rowMapper, username);
        } catch (EmptyResultDataAccessException e1) {
            //查询结果为空，返回null
            return null;
        }

        User user = (User) object;
        user.setPassword(encryptor.decrypt(user.getPassword()));
        return user;

    }

    /*
     * 获取所有用户数据，因用户较多，数据易发生改变，故直接查询mysql
     *
     * */
    @Override
    public List<User> getAllUsers() {
        List<User> list = null;
        try {
            RowMapper<User> rowMapper = new BeanPropertyRowMapper<User>(User.class);
            list = jdbcTemplate.query("select * from user order by uid ASC ", rowMapper);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        for (User user : list) {
            user.setPassword(encryptor.decrypt(user.getPassword()));
        }
        return list;
    }

    public List<User> getRankingList() {
        List<User> list = null;
        try {
            RowMapper<User> rowMapper = new BeanPropertyRowMapper<User>(User.class);
            list = jdbcTemplate.query("select * from user order by credit desc LIMIT 10", rowMapper);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return list;
    }

    public int getPersonalCreditRank(String uid) {
        int credit = 0;
        String sql = "SELECT obj_new.rownum FROM(SELECT obj.uid,obj.credit," +
                "            @rownum := @rownum + 1 AS num_tmp," +
                "            @incrnum := CASE" +
                "        WHEN @rowtotal = obj.credit THEN" +
                "            @incrnum" +
                "        WHEN @rowtotal := obj.credit THEN" +
                "            @rownum" +
                "        END AS rownum" +
                "        FROM(SELECT uid,credit FROM `user` ORDER BY credit DESC) " +
                "   AS obj,(SELECT @rownum := 0 ,@rowtotal := NULL ,@incrnum := 0) r)" +
                " AS obj_new ";


        try {
            Map<String, Object> map = jdbcTemplate.queryForMap(sql + "where uid=?", uid);
            System.out.println(map.size());
            credit = Integer.parseInt(map.get("rownum").toString());
        } catch (Exception e) {
            e.printStackTrace();
            return credit;
        }
        return credit;
    }
}
