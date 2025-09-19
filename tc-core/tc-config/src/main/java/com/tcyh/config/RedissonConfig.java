package com.tcyh.config;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson统一配置类
 * 
 * @author fp
 * @since 2025-01-17
 */
@Slf4j
@Configuration
public class RedissonConfig {

    @Value("${spring.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.redis.port:6379}")
    private int redisPort;

    @Value("${spring.redis.password:}")
    private String redisPassword;

    @Value("${spring.redis.database:0}")
    private int redisDatabase;

    @Value("${spring.redis.timeout:3000}")
    private int redisTimeout;

    /**
     * Redisson客户端配置
     */
    @Bean
    @ConditionalOnMissingBean(RedissonClient.class)
    public RedissonClient redissonClient() {
        Config config = new Config();
        
        // 单节点模式
        String address = "redis://" + redisHost + ":" + redisPort;
        config.useSingleServer()
                .setAddress(address)
                .setDatabase(redisDatabase)
                .setPassword(redisPassword.isEmpty() ? null : redisPassword)
                .setConnectionPoolSize(64)
                .setConnectionMinimumIdleSize(10)
                .setIdleConnectionTimeout(10000)
                .setConnectTimeout(redisTimeout)
                .setRetryAttempts(3)
                .setRetryInterval(1500)
                .setTimeout(redisTimeout)
                .setKeepAlive(true)
                .setPingConnectionInterval(30000);

        log.info("Redisson配置完成: host={}, port={}, database={}", redisHost, redisPort, redisDatabase);
        return Redisson.create(config);
    }
}
