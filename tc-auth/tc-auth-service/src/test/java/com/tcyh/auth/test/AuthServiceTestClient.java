package com.tcyh.auth.test;

import com.tcyh.auth.dto.*;
import com.tcyh.auth.entity.AuthUser;
import com.tcyh.auth.service.AuthUserService;
import com.tcyh.auth.service.SmsCodeService;
import com.tcyh.auth.service.WechatLoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 认证服务测试客户端
 * 用于测试各种登录方式
 * 
 * @author fp
 * @since 2025-09-18
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthServiceTestClient implements CommandLineRunner {

    private final AuthUserService authUserService;
    private final SmsCodeService smsCodeService;
    private final WechatLoginService wechatLoginService;

    @Override
    public void run(String... args) throws Exception {
        log.info("开始测试认证服务...");
        
        // 测试用户注册
        testUserRegistration();
        
        // 测试短信验证码
        testSmsCode();
        
        // 测试手机号登录
        testPhoneLogin();
        
        log.info("认证服务测试完成！");
    }

    /**
     * 测试用户注册
     */
    private void testUserRegistration() {
        log.info("测试用户注册...");
        
        try {
            AuthUser user = new AuthUser();
            user.setUsername("testuser");
            user.setPassword("password123");
            user.setEmail("test@example.com");
            user.setPhone("13800138000");
            user.setNickname("测试用户");
            
            AuthUser savedUser = authUserService.createUser(user);
            log.info("用户注册成功: {}", savedUser.getUsername());
            
        } catch (Exception e) {
            log.error("用户注册失败: {}", e.getMessage());
        }
    }

    /**
     * 测试短信验证码
     */
    private void testSmsCode() {
        log.info("测试短信验证码...");
        
        try {
            String phone = "13800138000";
            
            // 发送验证码
            smsCodeService.sendSmsCode(phone, SmsCode.CodeType.LOGIN, "Test Device");
            log.info("验证码发送成功");
            
            // 验证验证码（这里使用固定验证码进行测试）
            boolean isValid = smsCodeService.isValidCode(phone, "123456", SmsCode.CodeType.LOGIN);
            log.info("验证码验证结果: {}", isValid);
            
        } catch (Exception e) {
            log.error("短信验证码测试失败: {}", e.getMessage());
        }
    }

    /**
     * 测试手机号登录
     */
    private void testPhoneLogin() {
        log.info("测试手机号登录...");
        
        try {
            String phone = "13800138000";
            
            // 查找用户
            var userOpt = authUserService.findByPhone(phone);
            if (userOpt.isPresent()) {
                AuthUser user = userOpt.get();
                log.info("找到用户: {}", user.getUsername());
                
                // 更新登录时间
                authUserService.updateLastLoginTime(user.getUsername());
                log.info("登录时间更新成功");
            } else {
                log.info("用户不存在，需要先注册");
            }
            
        } catch (Exception e) {
            log.error("手机号登录测试失败: {}", e.getMessage());
        }
    }
}
