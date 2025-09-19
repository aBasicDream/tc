package com.tcyh.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 网关服务启动类
 * 
 * @author fp
 * @since 2025-09-17
 */
@SpringBootApplication(scanBasePackages = {"com.tcyh.gateway", "com.tcyh.config"})
@EnableDiscoveryClient
public class TcGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(TcGatewayApplication.class, args);
        System.out.println("=================================");
        System.out.println("TC网关服务启动成功！");
        System.out.println("网关地址: http://localhost:8080");
        System.out.println("API文档: http://localhost:8080/swagger-ui.html");
        System.out.println("=================================");
    }
}
