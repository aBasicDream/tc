package com.tcyh.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tcyh.gateway.service.SecurityStatsService;
import com.tcyh.gateway.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT认证过滤器 - 基于Redisson
 * 
 * @author fp
 * @since 2025-09-17
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;
    private final RedissonClient redissonClient;
    private final ObjectMapper objectMapper;
    private final SecurityStatsService securityStatsService;

    // 不需要认证的路径
    private static final String[] PUBLIC_PATHS = {
            "/api/auth/login",
            "/api/auth/register",
            "/api/public/**",
            "/api/monitor/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/webjars/**",
            "/actuator/**",
            "/favicon.ico"
    };

    // 黑名单前缀
    private static final String BLACKLIST_PREFIX = "blacklist:";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();
        String clientIp = getClientIp(request);
        long startTime = System.currentTimeMillis();
        
        log.debug("认证过滤器开始处理请求: {} from {}", path, clientIp);
        
        // 检查是否为公开路径
        if (isPublicPath(path)) {
            log.debug("公开路径，跳过认证: {}", path);
            return chain.filter(exchange);
        }

        // 获取Authorization头
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            log.warn("缺少认证信息: {} from {}", path, clientIp);
            return unauthorizedResponse(exchange, "缺少认证信息");
        }

        String token = authHeader.substring(7);
        log.debug("开始验证Token: {} from {}", token.substring(0, Math.min(20, token.length())) + "...", clientIp);
        
        // 检查token是否在黑名单中
        return Mono.fromCallable(() -> {
            RBucket<String> blacklistBucket = redissonClient.getBucket(BLACKLIST_PREFIX + token);
            return blacklistBucket.isExists();
        }).flatMap(isBlacklisted -> {
            if (Boolean.TRUE.equals(isBlacklisted)) {
                log.warn("Token在黑名单中: {} from {}", token.substring(0, Math.min(20, token.length())) + "...", clientIp);
                // 记录黑名单命中统计
                securityStatsService.recordBlacklistHit(token, clientIp).subscribe();
                return unauthorizedResponse(exchange, "Token已失效");
            }
            
            // 验证token
            if (!jwtUtil.validateToken(token)) {
                log.warn("Token验证失败: {} from {}", token.substring(0, Math.min(20, token.length())) + "...", clientIp);
                // 记录Token验证失败统计
                securityStatsService.recordTokenValidateFailed(token, clientIp, "Token无效或已过期").subscribe();
                return unauthorizedResponse(exchange, "Token无效或已过期");
            }
            
            // 获取用户信息并添加到请求头
            Long userId = jwtUtil.getUserIdFromToken(token);
            String username = jwtUtil.getUsernameFromToken(token);
            
            if (userId == null || !StringUtils.hasText(username)) {
                log.warn("Token信息不完整: userId={}, username={} from {}", userId, username, clientIp);
                // 记录Token验证失败统计
                securityStatsService.recordTokenValidateFailed(token, clientIp, "Token信息不完整").subscribe();
                return unauthorizedResponse(exchange, "Token信息不完整");
            }
            
            log.debug("Token验证成功: userId={}, username={} from {}", userId, username, clientIp);
            // 记录Token验证成功统计
            securityStatsService.recordTokenValidateSuccess(username, clientIp).subscribe();
            
            // 添加用户信息到请求头
            ServerHttpRequest mutatedRequest = request.mutate()
                    .header("X-User-Id", userId.toString())
                    .header("X-Username", username)
                    .header("X-Gateway", "tc-gateway")
                    .header("X-Client-Ip", clientIp)
                    .build();
            
            ServerWebExchange mutatedExchange = exchange.mutate()
                    .request(mutatedRequest)
                    .build();
            
            // 记录处理时间
            long processingTime = System.currentTimeMillis() - startTime;
            log.debug("认证处理完成，耗时: {}ms, userId: {}, path: {}", processingTime, userId, path);
            
            return chain.filter(mutatedExchange);
        });
    }

    /**
     * 检查是否为公开路径
     */
    private boolean isPublicPath(String path) {
        return Arrays.stream(PUBLIC_PATHS)
                .anyMatch(pattern -> path.matches(pattern.replace("**", ".*")));
    }

    /**
     * 返回未授权响应
     */
    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 401);
        result.put("message", message);
        result.put("timestamp", System.currentTimeMillis());
        result.put("path", exchange.getRequest().getPath().value());
        
        try {
            String body = objectMapper.writeValueAsString(result);
            DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Flux.just(buffer));
        } catch (JsonProcessingException e) {
            log.error("序列化响应失败: {}", e.getMessage());
            return response.setComplete();
        }
    }

    /**
     * 获取客户端真实IP
     */
    private String getClientIp(ServerHttpRequest request) {
        String xForwardedFor = request.getHeaders().getFirst("X-Forwarded-For");
        if (StringUtils.hasText(xForwardedFor) && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeaders().getFirst("X-Real-IP");
        if (StringUtils.hasText(xRealIp) && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddress() != null ? request.getRemoteAddress().getAddress().getHostAddress() : "unknown";
    }

    @Override
    public int getOrder() {
        return -100; // 优先级最高
    }
}