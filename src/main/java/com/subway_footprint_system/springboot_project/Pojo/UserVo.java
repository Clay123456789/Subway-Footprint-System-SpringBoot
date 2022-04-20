package com.subway_footprint_system.springboot_project.Pojo;

public class UserVo extends User{

    //    验证码
    private String code;

    //更改以下信息前需要和原信息验证，故单独列出
    private String newEmail;
    private String newPassword;

    public UserVo() {

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
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNewEmail() {
        return newEmail;
    }

    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
