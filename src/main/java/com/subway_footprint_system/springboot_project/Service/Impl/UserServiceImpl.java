package com.subway_footprint_system.springboot_project.Service.Impl;

import com.subway_footprint_system.springboot_project.Dao.Impl.UserDaoImpl;
import com.subway_footprint_system.springboot_project.Pojo.User;
import com.subway_footprint_system.springboot_project.Pojo.UserVo;
import com.subway_footprint_system.springboot_project.Service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserDaoImpl userDao;


    @Override
    public boolean insertUser(UserVo userVo) {
        return userDao.insertUser(userVo);
    }

    @Override
    public boolean deleteUser(String uid) {
        return userDao.deleteUser(uid);
    }

    @Override
    public boolean updatePassword(UserVo userVo) {
        //先判断用户是否合法
        if (judgeByEmail(userVo)) {
            //获取用户原信息
            User user = getUserByEmail(userVo.getEmail());
            //修改密码
            return userDao.changePassword(user.getUid(), userVo.getNewPassword());
        }
        return false;
    }

    @Override
    public boolean updateEmail(UserVo userVo) {
        /*
         * 由于更改邮箱流程较为复杂，该方法后续再实现。
         *
         *
         *
         * */
        return false;
    }


    @Override
    public boolean updateUser(UserVo userVo) {
        if (null != getUserByUid(userVo.getUid()))
            return userDao.updateUser(userVo);
        return false;
    }

    @Override
    public User getUserByUid(String uid) {
        return userDao.getUserByUid(uid);
    }

    @Override
    public User getUserByEmail(String email) {
        return userDao.getUserByEmail(email);
    }

    @Override
    public User getUserByUsername(String username) {
        return userDao.getUserByUsername(username);
    }


    @Override
    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }

    @Override
    public boolean judgeByUid(UserVo userVo) {
        //根据id查询该用户信息
        User user = userDao.getUserByUid(userVo.getUid());
        //用户存在且密码相同，返回真
        if (user != null && userVo.getPassword() != null) {
            return user.getPassword().equals(userVo.getPassword());
        }
        return false;
    }

    @Override
    public boolean judgeByUsername(UserVo userVo) {
        //根据username查询该用户信息
        User user = userDao.getUserByUsername(userVo.getUsername());
        //用户存在且密码相同，返回真
        if (user != null && userVo.getPassword() != null) {
            return user.getPassword().equals(userVo.getPassword());
        }
        return false;
    }

    @Override
    public boolean judgeByEmail(UserVo userVo) {
        //根据Email查询该用户信息
        User user = userDao.getUserByEmail(userVo.getEmail());
        //用户存在且密码相同，返回真
        if (user != null && userVo.getPassword() != null) {
            return user.getPassword().equals(userVo.getPassword());
        }
        return false;
    }

    @Override
    public List<Map<String, Object>> getRankingList() {

        List<User> users = userDao.getRankingList();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < users.size(); i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("rank", i + 1);
            map.put("touxiang", users.get(i).getTouxiang());
            map.put("username", users.get(i).getUsername());
            map.put("credit", users.get(i).getCredit());
            list.add(map);
        }
        return list;
    }


    public int getPersonalCreditRank(String uid) {
        return userDao.getPersonalCreditRank(uid);
    }
}
