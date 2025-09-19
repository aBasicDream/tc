package com.tcyh.user.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT认证入口点
 * 
 * @author fp
 * @since 2025-09-17
 */
@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        
        log.error("未授权访问: {}", authException.getMessage());
        
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        Map<String, Object> body = new HashMap<>();
        body.put("code", 401);
        body.put("message", "未授权访问");
        body.put("timestamp", System.currentTimeMillis());
        body.put("path", request.getRequestURI());
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);
    }
}
