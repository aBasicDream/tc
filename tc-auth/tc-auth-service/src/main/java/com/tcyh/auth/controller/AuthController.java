package com.tcyh.auth.controller;

import com.tcyh.auth.dto.*;
import com.tcyh.auth.entity.AuthUser;
import com.tcyh.auth.entity.SmsCode;
import com.tcyh.auth.service.AuthUserService;
import com.tcyh.auth.service.SmsCodeService;
import com.tcyh.auth.service.WechatLoginService;
import com.tcyh.common.constant.ErrorCodeEnum;
import com.tcyh.common.resp.RestResp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.stream.Collectors;

/**
 * 认证控制器
 * 
 * @author fp
 * @since 2025-09-18
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthUserService authUserService;
    private final AuthenticationManager authenticationManager;
    private final SmsCodeService smsCodeService;
    private final WechatLoginService wechatLoginService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public RestResp<UserInfoResponse> register(@RequestBody UserRegisterRequest request) {
        log.info("User registration request: {}", request.getUsername());
        
        try {
            // 验证密码确认
            if (!request.getPassword().equals(request.getConfirmPassword())) {
                return RestResp.fail(null, ErrorCodeEnum.USER_PASSWORD_NO_SAME);
            }
            
            // 创建用户
            AuthUser user = new AuthUser();
            user.setUsername(request.getUsername());
            user.setPassword(request.getPassword());
            user.setEmail(request.getEmail());
            user.setPhone(request.getPhone());
            user.setNickname(request.getNickname());
            
            AuthUser savedUser = authUserService.createUser(user);
            
            // 构建响应
            UserInfoResponse response = buildUserInfoResponse(savedUser);
            
            log.info("User registered successfully: {}", savedUser.getUsername());
            return RestResp.ok(response);
            
        } catch (Exception e) {
            log.error("User registration failed: {}", e.getMessage(), e);
            return RestResp.fail(null, ErrorCodeEnum.USER_REGISTER_FAIL);
        }
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public RestResp<UserInfoResponse> login(@RequestBody UserLoginRequest request) {
        log.info("User login request: {}", request.getUsername());
        
        try {
            // 执行认证
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            
            // 设置认证上下文
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // 获取用户信息
            AuthUser user = (AuthUser) authentication.getPrincipal();
            
            // 更新最后登录时间
            authUserService.updateLastLoginTime(user.getUsername());
            
            // 构建响应
            UserInfoResponse response = buildUserInfoResponse(user);
            
            log.info("User logged in successfully: {}", user.getUsername());
            return RestResp.ok(response);
            
        } catch (Exception e) {
            log.error("User login failed: {}", e.getMessage(), e);
            return RestResp.fail(null, ErrorCodeEnum.USER_PASSWORD_ERROR);
        }
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    public RestResp<UserInfoResponse> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return RestResp.fail(null, ErrorCodeEnum.USER_TOKEN_AUTH_FAIL);
        }
        
        AuthUser user = (AuthUser) authentication.getPrincipal();
        UserInfoResponse response = buildUserInfoResponse(user);
        
        return RestResp.ok(response);
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public RestResp<Void> logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null) {
            AuthUser user = (AuthUser) authentication.getPrincipal();
            log.info("User logged out: {}", user.getUsername());
        }
        
        // 清除认证上下文
        SecurityContextHolder.clearContext();
        
        return RestResp.ok(null);
    }

    /**
     * 检查用户名是否存在
     */
    @GetMapping("/check-username")
    public RestResp<Boolean> checkUsername(@RequestParam String username) {
        boolean exists = authUserService.existsByUsername(username);
        return RestResp.ok(exists);
    }

    /**
     * 检查邮箱是否存在
     */
    @GetMapping("/check-email")
    public RestResp<Boolean> checkEmail(@RequestParam String email) {
        boolean exists = authUserService.existsByEmail(email);
        return RestResp.ok(exists);
    }

    /**
     * 发送短信验证码
     */
    @PostMapping("/send-sms")
    public RestResp<Void> sendSms(@RequestBody SendSmsRequest request) {
        log.info("Send SMS request for phone: {}, type: {}", request.getPhone(), request.getCodeType());
        
        try {
            SmsCode.CodeType codeType = SmsCode.CodeType.valueOf(request.getCodeType().toUpperCase());
            smsCodeService.sendSmsCode(request.getPhone(), codeType, request.getDeviceInfo());
            
            log.info("SMS sent successfully to phone: {}", request.getPhone());
            return RestResp.ok(null);
            
        } catch (Exception e) {
            log.error("Failed to send SMS: {}", e.getMessage(), e);
            return RestResp.fail(null, ErrorCodeEnum.SYSTEM_ERROR);
        }
    }

    /**
     * 手机号验证码登录
     */
    @PostMapping("/login/phone")
    public RestResp<LoginResponse> phoneLogin(@RequestBody PhoneLoginRequest request) {
        log.info("Phone login request: {}", request.getPhone());
        
        try {
            // 验证短信验证码
            if (!smsCodeService.verifySmsCode(request.getPhone(), request.getCode(), SmsCode.CodeType.LOGIN)) {
                return RestResp.fail(null, ErrorCodeEnum.USER_PASSWORD_ERROR);
            }
            
            // 查找或创建用户
            AuthUser user = authUserService.findByPhone(request.getPhone())
                    .orElseGet(() -> {
                        // 如果用户不存在，创建新用户
                        AuthUser newUser = new AuthUser();
                        newUser.setPhone(request.getPhone());
                        newUser.setUsername("phone_" + request.getPhone());
                        newUser.setPassword(""); // 手机号登录不需要密码
                        newUser.setLoginType(AuthUser.LoginType.PHONE);
                        newUser.setDeviceInfo(request.getDeviceInfo());
                        return authUserService.createUser(newUser);
                    });
            
            // 更新登录信息
            authUserService.updateLastLoginTime(user.getUsername());
            
            // 构建响应
            LoginResponse response = buildLoginResponse(user, true);
            
            log.info("Phone login successful: {}", request.getPhone());
            return RestResp.ok(response);
            
        } catch (Exception e) {
            log.error("Phone login failed: {}", e.getMessage(), e);
            return RestResp.fail(null, ErrorCodeEnum.USER_LOGIN_FAIL);
        }
    }

    /**
     * 微信登录
     */
    @PostMapping("/login/wechat")
    public RestResp<LoginResponse> wechatLogin(@RequestBody WechatLoginRequest request) {
        log.info("Wechat login request with code: {}", request.getCode());
        
        try {
            AuthUser user = wechatLoginService.wechatLogin(request.getCode(), request.getDeviceInfo());
            
            // 更新登录信息
            authUserService.updateLastLoginTime(user.getUsername());
            
            // 构建响应
            LoginResponse response = buildLoginResponse(user, false);
            
            log.info("Wechat login successful: {}", user.getUsername());
            return RestResp.ok(response);
            
        } catch (Exception e) {
            log.error("Wechat login failed: {}", e.getMessage(), e);
            return RestResp.fail(null, ErrorCodeEnum.USER_LOGIN_FAIL);
        }
    }

    /**
     * 绑定微信账号
     */
    @PostMapping("/bind-wechat")
    public RestResp<Void> bindWechat(@RequestBody WechatLoginRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return RestResp.fail(null, ErrorCodeEnum.USER_TOKEN_AUTH_FAIL);
        }
        
        AuthUser user = (AuthUser) authentication.getPrincipal();
        log.info("Bind wechat request for user: {}", user.getUsername());
        
        try {
            wechatLoginService.bindWechatAccount(user.getId(), request.getCode());
            
            log.info("Wechat account bound successfully for user: {}", user.getUsername());
            return RestResp.ok(null);
            
        } catch (Exception e) {
            log.error("Failed to bind wechat account: {}", e.getMessage(), e);
            return RestResp.fail(null, ErrorCodeEnum.SYSTEM_ERROR);
    /**
     * 构建登录响应
     */
    private LoginResponse buildLoginResponse(AuthUser user, boolean isFirstLogin) {
        LoginResponse response = new LoginResponse();
        
        // TODO: 生成JWT令牌
        response.setAccessToken("jwt_access_token_" + user.getId());
        response.setRefreshToken("jwt_refresh_token_" + user.getId());
        response.setExpiresIn(3600L); // 1小时
        
        // 构建用户信息
        UserInfoResponse userInfo = buildUserInfoResponse(user);
        response.setUserInfo(userInfo);
        response.setFirstLogin(isFirstLogin);
        
        return response;
    }

    /**
     * 构建用户信息响应
     */
    private UserInfoResponse buildUserInfoResponse(AuthUser user) {
        UserInfoResponse response = new UserInfoResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setNickname(user.getNickname());
        response.setAvatar(user.getAvatar());
        response.setEnabled(user.getEnabled());
        response.setCreateTime(user.getCreateTime());
        response.setLastLoginTime(user.getLastLoginTime());
        
        if (user.getRoles() != null) {
            response.setRoles(user.getRoles().stream()
                    .map(Enum::name)
                    .collect(Collectors.toSet()));
        }
        
        return response;
    }
