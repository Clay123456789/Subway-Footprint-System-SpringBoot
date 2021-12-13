package com.subway_footprint_system.springboot_project.Pojo;

public class UserVo {
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
    private String stationtable;//存储点亮的地铁站的表名称

    //    验证码
    private String code;

    //更改后的新信息
    private String newUsername;
    private String newEmail;
    private String newTel;
    private String newPassword;
    private String newTouxiang;
    private String newQianming;
    private int newAge;
    private String newSex;

    public UserVo() {

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getTouxiang() {
        return touxiang;
    }

    public void setTouxiang(String touxiang) {
        this.touxiang = touxiang;
    }

    public String getQianming() {
        return qianming;
    }

    public void setQianming(String qianming) {
        this.qianming = qianming;
    }

    public String getStationtable() {
        return stationtable;
    }

    public void setStationtable(String stationtable) {
        this.stationtable = stationtable;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNewUsername() {
        return newUsername;
    }

    public void setNewUsername(String newUsername) {
        this.newUsername = newUsername;
    }

    public String getNewEmail() {
        return newEmail;
    }

    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }

    public String getNewTel() {
        return newTel;
    }

    public void setNewTel(String newTel) {
        this.newTel = newTel;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getNewTouxiang() {
        return newTouxiang;
    }

    public void setNewTouxiang(String newTouxiang) {
        this.newTouxiang = newTouxiang;
    }

    public String getNewQianming() {
        return newQianming;
    }

    public void setNewQianming(String newQianming) {
        this.newQianming = newQianming;
    }

    public int getNewAge() {
        return newAge;
    }

    public void setNewAge(int newAge) {
        this.newAge = newAge;
    }

    public String getNewSex() {
        return newSex;
    }

    public void setNewSex(String newSex) {
        this.newSex = newSex;
    }


}
