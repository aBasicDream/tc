package com.tcyh.common.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * Logback XML配置测试类
 * 用于验证XML配置是否正确解析
 * 
 * @author fp
 * @since 2025-09-18
 */
public class LogbackXmlTest {
    
    private static final Logger logger = LoggerFactory.getLogger(LogbackXmlTest.class);
    
    public static void main(String[] args) {
        System.out.println("开始测试Logback XML配置...");
        
        // 测试基本日志输出
        logger.info("测试INFO级别日志输出");
        logger.warn("测试WARN级别日志输出");
        logger.error("测试ERROR级别日志输出");
        
        // 测试MDC
        MDC.put("userId", "12345");
        MDC.put("requestId", "req-001");
        logger.info("测试MDC上下文日志");
        MDC.clear();
        
        // 测试异常日志
        try {
            throw new RuntimeException("测试异常日志输出");
        } catch (Exception e) {
            logger.error("捕获到异常", e);
        }
        
        // 测试不同包名的日志
        Logger gatewayLogger = LoggerFactory.getLogger("com.tcyh.gateway");
        gatewayLogger.info("测试网关模块日志");
        
        Logger userLogger = LoggerFactory.getLogger("com.tcyh.user");
        userLogger.info("测试用户模块日志");
        
        System.out.println("Logback XML配置测试完成！");
        System.out.println("请检查控制台输出和日志文件是否正确生成");
    }
}
