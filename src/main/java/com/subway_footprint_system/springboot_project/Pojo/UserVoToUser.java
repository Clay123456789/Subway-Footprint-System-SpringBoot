package com.subway_footprint_system.springboot_project.Pojo;

import org.springframework.stereotype.Repository;

@Repository
public class UserVoToUser {
    /**
     * 将表单中的对象转化为数据库中存储的用户对象（剔除表单中的code）
     */
    public static User toUser(UserVo userVo) {

        //分离出UserVo中的当前用户信息
        User user = new User();
        //传值
        user.setUid(userVo.getUid());
        user.setUsername(userVo.getUsername());
        user.setPassword(userVo.getPassword());
        user.setEmail(userVo.getEmail());
        user.setTel(userVo.getTel());
        user.setAge(userVo.getAge());
        user.setQianming(userVo.getQianming());
        user.setTouxiang(userVo.getTouxiang());
        user.setSex(userVo.getSex());
        // 返回包装后的对象
        return user;
    }
    public static User toNewUser(UserVo userVo){

        //分离出UserVo中的预修改用户信息
        User newUser = new User();
        //传值
        newUser.setUid(userVo.getUid());
        newUser.setUsername(userVo.getNewUsername());
        newUser.setPassword(userVo.getNewPassword());
        newUser.setEmail(userVo.getNewEmail());
        newUser.setTel(userVo.getNewTel());
        newUser.setAge(userVo.getNewAge());
        newUser.setQianming(userVo.getNewQianming());
        newUser.setTouxiang(userVo.getNewTouxiang());
        newUser.setSex(userVo.getNewSex());
        // 返回包装后的对象
        return newUser;
    }

}
