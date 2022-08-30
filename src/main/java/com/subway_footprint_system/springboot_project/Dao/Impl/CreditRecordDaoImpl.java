package com.subway_footprint_system.springboot_project.Dao.Impl;

import com.subway_footprint_system.springboot_project.Dao.ICreditRecordDao;
import com.subway_footprint_system.springboot_project.Pojo.CreditRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CreditRecordDaoImpl implements ICreditRecordDao {

  /*
   * 对于该表数据而言，更新频率较高，故不使用redis，直接对mysql操作
   * */
  @Autowired private JdbcTemplate jdbcTemplate;

  /** 添加一行数据 */
  @Override
  public boolean insertCreditRecord(CreditRecord creditRecord) {
    try {
      // 返回影响行数，为1即增加成功
      int result =
          jdbcTemplate.update(
              "insert into credit_record (crid,uid,operation,way,num,balance,time) values(?,?,?,?,?,?,?)",
              creditRecord.getCrid(),
              creditRecord.getUid(),
              creditRecord.getOperation(),
              creditRecord.getWay(),
              creditRecord.getNum(),
              creditRecord.getBalance(),
              creditRecord.getTime());
      if (1 == result) {
        return true;
      }
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return false;
  }
  /** 删除一行数据 */
  @Override
  public boolean deleteCreditRecord(String crid) {
    try {
      // 返回影响行数，为1即删除成功
      int result = jdbcTemplate.update("delete from credit_record where crid=?", crid);
      if (1 == result) {
        return true;
      }
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return false;
  }

  /** 获取一行数据 */
  @Override
  public CreditRecord getCreditRecord(String crid) {
    RowMapper<CreditRecord> rowMapper = new BeanPropertyRowMapper<CreditRecord>(CreditRecord.class);
    Object object = null;
    // queryForObject会抛出非检查性异常DataAccessException，同时对返回值进行requiredSingleResult操作
    // requiredSingleResult会在查询结果为空的时候抛出EmptyResultDataAccessException异常，需要捕获后进行处理
    try {
      object =
          jdbcTemplate.queryForObject("select * from credit_record where crid=?", rowMapper, crid);
    } catch (EmptyResultDataAccessException e1) {
      // 查询结果为空，返回null
      return null;
    }
    return (CreditRecord) object;
  }
  /*
   * 获取指定uid的所有记录
   * */
  @Override
  public List<CreditRecord> getUserCreditRecords(String uid, int group) {
    List<CreditRecord> list = null;
    try {
      RowMapper<CreditRecord> rowMapper =
          new BeanPropertyRowMapper<CreditRecord>(CreditRecord.class);
      list =
          jdbcTemplate.query(
              "select * from credit_record where uid=? order by time desc LIMIT ?,?",
              rowMapper,
              uid,
              (group - 1) * 6,
              6);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
    return list;
  }
}
