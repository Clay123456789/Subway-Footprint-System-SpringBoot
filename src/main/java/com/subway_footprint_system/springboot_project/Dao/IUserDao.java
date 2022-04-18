package com.subway_footprint_system.springboot_project.Dao;

import com.subway_footprint_system.springboot_project.Pojo.User;

import java.util.List;

public interface IUserDao {
    //增删改查方法
    boolean insertUser(User user);
    boolean deleteUser(String uid);
    //更改用户信息（不含uid,password,email)
    boolean updateUser(User user);
    //更改password
    boolean changePassword(String uid,String password);
    User getUserByUid(String uid);
    User getUserByEmail(String email);
    User getUserByUsername(String username);
    List<User> getAllUsers();
}
