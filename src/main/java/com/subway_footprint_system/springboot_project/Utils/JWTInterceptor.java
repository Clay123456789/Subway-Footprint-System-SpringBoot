package com.subway_footprint_system.springboot_project.Utils;

import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class JWTInterceptor implements HandlerInterceptor {

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    // vue axios在发送请求之前需要先发送一个OPTIONS预请求，相当于请求两次，直接放行options
    if ("OPTIONS".equals(request.getMethod().toString())) {
      return true; // true是直接放行，前端抓包会有options请求
      // false拒接访问，抓包就不会有options请求了
    }
    Map<String, String> map = new HashMap<>();
    // 获取请求头中的token令牌
    String token = request.getHeader("token");
    try {
      JWTUtil.getTokenInfo(token); // 验证令牌
      return true; // 放行请求
    } catch (NullPointerException | SignatureVerificationException e) {
      e.printStackTrace();
      map.put("msg", "无效签名!");
    } catch (TokenExpiredException e) {
      e.printStackTrace();
      map.put("msg", "token过期!");
    } catch (AlgorithmMismatchException e) {
      e.printStackTrace();
      map.put("msg", "token算法不一致!");
    } catch (Exception e) {
      e.printStackTrace();
      map.put("msg", "token无效!!");
    }
    map.put("code", "403"); // 设置状态
    // 将 map 转为json  jackson
    String json = new ObjectMapper().writeValueAsString(map);
    response.setContentType("application/json;charset=UTF-8");
    response.getWriter().println(json);
    return false;
  }
}
