package com.tcyh.user.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 登录请求DTO
 * 
 * @author fp
 * @since 2025-09-17
 */
@Data
public class LoginRequest {

    /**
     * 用户名/手机号/邮箱
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 登录IP
     */
    private String loginIp;

    /**
     * 登录设备
     */
    private String loginDevice;

    /**
     * 登录地点
     */
    private String loginLocation;
}
