package com.tcyh.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * 网关路由配置
 * 
 * @author fp
 * @since 2025-09-17
 */
@Configuration
public class GatewayConfig {

    /**
     * 路由配置
     */
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // 允许的域,不要写*，否则cookie就无法使用了
        config.addAllowedOrigin("http://localhost:8089");
        // 允许的头信息
        config.addAllowedHeader("*");
        // 允许的请求方式
        config.addAllowedMethod("*");
        // 是否允许携带Cookie信息
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();
        // 添加映射路径，拦截一切请求
        configurationSource.registerCorsConfiguration("/**", config);
        return new CorsWebFilter(configurationSource);
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .csrf(csrf -> csrf.disable());
        return http.build();
    }
}
