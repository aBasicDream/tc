package com.tcyh.auth.controller;

import com.tcyh.auth.dto.OAuth2ClientRequest;
import com.tcyh.auth.entity.OAuth2Client;
import com.tcyh.auth.service.OAuth2ClientService;
import com.tcyh.common.constant.ErrorCodeEnum;
import com.tcyh.common.resp.RestResp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * OAuth2客户端管理控制器
 * 
 * @author fp
 * @since 2025-09-18
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/oauth2")
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasRole('ADMIN')")
public class OAuth2ClientController {

    private final OAuth2ClientService oAuth2ClientService;

    /**
     * 获取所有OAuth2客户端
     */
    @GetMapping("/clients")
    public RestResp<List<OAuth2Client>> getAllClients() {
        List<OAuth2Client> clients = oAuth2ClientService.findAllEnabled();
        return RestResp.ok(clients);
    }

    /**
     * 根据客户端ID获取客户端信息
     */
    @GetMapping("/clients/{clientId}")
    public RestResp<OAuth2Client> getClient(@PathVariable String clientId) {
        OAuth2Client client = oAuth2ClientService.findByClientId(clientId)
                .orElseThrow(() -> new RuntimeException("客户端不存在"));
        return RestResp.ok(client);
    }

    /**
     * 创建OAuth2客户端
     */
    @PostMapping("/clients")
    public RestResp<OAuth2Client> createClient( @RequestBody OAuth2ClientRequest request) {
        log.info("Creating OAuth2 client: {}", request.getClientId());
        
        try {
            OAuth2Client client = new OAuth2Client();
            client.setClientId(request.getClientId());
            client.setClientSecret(request.getClientSecret());
            client.setClientName(request.getClientName());
            client.setDescription(request.getDescription());
            client.setRedirectUris(request.getRedirectUris());
            client.setScopes(request.getScopes());
            client.setGrantTypes(request.getGrantTypes());
            client.setEnabled(request.getEnabled());
            client.setAccessTokenValiditySeconds(request.getAccessTokenValiditySeconds());
            client.setRefreshTokenValiditySeconds(request.getRefreshTokenValiditySeconds());
            client.setRequireAuthorizationConsent(request.getRequireAuthorizationConsent());
            
            OAuth2Client savedClient = oAuth2ClientService.createClient(client);
            
            log.info("OAuth2 client created successfully: {}", savedClient.getClientId());
            return RestResp.ok(savedClient);
            
        } catch (Exception e) {
            log.error("Failed to create OAuth2 client: {}", e.getMessage(), e);
            return RestResp.fail(null, ErrorCodeEnum.SYSTEM_ERROR);
        }
    }

    /**
     * 更新OAuth2客户端
     */
    @PutMapping("/clients/{clientId}")
    public RestResp<OAuth2Client> updateClient(@PathVariable String clientId, 
                                                @RequestBody OAuth2ClientRequest request) {
        log.info("Updating OAuth2 client: {}", clientId);
        
        try {
            OAuth2Client client = new OAuth2Client();
            client.setClientId(clientId);
            client.setClientSecret(request.getClientSecret());
            client.setClientName(request.getClientName());
            client.setDescription(request.getDescription());
            client.setRedirectUris(request.getRedirectUris());
            client.setScopes(request.getScopes());
            client.setGrantTypes(request.getGrantTypes());
            client.setEnabled(request.getEnabled());
            client.setAccessTokenValiditySeconds(request.getAccessTokenValiditySeconds());
            client.setRefreshTokenValiditySeconds(request.getRefreshTokenValiditySeconds());
            client.setRequireAuthorizationConsent(request.getRequireAuthorizationConsent());
            
            OAuth2Client updatedClient = oAuth2ClientService.updateClient(client);
            
            log.info("OAuth2 client updated successfully: {}", updatedClient.getClientId());
            return RestResp.ok(updatedClient);
            
        } catch (Exception e) {
            log.error("Failed to update OAuth2 client: {}", e.getMessage(), e);
            return RestResp.fail(null, ErrorCodeEnum.SYSTEM_ERROR);
        }
    }

    /**
     * 删除OAuth2客户端
     */
    @DeleteMapping("/clients/{clientId}")
    public RestResp<Void> deleteClient(@PathVariable String clientId) {
        log.info("Deleting OAuth2 client: {}", clientId);
        
        try {
            oAuth2ClientService.deleteClient(clientId);
            
            log.info("OAuth2 client deleted successfully: {}", clientId);
            return RestResp.ok(null);
            
        } catch (Exception e) {
            log.error("Failed to delete OAuth2 client: {}", e.getMessage(), e);
            return RestResp.fail(null, ErrorCodeEnum.SYSTEM_ERROR);
        }
    }

    /**
     * 启用/禁用OAuth2客户端
     */
    @PutMapping("/clients/{clientId}/status")
    public RestResp<Void> toggleClientStatus(@PathVariable String clientId, 
                                             @RequestParam Boolean enabled) {
        log.info("Toggling OAuth2 client status: {} -> {}", clientId, enabled);
        
        try {
            oAuth2ClientService.toggleClientStatus(clientId, enabled);
            
            log.info("OAuth2 client status updated successfully: {} -> {}", clientId, enabled);
            return RestResp.ok(null);
            
        } catch (Exception e) {
            log.error("Failed to toggle OAuth2 client status: {}", e.getMessage(), e);
            return RestResp.fail(null, ErrorCodeEnum.SYSTEM_ERROR);
        }
    }

    /**
     * 检查客户端ID是否存在
     */
    @GetMapping("/clients/check/{clientId}")
    public RestResp<Boolean> checkClientId(@PathVariable String clientId) {
        boolean exists = oAuth2ClientService.existsByClientId(clientId);
        return RestResp.ok(exists);
    }
}
