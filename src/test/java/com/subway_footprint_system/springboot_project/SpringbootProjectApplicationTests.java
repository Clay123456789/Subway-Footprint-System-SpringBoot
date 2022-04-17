package com.subway_footprint_system.springboot_project;

import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringbootProjectApplicationTests {

    @Test
    void contextLoads() {
    }
    /**
     * 注入加密方法
     */
    @Autowired
    private StringEncryptor encryptor;

    /**
     * 手动生成密文，此处演示了url，user，password
     */
    @Test
    public void encrypt() {
        String url = encryptor.encrypt("");
        String name = encryptor.encrypt("");
        String password = encryptor.encrypt("");
        String a = encryptor.encrypt("");
        String b = encryptor.encrypt("");
        String c = encryptor.encrypt("");

        System.out.println("database url: " + url);
        System.out.println("database name: " + name);
        System.out.println("database password: " + password);
        System.out.println("111 " + a);
        System.out.println("222 " + b);
        System.out.println("333 " + c);
    }

}
