package com.subway_footprint_system.springboot_project.Pojo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data//该注解会自动生成set/get方法，toString方法，equals方法，hashCode方法
@Builder //产生复杂的构建器api类,注意子类和父类会冲突
@NoArgsConstructor//不带参数的构造方法
@AllArgsConstructor//全构造方法
public class Subway {
    private String sid;//code_lid拼接,作为主键
    private int code;//城市代码
    private String cn_name;//城市中文名称
    private String cename;//城市英文名称
    private String cpre;//城市英文名称
    private Map<String, Object> l_xmlattr;//地铁线路信息
    private List<Map<String, Object>> p;//地铁线路所有站点数组信息

}
