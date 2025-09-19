package com.tcyh.auth.service;

import com.tcyh.auth.entity.AuthUser;
import com.tcyh.auth.repository.AuthUserRepository;
import com.tcyh.common.constant.ErrorCodeEnum;
import com.tcyh.common.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 用户认证服务
 * 
 * @author fp
 * @since 2025-09-18
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthUserService implements UserDetailsService {

    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user by username: {}", username);
        
        Optional<AuthUser> user = authUserRepository.findByUsernameOrEmail(username);
        if (user.isEmpty()) {
            log.warn("User not found with username: {}", username);
            throw new UsernameNotFoundException("User not found: " + username);
        }
        
        AuthUser authUser = user.get();
        if (!authUser.isEnabled()) {
            log.warn("User account is disabled: {}", username);
            throw new UsernameNotFoundException("User account is disabled: " + username);
        }
        
        log.debug("User loaded successfully: {}", username);
        return authUser;
    }

    /**
     * 根据用户名查找用户
     */
    public Optional<AuthUser> findByUsername(String username) {
        return authUserRepository.findByUsername(username);
    }

    /**
     * 根据邮箱查找用户
     */
    public Optional<AuthUser> findByEmail(String email) {
        return authUserRepository.findByEmail(email);
    }

    /**
     * 根据手机号查找用户
     */
    public Optional<AuthUser> findByPhone(String phone) {
        return authUserRepository.findByPhone(phone);
    }

    /**
     * 创建用户
     */
    @Transactional
    public AuthUser createUser(AuthUser user) {
        log.info("Creating new user: {}", user.getUsername());
        
        // 检查用户名是否已存在
        if (authUserRepository.existsByUsername(user.getUsername())) {
            log.warn("Username already exists: {}", user.getUsername());
            throw new BusinessException(ErrorCodeEnum.USER_REGISTER_FAIL, "用户名已存在");
        }
        
        // 检查邮箱是否已存在
        if (user.getEmail() != null && authUserRepository.existsByEmail(user.getEmail())) {
            log.warn("Email already exists: {}", user.getEmail());
            throw new BusinessException(ErrorCodeEnum.USER_REGISTER_FAIL, "邮箱已存在");
        }
        
        // 检查手机号是否已存在
        if (user.getPhone() != null && authUserRepository.existsByPhone(user.getPhone())) {
            log.warn("Phone already exists: {}", user.getPhone());
            throw new BusinessException(ErrorCodeEnum.USER_REGISTER_FAIL, "手机号已存在");
        }
        
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // 设置默认值
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        
        AuthUser savedUser = authUserRepository.save(user);
        log.info("User created successfully: {}", savedUser.getUsername());
        
        return savedUser;
    }

    /**
     * 更新用户最后登录时间
     */
    @Transactional
    public void updateLastLoginTime(String username) {
        Optional<AuthUser> userOpt = authUserRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            AuthUser user = userOpt.get();
            user.setLastLoginTime(LocalDateTime.now());
            authUserRepository.save(user);
            log.debug("Updated last login time for user: {}", username);
        }
    }

    /**
     * 验证用户密码
     */
    public boolean validatePassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * 检查用户是否存在
     */
    public boolean existsByUsername(String username) {
        return authUserRepository.existsByUsername(username);
    }

    /**
     * 检查邮箱是否存在
     */
    public boolean existsByEmail(String email) {
        return authUserRepository.existsByEmail(email);
    }

    /**
     * 检查手机号是否存在
     */
    public boolean existsByPhone(String phone) {
        return authUserRepository.existsByPhone(phone);
    }
}
