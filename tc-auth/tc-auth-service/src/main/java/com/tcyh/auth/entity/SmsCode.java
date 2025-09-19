package com.tcyh.auth.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 短信验证码实体类
 * 
 * @author fp
 * @since 2025-09-18
 */
@Data
@Entity
@Table(name = "sms_code")
@EqualsAndHashCode(callSuper = false)
public class SmsCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 手机号
     */
    @Column(nullable = false, length = 20)
    private String phone;

    /**
     * 验证码
     */
    @Column(nullable = false, length = 10)
    private String code;

    /**
     * 验证码类型
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CodeType codeType;

    /**
     * 过期时间
     */
    @Column(nullable = false)
    private LocalDateTime expireTime;

    /**
     * 是否已使用
     */
    @Column(nullable = false)
    private Boolean used = false;

    /**
     * 创建时间
     */
    @Column(nullable = false)
    private LocalDateTime createTime;

    /**
     * 使用时间
     */
    private LocalDateTime usedTime;

    /**
     * IP地址
     */
    @Column(length = 50)
    private String ipAddress;

    /**
     * 设备信息
     */
    @Column(length = 200)
    private String deviceInfo;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        if (expireTime == null) {
            expireTime = LocalDateTime.now().plusMinutes(5); // 默认5分钟过期
        }
    }

    /**
     * 验证码类型枚举
     */
    public enum CodeType {
        LOGIN,      // 登录验证码
        REGISTER,   // 注册验证码
        RESET_PASSWORD, // 重置密码验证码
        BIND_PHONE  // 绑定手机号验证码
    }
}
