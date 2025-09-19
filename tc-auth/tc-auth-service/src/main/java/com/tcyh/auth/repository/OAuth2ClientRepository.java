package com.tcyh.auth.repository;

import com.tcyh.auth.entity.OAuth2Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * OAuth2客户端Repository接口
 * 
 * @author fp
 * @since 2025-09-18
 */
@Repository
public interface OAuth2ClientRepository extends JpaRepository<OAuth2Client, Long> {

    /**
     * 根据客户端ID查找客户端
     */
    Optional<OAuth2Client> findByClientId(String clientId);

    /**
     * 检查客户端ID是否存在
     */
    boolean existsByClientId(String clientId);

    /**
     * 根据客户端ID和是否启用查找客户端
     */
    Optional<OAuth2Client> findByClientIdAndEnabled(String clientId, Boolean enabled);
}
