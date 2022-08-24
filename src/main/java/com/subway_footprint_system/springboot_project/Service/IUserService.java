package com.subway_footprint_system.springboot_project.Service;

import com.subway_footprint_system.springboot_project.Dao.IUserDao;
import com.subway_footprint_system.springboot_project.Pojo.User;
import com.subway_footprint_system.springboot_project.Pojo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

public interface IUserService {

    //增
    boolean insertUser(UserVo userVo);
    //删
    boolean deleteUser(String uid);

    //改
    boolean updateUser(UserVo userVo);
    //更改密码
    boolean updatePassword(UserVo userVo);
    //更改邮箱
    boolean updateEmail(UserVo userVo);
    //查
    User getUserByUid(String uid);
    User getUserByEmail(String email);
    User getUserByUsername(String username);
    List<User> getAllUsers();
    //根据Uid/Username/EMail及密码判断用户是否存在
    boolean judgeByUid(UserVo userVo);
    boolean judgeByUsername(UserVo userVo);
    boolean judgeByEmail(UserVo userVo);
    //获取碳积分排行榜
    List<Map<String, Object>> getRankingList();
}
