package com.tcyh.auth.service;

import com.tcyh.auth.entity.OAuth2Client;
import com.tcyh.auth.repository.OAuth2ClientRepository;
import com.tcyh.common.constant.ErrorCodeEnum;
import com.tcyh.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * OAuth2客户端服务
 * 
 * @author fp
 * @since 2025-09-18
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2ClientService {

    private final OAuth2ClientRepository oAuth2ClientRepository;

    /**
     * 根据客户端ID查找客户端
     */
    public Optional<OAuth2Client> findByClientId(String clientId) {
        return oAuth2ClientRepository.findByClientId(clientId);
    }

    /**
     * 根据客户端ID和是否启用查找客户端
     */
    public Optional<OAuth2Client> findByClientIdAndEnabled(String clientId, Boolean enabled) {
        return oAuth2ClientRepository.findByClientIdAndEnabled(clientId, enabled);
    }

    /**
     * 获取所有启用的客户端
     */
    public List<OAuth2Client> findAllEnabled() {
        return oAuth2ClientRepository.findAll().stream()
                .filter(OAuth2Client::getEnabled)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 创建OAuth2客户端
     */
    @Transactional
    public OAuth2Client createClient(OAuth2Client client) {
        log.info("Creating new OAuth2 client: {}", client.getClientId());
        
        // 检查客户端ID是否已存在
        if (oAuth2ClientRepository.existsByClientId(client.getClientId())) {
            log.warn("Client ID already exists: {}", client.getClientId());
            throw new BusinessException(ErrorCodeEnum.SYSTEM_ERROR, "客户端ID已存在");
        }
        
        // 设置默认值
        if (client.getEnabled() == null) {
            client.setEnabled(true);
        }
        if (client.getRequireAuthorizationConsent() == null) {
            client.setRequireAuthorizationConsent(true);
        }
        if (client.getAccessTokenValiditySeconds() == null) {
            client.setAccessTokenValiditySeconds(3600);
        }
        if (client.getRefreshTokenValiditySeconds() == null) {
            client.setRefreshTokenValiditySeconds(86400);
        }
        
        OAuth2Client savedClient = oAuth2ClientRepository.save(client);
        log.info("OAuth2 client created successfully: {}", savedClient.getClientId());
        
        return savedClient;
    }

    /**
     * 更新OAuth2客户端
     */
    @Transactional
    public OAuth2Client updateClient(OAuth2Client client) {
        log.info("Updating OAuth2 client: {}", client.getClientId());
        
        OAuth2Client existingClient = oAuth2ClientRepository.findByClientId(client.getClientId())
                .orElseThrow(() -> new BusinessException(ErrorCodeEnum.SYSTEM_ERROR, "客户端不存在"));
        
        // 更新字段
        existingClient.setClientName(client.getClientName());
        existingClient.setDescription(client.getDescription());
        existingClient.setRedirectUris(client.getRedirectUris());
        existingClient.setScopes(client.getScopes());
        existingClient.setGrantTypes(client.getGrantTypes());
        existingClient.setEnabled(client.getEnabled());
        existingClient.setAccessTokenValiditySeconds(client.getAccessTokenValiditySeconds());
        existingClient.setRefreshTokenValiditySeconds(client.getRefreshTokenValiditySeconds());
        existingClient.setRequireAuthorizationConsent(client.getRequireAuthorizationConsent());
        
        OAuth2Client updatedClient = oAuth2ClientRepository.save(existingClient);
        log.info("OAuth2 client updated successfully: {}", updatedClient.getClientId());
        
        return updatedClient;
    }

    /**
     * 删除OAuth2客户端
     */
    @Transactional
    public void deleteClient(String clientId) {
        log.info("Deleting OAuth2 client: {}", clientId);
        
        OAuth2Client client = oAuth2ClientRepository.findByClientId(clientId)
                .orElseThrow(() -> new BusinessException(ErrorCodeEnum.SYSTEM_ERROR, "客户端不存在"));
        
        oAuth2ClientRepository.delete(client);
        log.info("OAuth2 client deleted successfully: {}", clientId);
    }

    /**
     * 启用/禁用客户端
     */
    @Transactional
    public void toggleClientStatus(String clientId, Boolean enabled) {
        log.info("Toggling client status: {} -> {}", clientId, enabled);
        
        OAuth2Client client = oAuth2ClientRepository.findByClientId(clientId)
                .orElseThrow(() -> new BusinessException(ErrorCodeEnum.SYSTEM_ERROR, "客户端不存在"));
        
        client.setEnabled(enabled);
        oAuth2ClientRepository.save(client);
        log.info("Client status updated successfully: {} -> {}", clientId, enabled);
    }

    /**
     * 检查客户端ID是否存在
     */
    public boolean existsByClientId(String clientId) {
        return oAuth2ClientRepository.existsByClientId(clientId);
    }
}
