package com.tcyh.user.controller;

import com.tcyh.common.resp.RestResp;
import com.tcyh.user.dto.LoginRequest;
import com.tcyh.user.dto.RegisterRequest;
import com.tcyh.user.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 测试控制器
 * 
 * @author fp
 * @since 2025-09-17
 */
@Slf4j
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@Tag(name = "测试接口", description = "系统测试相关接口")
public class TestController {

    private final AuthService authService;

    @GetMapping("/hello")
    @Operation(summary = "Hello World", description = "测试接口连通性")
    public RestResp<String> hello() {
        return RestResp.ok("Hello, TC User Service is running!");
    }

    @PostMapping("/register-test")
    @Operation(summary = "测试注册", description = "创建测试用户")
    public RestResp<String> registerTest() {
        try {
            RegisterRequest request = new RegisterRequest();
            request.setUsername("testuser");
            request.setPassword("Test123456");
            request.setConfirmPassword("Test123456");
            request.setNickname("测试用户");
            request.setPhone("13800138001");
            request.setEmail("test@example.com");
            
            boolean success = authService.register(request);
            if (success) {
                return RestResp.ok("测试用户注册成功");
            } else {
                return RestResp.fail(null, com.tcyh.common.constant.ErrorCodeEnum.USER_REGISTER_FAIL);
            }
        } catch (Exception e) {
            log.error("测试注册失败: {}", e.getMessage());
            return RestResp.fail(null, com.tcyh.common.constant.ErrorCodeEnum.USER_REGISTER_FAIL);
        }
    }

    @PostMapping("/login-test")
    @Operation(summary = "测试登录", description = "测试用户登录")
    public RestResp<String> loginTest() {
        try {
            LoginRequest request = new LoginRequest();
            request.setUsername("testuser");
            request.setPassword("Test123456");
            
            var response = authService.login(request);
            return RestResp.ok("测试登录成功，Token: " + response.getAccessToken());
        } catch (Exception e) {
            log.error("测试登录失败: {}", e.getMessage());
            return RestResp.fail(null, com.tcyh.common.constant.ErrorCodeEnum.USER_LOGIN_FAIL);
        }
    }
}
