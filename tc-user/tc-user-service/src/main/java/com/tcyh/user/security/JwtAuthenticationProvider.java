package com.tcyh.user.security;

import com.tcyh.user.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * JWT认证提供者
 * 
 * @author fp
 * @since 2025-09-17
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            String token = (String) authentication.getCredentials();
            
            if (!StringUtils.hasText(token)) {
                throw new BadCredentialsException("Token不能为空");
            }
            
            String username = jwtUtil.getUsernameFromToken(token);
            if (!StringUtils.hasText(username)) {
                throw new BadCredentialsException("无效的Token");
            }
            
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            
            if (!jwtUtil.validateToken(token, username)) {
                throw new BadCredentialsException("Token验证失败");
            }
            
            return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            
        } catch (Exception e) {
            log.error("JWT认证失败: {}", e.getMessage());
            throw new BadCredentialsException("JWT认证失败", e);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
