package com.subway_footprint_system.springboot_project.Service.Impl;
import com.subway_footprint_system.springboot_project.Dao.Impl.UserDaoImpl;
import com.subway_footprint_system.springboot_project.Pojo.User;
import com.subway_footprint_system.springboot_project.Pojo.UserVo;
import com.subway_footprint_system.springboot_project.Pojo.UserVoToUser;
import com.subway_footprint_system.springboot_project.Service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserDaoImpl userDao;
    @Autowired
    private UserVoToUser userVoToUser;


    @Override
    public boolean insertUser(UserVo userVo) {
        return userDao.insertUser(userVoToUser.toUser(userVo));
    }

    @Override
    public boolean deleteUser(String uid) {
        return userDao.deleteUser(uid);
    }

    @Override
    public boolean updatePassword(UserVo userVo) {
        //先判断用户是否合法
        if(judgeByUid(userVo)){
            //获取用户原信息
            User user=getUserByUid(userVo.getUid());
            //修改密码
            user.setPassword(userVo.getNewPassword());
            //更新
            return userDao.updateUser(user);
            //发送更改密码提示

        }
        return false;
    }

    @Override
    public boolean updateEmail(UserVo userVo) {
        if(judgeByUid(userVo)){
            User user=getUserByUid(userVo.getUid());
            user.setEmail(userVo.getNewEmail());
            return userDao.updateUser(user);
        }
        return false;
    }

    @Override
    public boolean updateUserName(UserVo userVo) {
        if(judgeByUid(userVo)){
            User user=getUserByUid(userVo.getUid());
            user.setUsername(userVo.getNewUsername());
            return userDao.updateUser(user);
        }
        return false;
    }

    @Override
    public boolean updateUser(UserVo userVo) {
        return  userDao.updateUser(UserVoToUser.toNewUser(userVo));
    }

    @Override
    public User getUserByUid(String uid) {
        return userDao.getUserByUid(uid);
    }

    @Override
    public User getUserByEmail(String email) {
        return userDao.getUserByUid(email);
    }

    @Override
    public User getUserByUsername(String email) {
        return userDao.getUserByUid(email);
    }

    @Override
    public String getUserTouxiang(String uid) {
        User user=getUserByUid(uid);
        if(user!=null){
            return user.getTouxiang();
        }
        return null;
    }

    @Override
    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }

    @Override
    public boolean judgeByUid(UserVo userVo) {
        //根据id查询该用户信息
        User user=userDao.getUserByUid(userVo.getUid());
        //用户存在且密码相同，返回真
        if(user!=null&&userVo.getPassword()!=null){
            return user.getPassword().equals(userVo.getPassword());
        }
        return false;
    }

    @Override
    public boolean judgeByUsername(UserVo userVo) {
        //根据username查询该用户信息
        User user=userDao.getUserByUsername(userVo.getUsername());
        //用户存在且密码相同，返回真
        if(user!=null&&userVo.getPassword()!=null){
            return user.getPassword().equals(userVo.getPassword());
        }
        return false;}

    @Override
    public boolean judgeByEmail(UserVo userVo) {
        //根据Email查询该用户信息
        User user=userDao.getUserByEmail(userVo.getEmail());
        //用户存在且密码相同，返回真
        if(user!=null&&userVo.getPassword()!=null){
            return user.getPassword().equals(userVo.getPassword());
        }
        return false;
    }


}
