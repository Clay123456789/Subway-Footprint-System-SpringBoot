package com.subway_footprint_system.springboot_project.Pojo;
import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Subway {
    private String sid;//code_lid拼接,作为主键
    private int code;//城市代码
    private String cn_name;//城市中文名称
    private String cename;//城市英文名称
    private String cpre;//城市英文名称
    private Map<String, Object> l_xmlattr;//地铁线路信息
    private Map<String, Object> p;//地铁线路所有站点数组信息

    public Subway() {
    }

    public Subway(String sid, int code, String cn_name, String cename, String cpre, Map<String, Object> l_xmlattr, Map<String, Object> p) {
        this.sid = sid;
        this.code = code;
        this.cn_name = cn_name;
        this.cename = cename;
        this.cpre = cpre;
        this.l_xmlattr = l_xmlattr;
        this.p = p;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getCn_name() {
        return cn_name;
    }

    public void setCn_name(String cn_name) {
        this.cn_name = cn_name;
    }

    public String getCename() {
        return cename;
    }

    public void setCename(String cename) {
        this.cename = cename;
    }

    public String getCpre() {
        return cpre;
    }

    public void setCpre(String cpre) {
        this.cpre = cpre;
    }

    public Map<String, Object> getL_xmlattr() {
        return l_xmlattr;
    }

    public void setL_xmlattr(Map<String, Object> l_xmlattr) {
        this.l_xmlattr = l_xmlattr;
    }

    public Map<String, Object> getP() {
        return p;
    }

    public void setP(Map<String, Object> p) {
        this.p = p;
    }



}
