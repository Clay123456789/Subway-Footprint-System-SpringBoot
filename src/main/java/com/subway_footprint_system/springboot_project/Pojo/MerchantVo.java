package com.subway_footprint_system.springboot_project.Pojo;

import lombok.*;

@Data // 该注解会自动生成set/get方法，toString方法，equals方法，hashCode方法
@NoArgsConstructor // 不带参数的构造方法
@AllArgsConstructor // 全构造方法
@ToString(callSuper = true) // callSuper = true表示带上父类参数
public class MerchantVo extends Merchant {
  //    验证码
  private String code;
  // 更改以下信息前需要和原信息验证，故单独列出
  private String newEmail;
  private String newPassword;

  // 重新构造一个builder，以免和父类冲突
  @Builder(builderMethodName = "childBuilder")
  public MerchantVo(
      String mid,
      String account,
      String name,
      String password,
      String email,
      String tel,
      String location,
      String authentication,
      int authenticated,
      String time,
      String info,
      String code,
      String newEmail,
      String newPassword) {
    super(
        mid,
        account,
        name,
        password,
        email,
        tel,
        location,
        authentication,
        authenticated,
        time,
        info);
    this.code = code;
    this.newEmail = newEmail;
    this.newPassword = newPassword;
  }

  public MerchantVo(Merchant merchant) {
    this.setMid(merchant.getMid());
    this.setAccount(merchant.getAccount());
    this.setName(merchant.getName());
    this.setPassword(merchant.getPassword());
    this.setEmail(merchant.getEmail());
    this.setTel(merchant.getTel());
    this.setLocation(merchant.getLocation());
    this.setAuthentication(merchant.getAuthentication());
    this.setAuthenticated(merchant.getAuthenticated());
    this.setTime(merchant.getTime());
    this.setInfo(merchant.getInfo());
  }
}
