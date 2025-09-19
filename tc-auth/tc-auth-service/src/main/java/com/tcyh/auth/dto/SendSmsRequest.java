package com.tcyh.auth.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 发送短信验证码请求DTO
 * 
 * @author fp
 * @since 2025-09-18
 */
@Data
public class SendSmsRequest {

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 验证码类型
     */
    @NotBlank(message = "验证码类型不能为空")
    private String codeType;

    /**
     * 设备信息
     */
    private String deviceInfo;
}
