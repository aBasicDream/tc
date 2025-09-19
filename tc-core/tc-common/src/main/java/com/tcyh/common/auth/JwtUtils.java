package com.tcyh.common.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;

/**
 * JWT 工具类 - 支持RS256算法
 *
 * @author xiongxiaoyang
 * @date 2022/5/17
 */
@UtilityClass
@Slf4j
public class JwtUtils {

    /**
     * 定义系统标识头常量
     */
    private static final String HEADER_SYSTEM_KEY = "systemKeyHeader";

    /**
     * 根据用户ID生成JWT
     *
     * @param uid       用户ID
     * @param systemKey 系统标识
     * @return JWT
     */
    public String generateToken(Long uid, String systemKey) {
        try {
            PrivateKey privateKey = getPrivateKey();
            return Jwts.builder()
                .setHeaderParam(HEADER_SYSTEM_KEY, systemKey)
                .setSubject(uid.toString())
                .setIssuedAt(new Date())
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate JWT token", e);
        }
    }

    /**
     * 解析JWT返回用户ID
     *
     * @param token     JWT
     * @param systemKey 系统标识
     * @return 用户ID
     */
    public Long parseToken(String token, String systemKey) {
        Jws<Claims> claimsJws;
        try {
            PublicKey publicKey = getPublicKey();
            claimsJws = Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token);
            // OK, we can trust this JWT
            // 判断该 JWT 是否属于指定系统
            if (Objects.equals(claimsJws.getHeader().get(HEADER_SYSTEM_KEY), systemKey)) {
                return Long.parseLong(claimsJws.getBody().getSubject());
            }
        } catch (JwtException | IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.warn("JWT解析失败:{}", token);
        }
        return null;
    }

    /**
     * 获取私钥
     */
    private PrivateKey getPrivateKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        String privateKeyContent = new String(Files.readAllBytes(Paths.get("src/main/resources/jwt-private-key.pem")));
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
        String publicKeyContent = new String(Files.readAllBytes(Paths.get("src/main/resources/jwt-public-key.pem")));
        publicKeyContent = publicKeyContent.replaceAll("\\n", "")
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "");
        
        byte[] keyBytes = Base64.getDecoder().decode(publicKeyContent);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }

}
