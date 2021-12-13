package com.subway_footprint_system.springboot_project.Dao;

import com.subway_footprint_system.springboot_project.Pojo.User;

import java.util.List;

public interface IUserDao {
    //增删改查方法
    boolean insertUser(User user);
    boolean deleteUser(String uid);
    boolean updateUser(User user);
    User getUserByUid(String uid);
    User getUserByEmail(String email);
    User getUserByUsername(String username);
    List<User> getAllUsers();
}
