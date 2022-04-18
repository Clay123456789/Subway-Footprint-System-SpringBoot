package com.subway_footprint_system.springboot_project.Pojo;

public class CreditRecord {
    private String crid;//碳积分流水记录id
    private String uid;//用户id
    private String operation;//操作类型（获得1/消耗0）
    private String way;//获得/消耗途径
    private String num;//获得/消耗数量
    private String balance;//剩余碳积分数目
    private String time;//操作时间

    public CreditRecord() {
    }

    public CreditRecord(String crid, String uid, String operation, String way, String num, String balance, String time) {
        this.crid = crid;
        this.uid = uid;
        this.operation = operation;
        this.way = way;
        this.num = num;
        this.balance = balance;
        this.time = time;
    }

    public String getCrid() {
        return crid;
    }

    public void setCrid(String crid) {
        this.crid = crid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getWay() {
        return way;
    }

    public void setWay(String way) {
        this.way = way;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
