package com.tcyh.auth.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * OAuth2客户端请求DTO
 * 
 * @author fp
 * @since 2025-09-18
 */
@Data
public class OAuth2ClientRequest {

    /**
     * 客户端ID
     */
    private String clientId;

    /**
     * 客户端密钥
     */
    private String clientSecret;

    /**
     * 客户端名称
     */
    private String clientName;

    /**
     * 客户端描述
     */
    private String description;

    /**
     * 重定向URI
     */
    private Set<String> redirectUris;

    /**
     * 授权范围
     */
    private Set<String> scopes;

    /**
     * 授权类型
     */
    private Set<String> grantTypes;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 访问令牌有效期（秒）
     */
    private Integer accessTokenValiditySeconds;

    /**
     * 刷新令牌有效期（秒）
     */
    private Integer refreshTokenValiditySeconds;

    /**
     * 是否需要授权确认
     */
    private Boolean requireAuthorizationConsent;
}
