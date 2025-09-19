package com.tcyh.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
/**
 * OAuth2认证服务启动类
 * 
 * @author fp
 * @since 2025-09-18
 */
@SpringBootApplication(scanBasePackages = {"com.tcyh.auth", "com.tcyh.common", "com.tcyh.config"})
@EntityScan(basePackages = {"com.tcyh.auth.entity"})
public class TcAuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TcAuthServiceApplication.class, args);
    }
}
