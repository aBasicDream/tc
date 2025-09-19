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
 * 请求日志过滤器
 * 
 * @author fp
 * @since 2025-09-17
 */
@Slf4j
@Component
public class RequestLogFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        String path = request.getPath().value();
        String method = request.getMethod().name();
        String clientIp = getClientIp(request);
        String userAgent = request.getHeaders().getFirst("User-Agent");
        String userId = request.getHeaders().getFirst("X-User-Id");
        String username = request.getHeaders().getFirst("X-Username");
        
        // 记录请求日志
        log.info("请求开始: {} {} from {} User-Agent: {} UserId: {} Username: {}", 
                method, path, clientIp, userAgent, userId, username);
        
        return chain.filter(exchange)
                .doOnSuccess(aVoid -> {
                    ServerHttpResponse response = exchange.getResponse();
                    log.info("请求成功: {} {} from {} 状态码: {}", 
                            method, path, clientIp, response.getStatusCode());
                })
                .doOnError(throwable -> {
                    log.error("请求失败: {} {} from {} 错误: {}", 
                            method, path, clientIp, throwable.getMessage());
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
        return -300; // 优先级最高，最先执行
    }
}
