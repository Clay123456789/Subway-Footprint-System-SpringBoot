package com.subway_footprint_system.springboot_project.Pojo;

public class Station {
    private String SID;//Station ID
    private String SName;//地铁站名称（中文名）(32个汉字)
    private float Longitude;//经度(默认东经)
    private float Latitude;//纬度（默认北纬）
    private String Route;//从属路线名字（中文名）

    public Station() {
    }
    public Station(String SID, String SName, float Longitude, float Latitude, String route) {
        this.SID = SID;
        this.SName = SName;
        this.Longitude = Longitude;
        this.Latitude = Latitude;
        Route = route;
    }

    public String getSID() {
        return SID;
    }

    public void setSID(String SID) {
        this.SID = SID;
    }

    public String getSName() {
        return SName;
    }

    public void setSName(String SName) {
        this.SName = SName;
    }

    public float getLongitude() {
        return Longitude;
    }

    public void setLongitude(float Longitude) {
        this.Longitude = Longitude;
    }

    public float getLatitude() {
        return Latitude;
    }

    public void setLatitude(float Latitude) {
        this.Latitude = Latitude;
    }

    public String getRoute() {
        return Route;
    }

    public void setRoute(String route) {
        Route = route;
    }

    @Override
    public String toString() {
        return "Station{" +
                "SID='" + SID + '\'' +
                ", SName='" + SName + '\'' +
                ", Longitude='" + Longitude + '\'' +
                ", Latitude='" + Latitude + '\'' +
                ", Route='" + Route + '\'' +
                '}';
    }
}
