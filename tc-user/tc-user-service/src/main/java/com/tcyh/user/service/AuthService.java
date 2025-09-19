package com.tcyh.user.service;

import com.tcyh.user.dto.LoginRequest;
import com.tcyh.user.dto.LoginResponse;
import com.tcyh.user.dto.RegisterRequest;
import com.tcyh.user.entity.UserInfo;
import com.tcyh.user.entity.UserLoginLog;

/**
 * 认证服务接口
 * 
 * @author fp
 * @since 2025-09-17
 */
public interface AuthService {

    /**
     * 用户登录
     * 
     * @param loginRequest 登录请求
     * @return 登录响应
     */
    LoginResponse login(LoginRequest loginRequest);

    /**
     * 用户注册
     * 
     * @param registerRequest 注册请求
     * @return 是否成功
     */
    boolean register(RegisterRequest registerRequest);

    /**
     * 刷新token
     * 
     * @param refreshToken 刷新token
     * @return 新的token
     */
    String refreshToken(String refreshToken);

    /**
     * 用户登出
     * 
     * @param token 用户token
     * @return 是否成功
     */
    boolean logout(String token);

    /**
     * 验证token
     * 
     * @param token JWT token
     * @return 是否有效
     */
    boolean validateToken(String token);

    /**
     * 记录登录日志
     * 
     * @param userId 用户ID
     * @param loginIp 登录IP
     * @param loginDevice 登录设备
     * @param loginLocation 登录地点
     */
    void recordLoginLog(Long userId, String loginIp, String loginDevice, String loginLocation);


}
