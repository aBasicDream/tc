package com.tcyh.gateway.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器
 * 
 * @author fp
 * @since 2025-09-17
 */
@Slf4j
@Component
@Order(-1)
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    public GlobalExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        
        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        
        Map<String, Object> result = new HashMap<>();
        result.put("timestamp", System.currentTimeMillis());
        result.put("path", exchange.getRequest().getPath().value());
        
        if (ex instanceof NotFoundException) {
            response.setStatusCode(HttpStatus.NOT_FOUND);
            result.put("code", 404);
            result.put("message", "服务不可用");
            log.error("服务不可用: {}", ex.getMessage());
        } else if (ex instanceof ResponseStatusException) {
            ResponseStatusException responseStatusException = (ResponseStatusException) ex;
            response.setStatusCode(responseStatusException.getStatusCode());
            result.put("code", responseStatusException.getStatusCode().value());
            result.put("message", responseStatusException.getReason());
            log.error("响应状态异常: {}", ex.getMessage());
        } else {
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            result.put("code", 500);
            result.put("message", "系统内部错误");
            log.error("系统内部错误: ", ex);
        }

        try {
            String body = objectMapper.writeValueAsString(result);
            DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Flux.just(buffer));
        } catch (JsonProcessingException e) {
            log.error("序列化响应失败: {}", e.getMessage());
            return response.setComplete();
        }
    }
}
