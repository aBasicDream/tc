package com.tcyh.common.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logback配置测试类
 * 
 * @author fp
 * @since 2025-09-18
 */
public class LogbackTest {
    
    private static final Logger logger = LoggerFactory.getLogger(LogbackTest.class);
    
    public static void main(String[] args) {
        // 测试不同级别的日志输出
        logger.trace("这是TRACE级别的日志");
        logger.debug("这是DEBUG级别的日志");
        logger.info("这是INFO级别的日志");
        logger.warn("这是WARN级别的日志");
        logger.error("这是ERROR级别的日志");
        
        // 测试异常日志
        try {
            throw new RuntimeException("测试异常日志");
        } catch (Exception e) {
            logger.error("捕获到异常", e);
        }
        
        System.out.println("Logback配置测试完成！");
    }
}
