package com.subway_footprint_system.springboot_project.Pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String location;//商户位置（格式：经度，纬度）
    private String authentication;//商户证书url
    private int authenticated;//-1未认证/0认证中/1已认证/2认证过期
    private String time;//认证时间（有效期一年）
    private String info;
}
