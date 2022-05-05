package com.subway_footprint_system.springboot_project.Pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data//该注解会自动生成set/get方法，toString方法，equals方法，hashCode方法
@Builder //产生复杂的构建器api类,注意子类和父类会冲突
@NoArgsConstructor//不带参数的构造方法
@AllArgsConstructor//全构造方法
public class CreditRecord {

    private String crid;//碳积分流水记录id
    private String uid;//用户id
    private int operation;//操作类型（获得1/消耗0）
    private String way;//获得/消耗途径
    private int num;//获得/消耗数量
    private int balance;//剩余碳积分数目
    private String time;//操作时间

}
