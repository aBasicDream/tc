package com.tcyh.auth.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 微信登录请求DTO
 * 
 * @author fp
 * @since 2025-09-18
 */
@Data
public class WechatLoginRequest {

    /**
     * 微信授权码
     */
    @NotBlank(message = "微信授权码不能为空")
    private String code;

    /**
     * 设备信息
     */
    private String deviceInfo;

    /**
     * 客户端类型 (android, ios, web)
     */
    private String clientType;
}
