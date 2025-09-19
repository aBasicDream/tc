package com.tcyh.auth.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * OAuth2客户端实体类
 * 
 * @author fp
 * @since 2025-09-18
 */
@Data
@Entity
@Table(name = "oauth2_client")
@EqualsAndHashCode(callSuper = false)
public class OAuth2Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 客户端ID
     */
    @Column(unique = true, nullable = false, length = 100)
    private String clientId;

    /**
     * 客户端密钥
     */
    @Column(nullable = false)
    private String clientSecret;

    /**
     * 客户端名称
     */
    @Column(nullable = false, length = 100)
    private String clientName;

    /**
     * 客户端描述
     */
    @Column(length = 500)
    private String description;

    /**
     * 重定向URI
     */
    @ElementCollection
    @CollectionTable(name = "oauth2_client_redirect_uri", joinColumns = @JoinColumn(name = "client_id"))
    @Column(name = "redirect_uri")
    private Set<String> redirectUris;

    /**
     * 授权范围
     */
    @ElementCollection
    @CollectionTable(name = "oauth2_client_scope", joinColumns = @JoinColumn(name = "client_id"))
    @Column(name = "scope")
    private Set<String> scopes;

    /**
     * 授权类型
     */
    @ElementCollection
    @CollectionTable(name = "oauth2_client_grant_type", joinColumns = @JoinColumn(name = "client_id"))
    @Column(name = "grant_type")
    private Set<String> grantTypes;

    /**
     * 是否启用
     */
    @Column(nullable = false)
    private Boolean enabled = true;

    /**
     * 访问令牌有效期（秒）
     */
    @Column(nullable = false)
    private Integer accessTokenValiditySeconds = 3600;

    /**
     * 刷新令牌有效期（秒）
     */
    @Column(nullable = false)
    private Integer refreshTokenValiditySeconds = 86400;

    /**
     * 是否需要授权确认
     */
    @Column(nullable = false)
    private Boolean requireAuthorizationConsent = true;

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
