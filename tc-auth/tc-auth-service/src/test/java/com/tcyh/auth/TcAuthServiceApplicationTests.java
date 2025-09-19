package com.tcyh.auth;

import com.tcyh.auth.entity.AuthUser;
import com.tcyh.auth.repository.AuthUserRepository;
import com.tcyh.auth.service.AuthUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * OAuth2认证服务测试类
 * 
 * @author fp
 * @since 2025-09-18
 */
@SpringBootTest
@ActiveProfiles("test")
public class TcAuthServiceApplicationTests {

    @Autowired
    private AuthUserService authUserService;

    @Autowired
    private AuthUserRepository authUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void contextLoads() {
        assertNotNull(authUserService);
        assertNotNull(authUserRepository);
        assertNotNull(passwordEncoder);
    }

    @Test
    public void testPasswordEncoder() {
        String rawPassword = "test123";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        
        assertNotNull(encodedPassword);
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
        assertFalse(passwordEncoder.matches("wrong", encodedPassword));
    }

    @Test
    public void testUserCreation() {
        // 创建测试用户
        AuthUser user = new AuthUser();
        user.setUsername("testuser");
        user.setPassword("test123");
        user.setEmail("test@example.com");
        user.setNickname("测试用户");
        
        // 保存用户
        AuthUser savedUser = authUserRepository.save(user);
        
        assertNotNull(savedUser.getId());
        assertEquals("testuser", savedUser.getUsername());
        assertEquals("test@example.com", savedUser.getEmail());
        assertTrue(savedUser.getEnabled());
        
        // 清理测试数据
        authUserRepository.delete(savedUser);
    }

    @Test
    public void testUserService() {
        // 测试用户服务
        AuthUser user = new AuthUser();
        user.setUsername("serviceuser");
        user.setPassword("service123");
        user.setEmail("service@example.com");
        
        try {
            AuthUser savedUser = authUserService.createUser(user);
            assertNotNull(savedUser.getId());
            assertEquals("serviceuser", savedUser.getUsername());
            
            // 测试用户查找
            assertTrue(authUserService.existsByUsername("serviceuser"));
            assertTrue(authUserService.existsByEmail("service@example.com"));
            
            // 清理测试数据
            authUserRepository.delete(savedUser);
        } catch (Exception e) {
            fail("User creation failed: " + e.getMessage());
        }
    }
}
