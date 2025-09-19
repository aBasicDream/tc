package com.tcyh.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * OAuth2资源服务器配置
 * 
 * @author fp
 * @since 2025-09-18
 */
@Configuration
@EnableWebSecurity
public class OAuth2ResourceServerConfig {

    /**
     * 资源服务器安全过滤器链
     */
    @Bean
    public SecurityFilterChain resourceServerSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder())
                        )
                )
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    /**
     * JWT解码器
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        // 这里应该配置实际的JWT解码器
        // 可以从授权服务器获取公钥或使用共享密钥
        return NimbusJwtDecoder.withJwkSetUri("http://localhost:9000/.well-known/jwks.json").build();
    }
}
