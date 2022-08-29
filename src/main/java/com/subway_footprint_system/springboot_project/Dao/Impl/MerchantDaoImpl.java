package com.subway_footprint_system.springboot_project.Dao.Impl;

import com.google.gson.Gson;
import com.subway_footprint_system.springboot_project.Dao.IMerchantDao;
import com.subway_footprint_system.springboot_project.Pojo.Merchant;
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
import java.util.concurrent.TimeUnit;

@Repository
public class MerchantDaoImpl implements IMerchantDao {

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
    public boolean insertMerchant(Merchant merchant) {
        try {
            //返回影响行数，为1即增加成功
            int result = jdbcTemplate.update("insert into merchant(mid,account,name,password,email,tel,location,authentication,authenticated,time,info) values(?,?,?,?,?,?,?,?,?,?,?)",
                    merchant.getMid(),merchant.getAccount(),merchant.getName(),encryptor.encrypt(merchant.getPassword()),merchant.getEmail(),merchant.getTel(),merchant.getLocation(),null,-1,null,merchant.getInfo());
            return result > 0;
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
    public boolean deleteMerchant(String mid) {
        int result = jdbcTemplate.update("delete from merchant where mid = ?",mid);
        if(result!=0){
            // 判断是否缓存存在
            String key1 = "merchant_" + mid;
            Boolean hasKey1 = redisTemplate.hasKey(key1);
            // 缓存存在，进行删除
            if (hasKey1) {
                redisTemplate.delete(key1);
            }
            return true;
        }
        else{
            return false;
        }
    }
    /**
     * 修改商户基本信息：
     * 先修改mysql数据库，再将缓存的数据删除即可，不直接更新缓存，效率太低。
     */
    @Override
    public boolean updateMerchant(Merchant merchant) {
        //返回影响行数，为1表示修改成功
        int result = jdbcTemplate.update("update merchant set account=?,name=?,tel=?,location=?,info=? where mid=?"
                ,merchant.getAccount(),merchant.getName(),merchant.getTel(),merchant.getLocation(),merchant.getInfo(),merchant.getMid());
        if(result > 0){
            // 判断是否缓存存在
            String key1 = "merchant_" + merchant.getMid();
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
     * 修改商户认证信息：
     *
     */
    @Override
    public boolean updateAuthentication(Merchant merchant) {
        //返回影响行数，为1表示修改成功
        int result = jdbcTemplate.update("update merchant set authentication=?,authenticated=?,time=? where mid=?"
                ,merchant.getAuthentication(),merchant.getAuthenticated(),merchant.getTime(),merchant.getMid());
        if(result > 0){
            // 判断是否缓存存在
            String key1 = "merchant_" + merchant.getMid();
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
    public boolean changePassword(String mid, String password) {
        //返回影响行数，为1表示修改成功
        int result = jdbcTemplate.update("update merchant set password=? where mid=?",encryptor.encrypt(password),mid);
        if(result > 0){
            // 判断是否缓存存在
            String key1 = "merchant_" + mid;
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
     * 通过mid获取一行数据：
     * 如果缓存(redis)中存在，从缓存中获取信息
     * 如果缓存不存在，从 mysql中获取信息，然后插入缓存
     */
    @Override
    public Merchant getMerchantByMid(String mid) {
        // 从缓存中 取出信息
        String key = "merchant_" + mid;
        Boolean hasKey = redisTemplate.hasKey(key);

        ValueOperations operations = redisTemplate.opsForValue();
        // 缓存中存在
        if (hasKey) {
            String str = (String) operations.get(key);
            Merchant merchant=new Gson().fromJson(str, Merchant.class);
            merchant.setPassword(encryptor.decrypt(merchant.getPassword()));
            return merchant;
        }
        //缓存中不存在
        RowMapper<Merchant> rowMapper = new BeanPropertyRowMapper<Merchant>(Merchant.class);
        Object object = null;
        //queryForObject会抛出非检查性异常DataAccessException，同时对返回值进行requiredSingleResult操作
        //requiredSingleResult会在查询结果为空的时候抛出EmptyResultDataAccessException异常，需要捕获后进行处理
        try {
            object = jdbcTemplate.queryForObject("select * from merchant where mid = ?",rowMapper,mid);
        } catch (EmptyResultDataAccessException e1) {
            //查询结果为空，返回null
            return null;
        }
        Merchant merchant=(Merchant) object;
        // 插入缓存中
        String str = new Gson().toJson(merchant);
        operations.set(key, str,60*10, TimeUnit.SECONDS);//向redis里存入数据,设置缓存时间为10min
        merchant.setPassword(encryptor.decrypt(merchant.getPassword()));
        return merchant;
    }
    /*
     * 通过Email获取商户数据，因发生频率较低，故直接查询mysql
     * */
    @Override
    public Merchant getMerchantByEmail(String email) {
        RowMapper<Merchant> rowMapper = new BeanPropertyRowMapper<Merchant>(Merchant.class);
        Object object = null;
        //queryForObject会抛出非检查性异常DataAccessException，同时对返回值进行requiredSingleResult操作
        //requiredSingleResult会在查询结果为空的时候抛出EmptyResultDataAccessException异常，需要捕获后进行处理
        try {
            object = jdbcTemplate.queryForObject("select * from merchant where email = ?",rowMapper,email);
        } catch (EmptyResultDataAccessException e1) {
            //查询结果为空，返回null
            return null;
        }
        Merchant merchant=(Merchant)object;
        merchant.setPassword(encryptor.decrypt(merchant.getPassword()));
        return merchant;
    }
    /*
     * 通过账号获取商户数据，因发生频率较低，故直接查询mysql
     * */
    @Override
    public Merchant getMerchantByAccount(String account) {
        RowMapper<Merchant> rowMapper = new BeanPropertyRowMapper<Merchant>(Merchant.class);
        Object object = null;
        //queryForObject会抛出非检查性异常DataAccessException，同时对返回值进行requiredSingleResult操作
        //requiredSingleResult会在查询结果为空的时候抛出EmptyResultDataAccessException异常，需要捕获后进行处理
        try {
            object = jdbcTemplate.queryForObject("select * from merchant where account = ?",rowMapper,account);
        } catch (EmptyResultDataAccessException e1) {
            //查询结果为空，返回null
            return null;
        }
        Merchant merchant=(Merchant)object;
        merchant.setPassword(encryptor.decrypt(merchant.getPassword()));
        return merchant;
    }
    /*
     * 获取所有用户数据，因用户较多，数据易发生改变，故直接查询mysql
     *
     * */
    @Override
    public List<Merchant> getAllMerchants() {
        List<Merchant> list=null;
        try{
            RowMapper<Merchant> rowMapper = new BeanPropertyRowMapper<Merchant>(Merchant.class);
            list= jdbcTemplate.query("select * from merchant order by mid ASC ",rowMapper);
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
        for (Merchant merchant:list) {
            merchant.setPassword(encryptor.decrypt(merchant.getPassword()));
        }
        return list;
    }
    @Override
    public List<Merchant> getAllUnAuthenticatedMerchants() {
        List<Merchant> list=null;
        try{
            RowMapper<Merchant> rowMapper = new BeanPropertyRowMapper<Merchant>(Merchant.class);
            list= jdbcTemplate.query("select * from merchant where merchant.authenticated=0 order by time DESC",rowMapper);
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
        for (Merchant merchant:list) {
            merchant.setPassword(encryptor.decrypt(merchant.getPassword()));
        }
        return list;
    }
}
