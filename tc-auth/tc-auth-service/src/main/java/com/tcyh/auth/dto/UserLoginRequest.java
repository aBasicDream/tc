package com.tcyh.auth.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 用户登录请求DTO
 * 
 * @author fp
 * @since 2025-09-18
 */
@Data
public class UserLoginRequest {

    /**
     * 用户名或邮箱
     */
    @NotBlank(message = "用户名或邮箱不能为空")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 记住我
     */
    private Boolean rememberMe = false;
}
