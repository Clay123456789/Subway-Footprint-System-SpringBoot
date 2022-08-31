package com.subway_footprint_system.springboot_project.Pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data//该注解会自动生成set/get方法，toString方法，equals方法，hashCode方法
@Builder //产生复杂的构建器api类,注意子类和父类会冲突
@NoArgsConstructor//不带参数的构造方法
@AllArgsConstructor//全构造方法
public class User {
    private String uid;//User ID
    private String username;
    private String password;
    private String email;
    private int age;
    private String sex;
    private String tel;
    //用户头像，以url存储
    private String touxiang;
    private String qianming;
    private int credit;///碳积分数目
}