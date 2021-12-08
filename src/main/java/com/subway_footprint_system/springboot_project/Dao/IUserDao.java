package com.subway_footprint_system.springboot_project.Dao;

import com.subway_footprint_system.springboot_project.Pojo.User;

import java.util.List;

public interface IUserDao {
    //增删改查方法
    boolean insert(User user);
    boolean delete(User user);
    boolean update(User user);
    User select(User user);
    List<User> selectAll(User user);
}
