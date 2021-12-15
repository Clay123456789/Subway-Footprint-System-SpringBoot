package com.subway_footprint_system.springboot_project.Pojo;

public class LightedStation {
    private String uid;//用户id
    private String pid;//点亮的地铁站的uid
    private String point;//积分
    private String time;//点亮的时间

    public LightedStation() {
    }

    public LightedStation(String uid, String pid, String point, String time) {
        this.uid = uid;
        this.pid = pid;
        this.point = point;
        this.time = time;
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

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    @Override
    public String toString() {
        return "LightedStation{" +
                "uid='" + uid + '\'' +
                ", pid='" + pid + '\'' +
                ", point='" + point + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
