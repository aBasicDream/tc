package com.tcyh.user.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 密码加密工具类
 * 
 * @author fp
 * @since 2025-09-17
 */
@Slf4j
@Component
public class PasswordUtil {

    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 8;

    /**
     * 生成随机盐值
     * 
     * @return 盐值
     */
    public String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * 加密密码
     * 
     * @param password 原始密码
     * @param salt 盐值
     * @return 加密后的密码
     */
    public String encryptPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt.getBytes());
            byte[] hashedPassword = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            log.error("密码加密失败: {}", e.getMessage());
            throw new RuntimeException("密码加密失败", e);
        }
    }

    /**
     * 验证密码
     * 
     * @param password 原始密码
     * @param salt 盐值
     * @param encryptedPassword 加密后的密码
     * @return 是否匹配
     */
    public boolean verifyPassword(String password, String salt, String encryptedPassword) {
        String encrypted = encryptPassword(password, salt);
        return encrypted.equals(encryptedPassword);
    }

    /**
     * 生成MD5密码（兼容旧系统）
     * 
     * @param password 原始密码
     * @return MD5密码
     */
    public String generateMD5Password(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error("MD5密码生成失败: {}", e.getMessage());
            throw new RuntimeException("MD5密码生成失败", e);
        }
    }
}
