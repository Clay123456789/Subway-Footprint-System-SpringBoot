package com.subway_footprint_system.springboot_project.Pojo;

public class Station {
    private String sid;//Station ID
    private String sname;//地铁站名称（中文名）(32个汉字)
    private float longitude;//经度(默认东经)
    private float latitude;//纬度（默认北纬）
    private String route;//从属路线名字（中文名）

    public Station() {
    }

    public Station(String sid, String sname, float longitude, float latitude, String route) {
        this.sid = sid;
        this.sname = sname;
        this.longitude = longitude;
        this.latitude = latitude;
        this.route = route;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    @Override
    public String toString() {
        return "Station{" +
                "sid='" + sid + '\'' +
                ", sname='" + sname + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", route='" + route + '\'' +
                '}';
    }
}
