package com.tcyh.auth.service;

import com.tcyh.auth.entity.AuthUser;
import com.tcyh.auth.entity.WechatUser;
import com.tcyh.auth.repository.AuthUserRepository;
import com.tcyh.auth.repository.WechatUserRepository;
import com.tcyh.common.constant.ErrorCodeEnum;
import com.tcyh.common.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

/**
 * 微信登录服务
 * 
 * @author fp
 * @since 2025-09-18
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WechatLoginService {

    private final AuthUserRepository authUserRepository;
    private final WechatUserRepository wechatUserRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${wechat.app-id:}")
    private String appId;

    @Value("${wechat.app-secret:}")
    private String appSecret;

    /**
     * 微信登录
     */
    @Transactional
    public AuthUser wechatLogin(String code, String deviceInfo) {
        log.info("Wechat login with code: {}", code);

        try {
            // 1. 通过code获取access_token
            String accessToken = getAccessToken(code);
            if (accessToken == null) {
                throw new BusinessException(ErrorCodeEnum.USER_LOGIN_FAIL, "微信授权失败");
            }

            // 2. 通过access_token获取用户信息
            Map<String, Object> userInfo = getWechatUserInfo(accessToken);
            if (userInfo == null) {
                throw new BusinessException(ErrorCodeEnum.USER_LOGIN_FAIL, "获取微信用户信息失败");
            }

            String openId = (String) userInfo.get("openid");
            String unionId = (String) userInfo.get("unionid");
            String nickname = (String) userInfo.get("nickname");
            String avatar = (String) userInfo.get("headimgurl");

            // 3. 查找或创建用户
            AuthUser user = findOrCreateWechatUser(openId, unionId, nickname, avatar, deviceInfo);

            log.info("Wechat login successful for user: {}", user.getUsername());
            return user;

        } catch (Exception e) {
            log.error("Wechat login failed: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCodeEnum.USER_LOGIN_FAIL, "微信登录失败");
        }
    }

    /**
     * 通过code获取access_token
     */
    private String getAccessToken(String code) {
        String url = String.format(
                "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code",
                appId, appSecret, code
        );

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            Map<String, Object> result = response.getBody();
            
            if (result != null && result.containsKey("access_token")) {
                return (String) result.get("access_token");
            }
        } catch (Exception e) {
            log.error("Failed to get wechat access token: {}", e.getMessage());
        }
        
        return null;
    }

    /**
     * 通过access_token获取用户信息
     */
    private Map<String, Object> getWechatUserInfo(String accessToken) {
        String url = String.format(
                "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s",
                accessToken, "openid"
        );

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("Failed to get wechat user info: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 查找或创建微信用户
     */
    private AuthUser findOrCreateWechatUser(String openId, String unionId, String nickname, String avatar, String deviceInfo) {
        // 先通过openId查找
        Optional<WechatUser> wechatUserOpt = wechatUserRepository.findByOpenId(openId);
        
        if (wechatUserOpt.isPresent()) {
            // 用户已存在，更新信息
            WechatUser wechatUser = wechatUserOpt.get();
            wechatUser.setNickname(nickname);
            wechatUser.setAvatar(avatar);
            wechatUserRepository.save(wechatUser);
            
            return authUserRepository.findById(wechatUser.getUserId())
                    .orElseThrow(() -> new BusinessException(ErrorCodeEnum.SYSTEM_ERROR, "用户不存在"));
        }

        // 用户不存在，创建新用户
        AuthUser user = new AuthUser();
        user.setUsername("wx_" + openId);
        user.setPassword(""); // 微信用户不需要密码
        user.setNickname(nickname);
        user.setAvatar(avatar);
        user.setWechatOpenId(openId);
        user.setWechatUnionId(unionId);
        user.setWechatNickname(nickname);
        user.setWechatAvatar(avatar);
        user.setLoginType(AuthUser.LoginType.WECHAT);
        user.setDeviceInfo(deviceInfo);
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);

        AuthUser savedUser = authUserRepository.save(user);

        // 创建微信用户记录
        WechatUser wechatUser = new WechatUser();
        wechatUser.setUserId(savedUser.getId());
        wechatUser.setOpenId(openId);
        wechatUser.setUnionId(unionId);
        wechatUser.setNickname(nickname);
        wechatUser.setAvatar(avatar);
        wechatUserRepository.save(wechatUser);

        return savedUser;
    }

    /**
     * 绑定微信账号
     */
    @Transactional
    public void bindWechatAccount(Long userId, String code) {
        log.info("Binding wechat account for user: {}", userId);

        try {
            String accessToken = getAccessToken(code);
            if (accessToken == null) {
                throw new BusinessException(ErrorCodeEnum.SYSTEM_ERROR, "微信授权失败");
            }

            Map<String, Object> userInfo = getWechatUserInfo(accessToken);
            if (userInfo == null) {
                throw new BusinessException(ErrorCodeEnum.SYSTEM_ERROR, "获取微信用户信息失败");
            }

            String openId = (String) userInfo.get("openid");
            String unionId = (String) userInfo.get("unionid");

            // 检查是否已被其他用户绑定
            if (wechatUserRepository.existsByOpenId(openId)) {
                throw new BusinessException(ErrorCodeEnum.SYSTEM_ERROR, "该微信账号已被其他用户绑定");
            }

            // 绑定微信账号
            AuthUser user = authUserRepository.findById(userId)
                    .orElseThrow(() -> new BusinessException(ErrorCodeEnum.SYSTEM_ERROR, "用户不存在"));

            user.setWechatOpenId(openId);
            user.setWechatUnionId(unionId);
            authUserRepository.save(user);

            // 创建微信用户记录
            WechatUser wechatUser = new WechatUser();
            wechatUser.setUserId(userId);
            wechatUser.setOpenId(openId);
            wechatUser.setUnionId(unionId);
            wechatUserRepository.save(wechatUser);

            log.info("Wechat account bound successfully for user: {}", userId);

        } catch (Exception e) {
            log.error("Failed to bind wechat account: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCodeEnum.SYSTEM_ERROR, "绑定微信账号失败");
        }
    }
}
