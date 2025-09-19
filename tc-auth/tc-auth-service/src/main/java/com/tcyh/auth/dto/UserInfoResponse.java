package com.tcyh.auth.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * 用户信息响应DTO
 * 
 * @author fp
 * @since 2025-09-18
 */
@Data
public class UserInfoResponse {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 用户角色
     */
    private Set<String> roles;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;
}
