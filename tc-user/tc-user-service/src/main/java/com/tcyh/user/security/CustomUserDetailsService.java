package com.tcyh.user.security;

import com.tcyh.user.entity.UserInfo;
import com.tcyh.user.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * 用户详情服务
 * 
 * @author fp
 * @since 2025-09-17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserInfoService userInfoService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            UserInfo userInfo = userInfoService.getByUsername(username);
            if (userInfo == null) {
                throw new UsernameNotFoundException("用户不存在: " + username);
            }

            // 检查用户状态
            if (userInfo.getStatus() == 0) {
                throw new UsernameNotFoundException("用户已被禁用: " + username);
            }

            return User.builder()
                    .username(userInfo.getUsername())
                    .password(userInfo.getPassword())
                    .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                    .accountExpired(false)
                    .accountLocked(false)
                    .credentialsExpired(false)
                    .disabled(userInfo.getStatus() != 1)
                    .build();

        } catch (Exception e) {
            log.error("加载用户详情失败: {}", e.getMessage());
            throw new UsernameNotFoundException("加载用户详情失败: " + username, e);
        }
    }
}
