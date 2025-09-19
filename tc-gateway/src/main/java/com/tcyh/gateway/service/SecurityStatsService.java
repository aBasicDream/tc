package com.tcyh.gateway.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 安全统计服务 - 基于Redisson
 * 
 * @author fp
 * @since 2025-09-17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityStatsService {

    private final RedissonClient redissonClient;

    // 统计键前缀
    private static final String STATS_PREFIX = "security:stats:";
    private static final String LOGIN_SUCCESS_PREFIX = "login:success:";
    private static final String LOGIN_FAILED_PREFIX = "login:failed:";
    private static final String TOKEN_VALIDATE_PREFIX = "token:validate:";
    private static final String BLACKLIST_HIT_PREFIX = "blacklist:hit:";

    /**
     * 记录登录成功
     */
    public Mono<Void> recordLoginSuccess(String username, String clientIp) {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String hour = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH"));

        return Mono.fromRunnable(() -> {
            // 按日期统计
            RAtomicLong dateCounter = redissonClient.getAtomicLong(STATS_PREFIX + LOGIN_SUCCESS_PREFIX + date);
            dateCounter.incrementAndGet();

            // 按小时统计
            RAtomicLong hourCounter = redissonClient.getAtomicLong(STATS_PREFIX + LOGIN_SUCCESS_PREFIX + hour);
            hourCounter.incrementAndGet();

            // 按用户统计
            RAtomicLong userCounter = redissonClient.getAtomicLong(STATS_PREFIX + LOGIN_SUCCESS_PREFIX + "user:" + username);
            userCounter.incrementAndGet();

            // 按IP统计
            RAtomicLong ipCounter = redissonClient.getAtomicLong(STATS_PREFIX + LOGIN_SUCCESS_PREFIX + "ip:" + clientIp);
            ipCounter.incrementAndGet();

            log.debug("记录登录成功统计: username={}, ip={}", username, clientIp);
        });
    }

    /**
     * 记录登录失败
     */
    public Mono<Void> recordLoginFailed(String username, String clientIp, String reason) {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String hour = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH"));

        return Mono.fromRunnable(() -> {
            // 按日期统计
            RAtomicLong dateCounter = redissonClient.getAtomicLong(STATS_PREFIX + LOGIN_FAILED_PREFIX + date);
            dateCounter.incrementAndGet();

            // 按小时统计
            RAtomicLong hourCounter = redissonClient.getAtomicLong(STATS_PREFIX + LOGIN_FAILED_PREFIX + hour);
            hourCounter.incrementAndGet();

            // 按用户统计
            RAtomicLong userCounter = redissonClient.getAtomicLong(STATS_PREFIX + LOGIN_FAILED_PREFIX + "user:" + username);
            userCounter.incrementAndGet();

            // 按IP统计
            RAtomicLong ipCounter = redissonClient.getAtomicLong(STATS_PREFIX + LOGIN_FAILED_PREFIX + "ip:" + clientIp);
            ipCounter.incrementAndGet();

            log.warn("记录登录失败统计: username={}, ip={}, reason={}", username, clientIp, reason);
        });
    }

    /**
     * 记录Token验证成功
     */
    public Mono<Void> recordTokenValidateSuccess(String username, String clientIp) {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String hour = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH"));

        return Mono.fromRunnable(() -> {
            // 按日期统计
            RAtomicLong dateCounter = redissonClient.getAtomicLong(STATS_PREFIX + TOKEN_VALIDATE_PREFIX + "success:" + date);
            dateCounter.incrementAndGet();

            // 按小时统计
            RAtomicLong hourCounter = redissonClient.getAtomicLong(STATS_PREFIX + TOKEN_VALIDATE_PREFIX + "success:" + hour);
            hourCounter.incrementAndGet();

            // 按用户统计
            RAtomicLong userCounter = redissonClient.getAtomicLong(STATS_PREFIX + TOKEN_VALIDATE_PREFIX + "success:user:" + username);
            userCounter.incrementAndGet();

            // 按IP统计
            RAtomicLong ipCounter = redissonClient.getAtomicLong(STATS_PREFIX + TOKEN_VALIDATE_PREFIX + "success:ip:" + clientIp);
            ipCounter.incrementAndGet();

            log.debug("记录Token验证成功统计: username={}, ip={}", username, clientIp);
        });
    }

    /**
     * 记录Token验证失败
     */
    public Mono<Void> recordTokenValidateFailed(String token, String clientIp, String reason) {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String hour = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH"));

        return Mono.fromRunnable(() -> {
            // 按日期统计
            RAtomicLong dateCounter = redissonClient.getAtomicLong(STATS_PREFIX + TOKEN_VALIDATE_PREFIX + "failed:" + date);
            dateCounter.incrementAndGet();

            // 按小时统计
            RAtomicLong hourCounter = redissonClient.getAtomicLong(STATS_PREFIX + TOKEN_VALIDATE_PREFIX + "failed:" + hour);
            hourCounter.incrementAndGet();

            // 按IP统计
            RAtomicLong ipCounter = redissonClient.getAtomicLong(STATS_PREFIX + TOKEN_VALIDATE_PREFIX + "failed:ip:" + clientIp);
            ipCounter.incrementAndGet();

            log.warn("记录Token验证失败统计: token={}, ip={}, reason={}",
                    token.substring(0, Math.min(20, token.length())) + "...", clientIp, reason);
        });
    }

    /**
     * 记录黑名单命中
     */
    public Mono<Void> recordBlacklistHit(String token, String clientIp) {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String hour = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH"));

        return Mono.fromRunnable(() -> {
            // 按日期统计
            RAtomicLong dateCounter = redissonClient.getAtomicLong(STATS_PREFIX + BLACKLIST_HIT_PREFIX + date);
            dateCounter.incrementAndGet();

            // 按小时统计
            RAtomicLong hourCounter = redissonClient.getAtomicLong(STATS_PREFIX + BLACKLIST_HIT_PREFIX + hour);
            hourCounter.incrementAndGet();

            // 按IP统计
            RAtomicLong ipCounter = redissonClient.getAtomicLong(STATS_PREFIX + BLACKLIST_HIT_PREFIX + "ip:" + clientIp);
            ipCounter.incrementAndGet();

            log.warn("记录黑名单命中统计: token={}, ip={}",
                    token.substring(0, Math.min(20, token.length())) + "...", clientIp);
        });
    }

    /**
     * 获取今日登录成功次数
     */
    public Mono<Long> getTodayLoginSuccessCount() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return Mono.fromCallable(() -> {
            RAtomicLong counter = redissonClient.getAtomicLong(STATS_PREFIX + LOGIN_SUCCESS_PREFIX + date);
            return counter.get();
        });
    }

    /**
     * 获取今日登录失败次数
     */
    public Mono<Long> getTodayLoginFailedCount() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return Mono.fromCallable(() -> {
            RAtomicLong counter = redissonClient.getAtomicLong(STATS_PREFIX + LOGIN_FAILED_PREFIX + date);
            return counter.get();
        });
    }

    /**
     * 获取今日Token验证成功次数
     */
    public Mono<Long> getTodayTokenValidateSuccessCount() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return Mono.fromCallable(() -> {
            RAtomicLong counter = redissonClient.getAtomicLong(STATS_PREFIX + TOKEN_VALIDATE_PREFIX + "success:" + date);
            return counter.get();
        });
    }

    /**
     * 获取今日Token验证失败次数
     */
    public Mono<Long> getTodayTokenValidateFailedCount() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return Mono.fromCallable(() -> {
            RAtomicLong counter = redissonClient.getAtomicLong(STATS_PREFIX + TOKEN_VALIDATE_PREFIX + "failed:" + date);
            return counter.get();
        });
    }

    /**
     * 设置统计数据的过期时间
     */
    public Mono<Void> setStatsExpiration(String key, Duration duration) {
        return Mono.fromRunnable(() -> {
            RBucket<Object> bucket = redissonClient.getBucket(key);
            bucket.expire(duration);
        });
    }
}