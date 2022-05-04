package com.subway_footprint_system.springboot_project.Dao.Impl;

import com.subway_footprint_system.springboot_project.Dao.IAwardRecordDao;
import com.subway_footprint_system.springboot_project.Pojo.AwardRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public class AwardRecordDaoImpl implements IAwardRecordDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Override
    public boolean insertAwardRecord(AwardRecord awardRecord) {
        try {
            //返回影响行数，为1即增加成功
            int result = jdbcTemplate.update("insert into await_record (arid, operation, uid, mid, aid, num, time, credit) values(?,?,?,?,?,?,?,?)",
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
    public boolean deleteAwardRecord(String arid) {
        try {
            //返回影响行数，为1即删除成功
            int result= jdbcTemplate.update("delete from await_record where arid=?",arid);
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
    public AwardRecord getAwardRecord(String arid) {
        RowMapper<AwardRecord> rowMapper = new BeanPropertyRowMapper<AwardRecord>(AwardRecord.class);
        Object object = null;
        //queryForObject会抛出非检查性异常DataAccessException，同时对返回值进行requiredSingleResult操作
        //requiredSingleResult会在查询结果为空的时候抛出EmptyResultDataAccessException异常，需要捕获后进行处理
        try {
            object = jdbcTemplate.queryForObject("select * from await_record where arid= ?",rowMapper,arid);
        } catch (EmptyResultDataAccessException e1) {
            //查询结果为空，返回null
            return null;
        }
        return (AwardRecord) object;
    }

    @Override
    public List<AwardRecord> getExchangeAwardRecords(String uid) {
        List<AwardRecord> list=null;
        try {
            RowMapper<AwardRecord> rowMapper = new BeanPropertyRowMapper<AwardRecord>(AwardRecord.class);
            list= jdbcTemplate.query("select * from await_record where uid=? and operation=1 order by time desc ",rowMapper,uid);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return list;
    }

    @Override
    public List<AwardRecord> getBuryAwardRecords(String uid) {
        List<AwardRecord> list=null;
        try {
            RowMapper<AwardRecord> rowMapper = new BeanPropertyRowMapper<AwardRecord>(AwardRecord.class);
            list= jdbcTemplate.query("select * from await_record where uid=? and operation=0 order by time desc ",rowMapper,uid);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return list;
    }
}
