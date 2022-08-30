package com.subway_footprint_system.springboot_project.Dao.Impl;

import com.subway_footprint_system.springboot_project.Dao.IManagerDao;
import com.subway_footprint_system.springboot_project.Pojo.Manager;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class ManagerDaoImpl implements IManagerDao {
  @Autowired private JdbcTemplate jdbcTemplate;
  @Autowired private StringEncryptor encryptor;

  @Override
  public Manager getManagerByManagerId(String managerID) {
    RowMapper<Manager> rowMapper = new BeanPropertyRowMapper<Manager>(Manager.class);
    Object object = null;
    // queryForObject会抛出非检查性异常DataAccessException，同时对返回值进行requiredSingleResult操作
    // requiredSingleResult会在查询结果为空的时候抛出EmptyResultDataAccessException异常，需要捕获后进行处理
    try {
      object =
          jdbcTemplate.queryForObject(
              "select * from manager where managerID = ?", rowMapper, managerID);
    } catch (EmptyResultDataAccessException e1) {
      // 查询结果为空，返回null
      return null;
    }
    Manager manager = (Manager) object;
    manager.setPassword(encryptor.decrypt(manager.getPassword()));
    return manager;
  }

  @Override
  public Manager getManagerByAccount(String account) {
    RowMapper<Manager> rowMapper = new BeanPropertyRowMapper<Manager>(Manager.class);
    Object object = null;
    // queryForObject会抛出非检查性异常DataAccessException，同时对返回值进行requiredSingleResult操作
    // requiredSingleResult会在查询结果为空的时候抛出EmptyResultDataAccessException异常，需要捕获后进行处理
    try {
      object =
          jdbcTemplate.queryForObject(
              "select * from manager where account = ?", rowMapper, account);
    } catch (EmptyResultDataAccessException e1) {
      // 查询结果为空，返回null
      return null;
    }
    Manager manager = (Manager) object;
    manager.setPassword(encryptor.decrypt(manager.getPassword()));
    return manager;
  }
}
