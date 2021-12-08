package com.subway_footprint_system.springboot_project.Pojo;

public class User {


    private String UID;//User ID
    private String Account;//账号
    private String password;//密码
    private String StationTable;//存储点亮的地铁站的表名称

    public User() {
    }

    public User(String UID,String Account, String password,String StationTable) {
        this.UID = UID;
        this.Account = Account;
        this.password = password;
        this.StationTable=StationTable;
    }
    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }
    public String getAccount() {
        return Account;
    }

    public void setAccount(String account) {
        this.Account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStationTable() {
        return StationTable;
    }

    public void setStationTable(String stationTable) {
        StationTable = stationTable;
    }

    @Override
    public String toString() {
        return "User{" +
                "UID='" + UID + '\'' +
                ", Account='" + Account + '\'' +
                ", password='" + password + '\'' +
                ", StationTable='" + StationTable + '\'' +
                '}';
    }
}
