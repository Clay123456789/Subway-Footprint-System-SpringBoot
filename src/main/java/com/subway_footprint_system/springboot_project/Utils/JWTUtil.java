package com.subway_footprint_system.springboot_project.Utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

@Configuration
public class JWTUtil {

    // 用于JWT进行签名加密的秘钥
    private static String SECRET;

    @Value("${jwt.secret}")//从配置文件里读取
    public void setSECRET(String SECRET) {
        this.SECRET = SECRET;
    }


    /**
     * @Param: 传入需要设置的payload信息
     * @return: 返回token
     */
    public static String generateToken(Map<String, String> map) {
        JWTCreator.Builder builder = JWT.create();

        // 将map内的信息传入JWT的payload中
        map.forEach((k, v) -> {
            builder.withClaim(k, v);
        });

        // 设置JWT令牌的过期时间为7天
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.DATE, 7);
        builder.withExpiresAt(instance.getTime());

        // 设置签名并返回token
        return builder.sign(Algorithm.HMAC256(SECRET)).toString();
    }

    /**
     * @Param: 传入token
     * @return: 解密的token信息
     */
    public static DecodedJWT getTokenInfo(String token) {
        return JWT.require(Algorithm.HMAC256(SECRET)).build().verify(token);
    }

    public static String getNowTime() {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");
        return formatter.format(calendar.getTime());
    }

}
