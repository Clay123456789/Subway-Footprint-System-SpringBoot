package com.subway_footprint_system.springboot_project.Pojo;

import lombok.*;

@Data//该注解会自动生成set/get方法，toString方法，equals方法，hashCode方法
@NoArgsConstructor//不带参数的构造方法
@AllArgsConstructor//全构造方法
@ToString(callSuper = true)//callSuper = true表示带上父类参数
public class UserVo extends User {

    //    验证码
    private String code;
    //更改以下信息前需要和原信息验证，故单独列出
    private String newEmail;
    private String newPassword;

    //重新构造一个builder，以免和父类冲突
    @Builder(builderMethodName = "childBuilder")
    public UserVo(String uid, String username, String password, String email, int age, String sex, String tel, String touxiang, String qianming, int credit, String code, String newEmail, String newPassword) {
        super(uid, username, password, email, age, sex, tel, touxiang, qianming, credit);
        this.code = code;
        this.newEmail = newEmail;
        this.newPassword = newPassword;
    }

    public UserVo(User user) {
        this.setUid(user.getUid());
        this.setUsername(user.getUsername());
        this.setEmail(user.getEmail());
        this.setPassword(user.getPassword());
        this.setAge(user.getAge());
        this.setCredit(user.getCredit());
        this.setSex(user.getSex());
        this.setTel(user.getTel());
        this.setTouxiang(user.getTouxiang());
        this.setQianming(user.getQianming());
    }
}
