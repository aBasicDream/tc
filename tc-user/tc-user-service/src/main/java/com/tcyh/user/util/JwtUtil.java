package com.tcyh.user.util;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类 - 支持RS256算法
 * 
 * @author fp
 * @since 2025-09-17
 */
@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.expiration:86400}")
    private Long expiration;

    /**
     * 生成JWT token
     * 
     * @param userId 用户ID
     * @param username 用户名
     * @return JWT token
     */
    public String generateToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        return createToken(claims, username);
    }

    /**
     * 创建token
     * 
     * @param claims 声明
     * @param subject 主题
     * @return token
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration * 1000);

        try {
            PrivateKey privateKey = getPrivateKey();

            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(subject)
                    .setIssuedAt(now)
                    .setExpiration(expiryDate)
                    .signWith(privateKey, SignatureAlgorithm.RS256)
                    .compact();
        } catch (Exception e) {
            log.error("Failed to create JWT token", e);
            throw new RuntimeException("Failed to create JWT token", e);
        }
    }

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
        } catch (JwtException | IllegalArgumentException | IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
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
     * 获取私钥
     */
    private PrivateKey getPrivateKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        ClassPathResource resource = new ClassPathResource("jwt-private-key.pem");
        String privateKeyContent = new String(Files.readAllBytes(resource.getFile().toPath()));
        privateKeyContent = privateKeyContent.replaceAll("\\n", "")
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "");
        
        byte[] keyBytes = Base64.getDecoder().decode(privateKeyContent);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(spec);
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