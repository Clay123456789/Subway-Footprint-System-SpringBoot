package com.subway_footprint_system.springboot_project.Dao.Impl;

import com.google.gson.Gson;
import com.subway_footprint_system.springboot_project.Dao.IAwardRecordDao;
import com.subway_footprint_system.springboot_project.Pojo.AwardRecord;
import com.subway_footprint_system.springboot_project.Pojo.User;
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
public class AwardRecordDaoImpl implements IAwardRecordDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public boolean insertMysqlAwardRecord(AwardRecord awardRecord) {
        try {
            //返回影响行数，为1即增加成功
            int result = jdbcTemplate.update("insert into award_record (arid, operation, uid, mid, aid, num, time, credit) values(?,?,?,?,?,?,?,?)",
                    awardRecord.getArid(),awardRecord.getOperation(),awardRecord.getUid(),awardRecord.getMid(),awardRecord.getAid(),awardRecord.getNum(),awardRecord.getTime(),awardRecord.getCredit());
            if(1==result){
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return false;
    }

    @Override
    public boolean insertRedisAwardRecord(AwardRecord awardRecord) {

        ValueOperations operations = redisTemplate.opsForValue();

        String key = "awardRecord_" + awardRecord.getArid();
        Boolean hasKey = redisTemplate.hasKey(key);
        if(hasKey){
            return false;
        }
        // 插入缓存中
        String str = new Gson().toJson(awardRecord);
        operations.set(key, str,60*15, TimeUnit.SECONDS);//向redis里存入数据,设置缓存时间为15min

        return true;
    }

    @Override
    public boolean deleteMysqlAwardRecord(String arid) {
        try {
            //返回影响行数，为1即删除成功
            int result= jdbcTemplate.update("delete from award_record where arid=?",arid);
            if(1==result){
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return false;
    }

    @Override
    public boolean deleteRedisAwardRecord(String arid) {
        String key = "awardRecord_" + arid;
        Boolean hasKey = redisTemplate.hasKey(key);
        // 缓存存在，进行删除
        if (hasKey) {
            redisTemplate.delete(key);
            return true;
        }
        return false;
    }

    @Override
    public AwardRecord getMysqlAwardRecord(String arid,int operation) {
        String sql="select * from award_record where arid= ?and operation=?";
        if(99==operation){
            sql="select * from award_record where arid= ?and operation<?";
        }
        RowMapper<AwardRecord> rowMapper = new BeanPropertyRowMapper<AwardRecord>(AwardRecord.class);
        Object object = null;
        //queryForObject会抛出非检查性异常DataAccessException，同时对返回值进行requiredSingleResult操作
        //requiredSingleResult会在查询结果为空的时候抛出EmptyResultDataAccessException异常，需要捕获后进行处理
        try {
            object = jdbcTemplate.queryForObject(sql,rowMapper,arid,operation);
        } catch (EmptyResultDataAccessException e1) {
            //查询结果为空，返回null
            return null;
        }
        return (AwardRecord) object;
    }

    @Override
    public AwardRecord getRedisAwardRecord(String arid) {
        String key = "awardRecord_" + arid;
        Boolean hasKey = redisTemplate.hasKey(key);
        ValueOperations operations = redisTemplate.opsForValue();
        // 缓存中存在
        if (hasKey) {
            String str = (String) operations.get(key);
            AwardRecord awardRecord=new Gson().fromJson(str, AwardRecord.class);
             return awardRecord;
        }
        return null;
    }

    @Override
    public boolean updateMysqlAwardRecord(AwardRecord awardRecord) {
        //返回影响行数，为1表示修改成功
        int result = jdbcTemplate.update("update award_record set operation=?,num=?,credit=? where arid=?"
                ,awardRecord.getOperation(),awardRecord.getNum(),awardRecord.getCredit(),awardRecord.getArid());
        return result > 0;
    }

    @Override
    public List<AwardRecord> getAwardRecords(int operation,String uid,int group) {
        List<AwardRecord> list=null;
        String sql=null;
        if(99==operation){
            sql="select * from award_record where uid=? and operation>=? order by time desc LIMIT ?,?";
            operation=1;
        }else{
            sql="select * from award_record where uid=? and operation=? order by time desc LIMIT ?,?";
        }
        try {
            RowMapper<AwardRecord> rowMapper = new BeanPropertyRowMapper<AwardRecord>(AwardRecord.class);
            list= jdbcTemplate.query(sql,rowMapper,uid,operation,(group-1)*6,6);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return list;
    }


}
