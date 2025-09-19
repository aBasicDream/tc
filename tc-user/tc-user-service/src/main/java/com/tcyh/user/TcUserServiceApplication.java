package com.tcyh.user;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 用户服务启动类
 * 
 * @author fp
 * @since 2025-09-17
 */
@SpringBootApplication(scanBasePackages = {"com.tcyh.user", "com.tcyh.config"})
@EnableCaching
@EnableTransactionManagement
@MapperScan("com.tcyh.user.mapper")
public class TcUserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TcUserServiceApplication.class, args);
        System.out.println("=================================");
        System.out.println("TC用户服务启动成功！");
        System.out.println("服务地址: http://localhost:8080/tc-user-service");
        System.out.println("API文档: http://localhost:8080/tc-user-service/swagger-ui.html");
        System.out.println("=================================");
    }
}
