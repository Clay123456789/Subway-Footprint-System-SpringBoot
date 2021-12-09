package com.subway_footprint_system.springboot_project.Pojo;

public class User {


    private String UID;//User ID
    private String Account;//账号
    private String Password;//密码
    private String StationTable;//存储点亮的地铁站的表名称

    public User() {
    }

    public User(String UID,String Account, String Password,String StationTable) {
        this.UID = UID;
        this.Account = Account;
        this.Password = Password;
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
        return Password;
    }

    public void setPassword(String Password) {
        this.Password = Password;
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
                ", password='" + Password + '\'' +
                ", StationTable='" + StationTable + '\'' +
                '}';
    }
}
