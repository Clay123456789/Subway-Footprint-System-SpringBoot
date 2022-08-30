package com.subway_footprint_system.springboot_project.Pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // 使用后该类无法被继承，该注解会自动生成set/get方法，toString方法，equals方法，hashCode方法
@Builder // 产生复杂的构建器api类
@NoArgsConstructor // 不带参数的构造方法
@AllArgsConstructor // 全构造方法
public class Result {
  /** 响应状态码 */
  private int code;
  /** 响应提示信息 */
  private String message;
  /** 响应结果对象 */
  private Object data;
}
