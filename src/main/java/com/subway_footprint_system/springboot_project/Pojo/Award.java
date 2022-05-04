package com.subway_footprint_system.springboot_project.Pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data//该注解会自动生成set/get方法，toString方法，equals方法，hashCode方法
@Builder //产生复杂的构建器api类,注意子类和父类会冲突
@NoArgsConstructor//不带参数的构造方法
@AllArgsConstructor//全构造方法
public class Award {
    private String aid;//奖品所属批次id
    private String variety;//奖品种类
    private int num;//该批次奖品剩余数量
    private String name;//奖品名称
    private String content;//奖品内容
    private int credit;//兑换所需碳积分
    private String fromdate;//奖品上传时间
    private String todate;//奖品有效期
    private String mid;//(若为商户上传)商户id
    private int status;//奖品当前状态（0正常/1下架/2售空等）
}
