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
                .excludePathPatterns("/user/regist") // 用户登录接口不用于token验
                .excludePathPatterns("/user/login")
                .excludePathPatterns("/user/sendRegistEmail")
                .excludePathPatterns("/user/findPassword")
                .addPathPatterns("/**"); // 其他非登录接口都需要进行token验证
    }
}