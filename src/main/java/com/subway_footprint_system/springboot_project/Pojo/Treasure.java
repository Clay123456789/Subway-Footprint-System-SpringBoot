package com.subway_footprint_system.springboot_project.Pojo;

public class Treasure {
    private String tid; //宝藏id
    private String variety; //宝藏种类
    private String content; //宝藏内容
    private String credit; //打开所需碳积分
    private String pid; //宝藏所藏的地铁站id
    private String fromdate; //藏宝时间
    private String todate; //有效期
    private int status; //宝箱当前状态
    private String uid; //藏宝用户id
    private String mid; //商户id
    private String uid2; //挖宝用户id
    private String getdate; //挖宝时间

    public Treasure(){

    }

    public Treasure(String tid, String variety, String content, String credit, String pid, String fromdate, String todate, int status, String uid, String mid, String uid2, String getdate) {
        this.tid = tid;
        this.variety = variety;
        this.content = content;
        this.credit = credit;
        this.pid = pid;
        this.fromdate = fromdate;
        this.todate = todate;
        this.status = status;
        this.uid = uid;
        this.mid = mid;
        this.uid2 = uid2;
        this.getdate = getdate;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getVariety() {
        return variety;
    }

    public void setVariety(String variety) {
        this.variety = variety;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getFromdate() {
        return fromdate;
    }

    public void setFromdate(String fromdate) {
        this.fromdate = fromdate;
    }

    public String getTodate() {
        return todate;
    }

    public void setTodate(String todate) {
        this.todate = todate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getUid2() {
        return uid2;
    }

    public void setUid2(String uid2) {
        this.uid2 = uid2;
    }

    public String getGetdate() {
        return getdate;
    }

    public void setGetdate(String getdate) {
        this.getdate = getdate;
    }
}
