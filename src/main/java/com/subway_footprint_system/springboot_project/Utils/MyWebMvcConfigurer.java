package com.subway_footprint_system.springboot_project.Utils;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MyWebMvcConfigurer implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JWTInterceptor())
                //.excludePathPatterns("/**");
                .excludePathPatterns("/Subway/**") // 地铁图相关接口不用于token验证
                .excludePathPatterns("/award/**") // 奖品相关接口不用于token验证
                .excludePathPatterns("/user/regist") // 用户登录注册相关接口不用于token验证
                .excludePathPatterns("/user/login")
                .excludePathPatterns("/user/sendRegistEmail")
                .excludePathPatterns("/user/findPassword")
                .excludePathPatterns("/merchant/regist") // 商户登录注册相关接口不用于token验证
                .excludePathPatterns("/merchant/login")
                .excludePathPatterns("/merchant/sendRegistEmail")
                .excludePathPatterns("/merchant/findPassword")
                .excludePathPatterns("/manager/login")
                .excludePathPatterns("/treasure/getTreasure")
                .excludePathPatterns("/treasure/getPositionTreasure")
                .excludePathPatterns("/treasure/getAllTreasure")
                .excludePathPatterns("/encrypt")
                .excludePathPatterns("/decrypt")
              .addPathPatterns("/**"); // 其他非登录接口都需要进行token验证
    }
}