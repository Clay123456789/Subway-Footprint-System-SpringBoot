package com.subway_footprint_system.springboot_project.Pojo;

import lombok.*;

@Data//该注解会自动生成set/get方法，toString方法，equals方法，hashCode方法
@Builder //产生复杂的构建器api类,注意子类和父类会冲突
@NoArgsConstructor//不带参数的构造方法
@AllArgsConstructor//全构造方法
public class Merchant {
    private String mid;//Merchant ID
    private String account;
    private String name;
    private String password;
    private String email;
    private String tel;
    private String info;
}