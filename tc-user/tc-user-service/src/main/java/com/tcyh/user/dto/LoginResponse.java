package com.tcyh.user.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 登录响应DTO
 * 
 * @author fp
 * @since 2025-09-17
 */
@Data
public class LoginResponse {

    /**
     * 访问token
     */
    private String accessToken;

    /**
     * 刷新token
     */
    private String refreshToken;

    /**
     * token类型
     */
    private String tokenType = "Bearer";

    /**
     * 过期时间（秒）
     */
    private Long expiresIn;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 登录时间
     */
    private LocalDateTime loginTime;
}
