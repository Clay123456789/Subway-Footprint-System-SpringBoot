package com.subway_footprint_system.springboot_project.Pojo;

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
    private String credit;///碳积分数目

    public User() {
    }

    public User(String email) {
        this.email = email;
    }

    public User(String uid, String username, String password, String email, int age, String sex, String tel, String touxiang, String qianming, String credit) {
        this.uid = uid;
        this.username = username;
        this.password = password;
        this.email = email;
        this.age = age;
        this.sex = sex;
        this.tel = tel;
        this.touxiang = touxiang;
        this.qianming = qianming;
        this.credit = credit;
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

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }
}