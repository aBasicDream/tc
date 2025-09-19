package com.tcyh.auth.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * 用户实体类
 * 
 * @author fp
 * @since 2025-09-18
 */
@Data
@Entity
@Table(name = "auth_user")
@EqualsAndHashCode(callSuper = false)
public class AuthUser implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户名
     */
    @Column(unique = true, nullable = false, length = 50)
    private String username;

    /**
     * 密码
     */
    @Column(nullable = false)
    private String password;

    /**
     * 邮箱
     */
    @Column(unique = true, length = 100)
    private String email;

    /**
     * 手机号
     */
    @Column(unique = true, length = 20)
    private String phone;

    /**
     * 昵称
     */
    @Column(length = 50)
    private String nickname;

    /**
     * 头像URL
     */
    @Column(length = 500)
    private String avatar;

    /**
     * 是否启用
     */
    @Column(nullable = false)
    private Boolean enabled = true;

    /**
     * 是否未过期
     */
    @Column(nullable = false)
    private Boolean accountNonExpired = true;

    /**
     * 是否未锁定
     */
    @Column(nullable = false)
    private Boolean accountNonLocked = true;

    /**
     * 凭证是否未过期
     */
    @Column(nullable = false)
    private Boolean credentialsNonExpired = true;

    /**
     * 创建时间
     */
    @Column(nullable = false)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Column(nullable = false)
    private LocalDateTime updateTime;

    /**
     * 微信OpenID
     */
    @Column(unique = true, length = 100)
    private String wechatOpenId;

    /**
     * 微信UnionID
     */
    @Column(unique = true, length = 100)
    private String wechatUnionId;

    /**
     * 微信昵称
     */
    @Column(length = 100)
    private String wechatNickname;

    /**
     * 微信头像
     */
    @Column(length = 500)
    private String wechatAvatar;

    /**
     * 登录类型
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoginType loginType = LoginType.PASSWORD;

    /**
     * 最后登录IP
     */
    @Column(length = 50)
    private String lastLoginIp;

    /**
     * 登录设备信息
     */
    @Column(length = 200)
    private String deviceInfo;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
        if (roles == null || roles.isEmpty()) {
            roles = Collections.singleton(UserRole.USER);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (roles == null || roles.isEmpty()) {
            return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        }
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 用户角色枚举
     */
    public enum UserRole {
        USER, ADMIN, MODERATOR
    }

    /**
     * 登录类型枚举
     */
    public enum LoginType {
        PASSWORD,    // 密码登录
        PHONE,       // 手机号登录
        WECHAT,      // 微信登录
        SMS_CODE     // 短信验证码登录
    }
}
