package com.tcyh.gateway.util;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

/**
 * JWT工具类 - 支持RS256算法
 * 
 * @author fp
 * @since 2025-09-17
 */
@Slf4j
@Component
public class JwtUtil {

    /**
     * 从token中获取用户ID
     * 
     * @param token JWT token
     * @return 用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims != null ? Long.valueOf(claims.get("userId").toString()) : null;
    }

    /**
     * 从token中获取用户名
     * 
     * @param token JWT token
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims != null ? claims.getSubject() : null;
    }

    /**
     * 从token中获取过期时间
     * 
     * @param token JWT token
     * @return 过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims != null ? claims.getExpiration() : null;
    }

    /**
     * 从token中获取声明
     * 
     * @param token JWT token
     * @return 声明
     */
    private Claims getClaimsFromToken(String token) {
        try {
            PublicKey publicKey = getPublicKey();
            return Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException | IllegalArgumentException | NoSuchAlgorithmException | InvalidKeySpecException | IOException e) {
            log.error("JWT token解析失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 验证token是否过期
     * 
     * @param token JWT token
     * @return 是否过期
     */
    public Boolean isTokenExpired(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            return expiration != null && expiration.before(new Date());
        } catch (Exception e) {
            log.error("验证token过期时间失败: {}", e.getMessage());
            return true;
        }
    }

    /**
     * 验证token
     * 
     * @param token JWT token
     * @param username 用户名
     * @return 是否有效
     */
    public Boolean validateToken(String token, String username) {
        try {
            String tokenUsername = getUsernameFromToken(token);
            return (username.equals(tokenUsername) && !isTokenExpired(token));
        } catch (Exception e) {
            log.error("验证token失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 验证token是否有效（不验证用户名）
     * 
     * @param token JWT token
     * @return 是否有效
     */
    public Boolean validateToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims != null && !isTokenExpired(token);
        } catch (Exception e) {
            log.error("验证token失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 获取公钥
     */
    private PublicKey getPublicKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        ClassPathResource resource = new ClassPathResource("jwt-public-key.pem");
        String publicKeyContent = new String(Files.readAllBytes(resource.getFile().toPath()));
        publicKeyContent = publicKeyContent.replaceAll("\\n", "")
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "");
        
        byte[] keyBytes = Base64.getDecoder().decode(publicKeyContent);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }
}
