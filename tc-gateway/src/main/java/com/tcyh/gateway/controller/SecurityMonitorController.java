package com.tcyh.gateway.controller;

import com.tcyh.common.resp.RestResp;
import com.tcyh.gateway.service.SecurityStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * 安全监控控制器
 * 
 * @author fp
 * @since 2025-09-17
 */
@Slf4j
@RestController
@RequestMapping("/api/monitor")
@RequiredArgsConstructor
@Tag(name = "安全监控", description = "认证链路监控相关接口")
public class SecurityMonitorController {

    private final SecurityStatsService securityStatsService;

    @GetMapping("/stats")
    @Operation(summary = "获取安全统计信息", description = "获取今日认证相关的统计数据")
    public Mono<RestResp<Map<String, Object>>> getSecurityStats() {
        return Mono.zip(
                securityStatsService.getTodayLoginSuccessCount(),
                securityStatsService.getTodayLoginFailedCount(),
                securityStatsService.getTodayTokenValidateSuccessCount(),
                securityStatsService.getTodayTokenValidateFailedCount()
        ).map(tuple -> {
            Map<String, Object> stats = new HashMap<>();
            stats.put("loginSuccessCount", tuple.getT1());
            stats.put("loginFailedCount", tuple.getT2());
            stats.put("tokenValidateSuccessCount", tuple.getT3());
            stats.put("tokenValidateFailedCount", tuple.getT4());
            
            // 计算成功率
            long totalLogin = tuple.getT1() + tuple.getT2();
            long totalTokenValidate = tuple.getT3() + tuple.getT4();
            
            double loginSuccessRate = totalLogin > 0 ? (double) tuple.getT1() / totalLogin * 100 : 0;
            double tokenValidateSuccessRate = totalTokenValidate > 0 ? (double) tuple.getT3() / totalTokenValidate * 100 : 0;
            
            stats.put("loginSuccessRate", String.format("%.2f%%", loginSuccessRate));
            stats.put("tokenValidateSuccessRate", String.format("%.2f%%", tokenValidateSuccessRate));
            stats.put("timestamp", System.currentTimeMillis());
            
            return RestResp.ok(stats);
        });
    }

    @GetMapping("/health")
    @Operation(summary = "健康检查", description = "检查网关服务健康状态")
    public RestResp<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "tc-gateway");
        health.put("timestamp", System.currentTimeMillis());
        health.put("version", "1.0.0");
        
        return RestResp.ok(health);
    }

    @GetMapping("/routes")
    @Operation(summary = "获取路由信息", description = "获取网关配置的路由信息")
    public RestResp<Map<String, Object>> getRoutes() {
        Map<String, Object> routes = new HashMap<>();
        
        Map<String, Object> routeConfig = new HashMap<>();
        routeConfig.put("/api/auth/**", "认证服务路由");
        routeConfig.put("/api/user/**", "用户服务路由");
        routeConfig.put("/api/public/**", "公开接口路由");
        routeConfig.put("/api/test/**", "测试接口路由");
        routeConfig.put("/swagger-ui/**", "API文档路由");
        routeConfig.put("/actuator/**", "健康检查路由");
        
        routes.put("routes", routeConfig);
        routes.put("totalRoutes", routeConfig.size());
        routes.put("timestamp", System.currentTimeMillis());
        
        return RestResp.ok(routes);
    }
}
