package com.subway_footprint_system.springboot_project.Pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // 该注解会自动生成set/get方法，toString方法，equals方法，hashCode方法
@Builder // 产生复杂的构建器api类,注意子类和父类会冲突
@NoArgsConstructor // 不带参数的构造方法
@AllArgsConstructor // 全构造方法
public class LightedStation {
  private String uid; // 用户uid
  private String pid; // 点亮的地铁站的uid
  private String time; // 点亮的时间
  private int credit; // 点亮获得的碳积分
}
