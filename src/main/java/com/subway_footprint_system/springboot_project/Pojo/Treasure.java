package com.subway_footprint_system.springboot_project.Pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data//该注解会自动生成set/get方法，toString方法，equals方法，hashCode方法
@Builder //产生复杂的构建器api类,注意子类和父类会冲突
@NoArgsConstructor//不带参数的构造方法
@AllArgsConstructor//全构造方法
public class Treasure {
    private String tid; //宝藏id
    private String variety; //宝藏种类
    private String content; //宝藏内容
    private int credit; //打开所需碳积分
    private String pid; //宝藏所藏的地铁站id
    private String fromdate; //藏宝时间
    private String todate; //有效期
    private int status; //宝箱当前状态
    private String uid; //藏宝用户id
    private String mid; //商户id
    private String uid2; //挖宝用户id
    private String getdate; //挖宝时间
    private String message; //留言
}
