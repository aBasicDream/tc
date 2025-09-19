package com.tcyh.auth.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 微信用户信息实体类
 * 
 * @author fp
 * @since 2025-09-18
 */
@Data
@Entity
@Table(name = "wechat_user")
@EqualsAndHashCode(callSuper = false)
public class WechatUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 关联的用户ID
     */
    @Column(nullable = false)
    private Long userId;

    /**
     * 微信OpenID
     */
    @Column(unique = true, nullable = false, length = 100)
    private String openId;

    /**
     * 微信UnionID
     */
    @Column(unique = true, length = 100)
    private String unionId;

    /**
     * 微信昵称
     */
    @Column(length = 100)
    private String nickname;

    /**
     * 微信头像
     */
    @Column(length = 500)
    private String avatar;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 国家
     */
    @Column(length = 50)
    private String country;

    /**
     * 省份
     */
    @Column(length = 50)
    private String province;

    /**
     * 城市
     */
    @Column(length = 50)
    private String city;

    /**
     * 语言
     */
    @Column(length = 20)
    private String language;

    /**
     * 是否关注公众号
     */
    private Boolean subscribe;

    /**
     * 关注时间
     */
    private LocalDateTime subscribeTime;

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

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}
