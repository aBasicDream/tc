package com.tcyh.user.controller;

import com.tcyh.common.resp.RestResp;
import com.tcyh.user.dto.LoginRequest;
import com.tcyh.user.dto.LoginResponse;
import com.tcyh.user.dto.RegisterRequest;
import com.tcyh.user.service.AuthService;
import com.tcyh.user.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * 
 * @author fp
 * @since 2025-09-17
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "用户认证相关接口")
public class AuthController {

    private final AuthService authService;

    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "支持用户名/手机号/邮箱登录")
    public RestResp<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest,
                                         HttpServletRequest request) {
        try {
            // 获取客户端IP
            String clientIp = getClientIp(request);
            loginRequest.setLoginIp(clientIp);
            
            // 获取User-Agent作为设备信息
            String userAgent = request.getHeader("User-Agent");
            loginRequest.setLoginDevice(userAgent);
            
            LoginResponse response = authService.login(loginRequest);
            return RestResp.ok(response);
            
        } catch (Exception e) {
            log.error("用户登录失败: {}", e.getMessage());
            throw e;
        }
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "新用户注册")
    public RestResp<String> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            boolean success = authService.register(registerRequest);
            if (success) {
                return RestResp.ok("注册成功");
            } else {
                return RestResp.fail(null, com.tcyh.common.constant.ErrorCodeEnum.USER_REGISTER_FAIL);
            }
        } catch (Exception e) {
            log.error("用户注册失败: {}", e.getMessage());
            throw e;
        }
    }

    @PostMapping("/refresh")
    @Operation(summary = "刷新Token", description = "使用refresh token获取新的access token")
    public RestResp<String> refreshToken(@RequestParam String refreshToken) {
        try {
            String newToken = authService.refreshToken(refreshToken);
            if (newToken != null) {
                return RestResp.ok(newToken);
            } else {
                return RestResp.fail(null, com.tcyh.common.constant.ErrorCodeEnum.USER_TOKEN_REFRESH_FAIL);
            }
        } catch (Exception e) {
            log.error("刷新token失败: {}", e.getMessage());
            throw e;
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "用户登出，将token加入黑名单")
    public RestResp<String> logout(@RequestHeader("Authorization") String token) {
        try {
            // 移除Bearer前缀
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            boolean success = authService.logout(token);
            if (success) {
                return RestResp.ok("登出成功");
            } else {
                return RestResp.fail(null, com.tcyh.common.constant.ErrorCodeEnum.USER_LOGOUT_FAIL);
            }
        } catch (Exception e) {
            log.error("用户登出失败: {}", e.getMessage());
            throw e;
        }
    }

    @GetMapping("/validate")
    @Operation(summary = "验证Token", description = "验证token是否有效")
    public RestResp<Boolean> validateToken(@RequestParam String token) {
        try {
            boolean valid = authService.validateToken(token);
            return RestResp.ok(valid);
        } catch (Exception e) {
            log.error("验证token失败: {}", e.getMessage());
            return RestResp.ok(false);
        }
    }

    /**
     * 获取客户端真实IP
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}
