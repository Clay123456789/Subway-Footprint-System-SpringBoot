package com.subway_footprint_system.springboot_project.Pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data//该注解会自动生成set/get方法，toString方法，equals方法，hashCode方法
@Builder //产生复杂的构建器api类,注意子类和父类会冲突
@NoArgsConstructor//不带参数的构造方法
@AllArgsConstructor//全构造方法
public class AwardRecord {

    private String arid;//奖品流水记录id
    private int operation;//操作类型(操作类型(购物车中-1/藏宝0/已兑换1/订单中2/已取消订单3/订单超时4）
    private String uid;//(若为兑换/藏宝为普通用户）用户id
    private String mid;//(用户兑换/商户藏宝）商户id
    private String aid;//兑换/藏宝商品所属批次id
    private int num;//兑换/藏宝数目
    private String time;//兑换/藏宝时间
    private int credit;//（若为兑换）消耗碳积分
}
