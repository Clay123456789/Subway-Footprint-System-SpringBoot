package com.subway_footprint_system.springboot_project.Pojo;

public class UserVo extends User{

    //    验证码
    private String code;

    //更改以下信息前需要和原信息验证，故单独列出
    private String newEmail;
    private String newPassword;

    public UserVo() {

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
