package com.tcyh.auth.dto;

import lombok.Data;

/**
 * 登录响应DTO
 * 
 * @author fp
 * @since 2025-09-18
 */
@Data
public class LoginResponse {

    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * 刷新令牌
     */
    private String refreshToken;

    /**
     * 令牌类型
     */
    private String tokenType = "Bearer";

    /**
     * 过期时间（秒）
     */
    private Long expiresIn;

    /**
     * 用户信息
     */
    private UserInfoResponse userInfo;

    /**
     * 是否首次登录
     */
    private Boolean firstLogin = false;
}
