package com.tcyh.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 性能监控过滤器
 * 
 * @author fp
 * @since 2025-09-17
 */
@Slf4j
@Component
public class PerformanceMonitorFilter implements GlobalFilter, Ordered {

    private static final String START_TIME_HEADER = "X-Start-Time";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        long startTime = System.currentTimeMillis();
        
        // 记录请求开始时间
        ServerHttpRequest mutatedRequest = request.mutate()
                .header(START_TIME_HEADER, String.valueOf(startTime))
                .build();
        
        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(mutatedRequest)
                .build();
        
        return chain.filter(mutatedExchange)
                .doOnSuccess(aVoid -> {
                    long endTime = System.currentTimeMillis();
                    long duration = endTime - startTime;
                    
                    String path = request.getPath().value();
                    String method = request.getMethod().name();
                    String clientIp = getClientIp(request);
                    
                    // 记录性能指标
                    log.info("请求处理完成: {} {} from {} 耗时: {}ms", 
                            method, path, clientIp, duration);
                    
                    // 性能告警
                    if (duration > 5000) { // 超过5秒
                        log.warn("慢请求告警: {} {} from {} 耗时: {}ms", 
                                method, path, clientIp, duration);
                    }
                })
                .doOnError(throwable -> {
                    long endTime = System.currentTimeMillis();
                    long duration = endTime - startTime;
                    
                    String path = request.getPath().value();
                    String method = request.getMethod().name();
                    String clientIp = getClientIp(request);
                    
                    log.error("请求处理失败: {} {} from {} 耗时: {}ms 错误: {}", 
                            method, path, clientIp, duration, throwable.getMessage());
                });
    }

    /**
     * 获取客户端真实IP
     */
    private String getClientIp(ServerHttpRequest request) {
        String xForwardedFor = request.getHeaders().getFirst("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeaders().getFirst("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddress() != null ? request.getRemoteAddress().getAddress().getHostAddress() : "unknown";
    }

    @Override
    public int getOrder() {
        return -200; // 优先级最高，在认证过滤器之前执行
    }
}
