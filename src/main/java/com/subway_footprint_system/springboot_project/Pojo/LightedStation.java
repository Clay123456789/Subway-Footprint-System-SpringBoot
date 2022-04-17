package com.subway_footprint_system.springboot_project.Pojo;

public class LightedStation {
    private String uid;//用户uid
    private String pid;//点亮的地铁站的uid
    private String time;//点亮的时间
    private String credit;//点亮获得的碳积分

    public LightedStation() {
    }

    public LightedStation(String uid, String pid, String time, String credit) {
        this.uid = uid;
        this.pid = pid;
        this.time = time;
        this.credit = credit;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }
}
