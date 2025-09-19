package com.tcyh.user.service.impl;

import com.tcyh.common.constant.ErrorCodeEnum;
import com.tcyh.config.exception.BusinessException;
import com.tcyh.user.dto.LoginRequest;
import com.tcyh.user.dto.LoginResponse;
import com.tcyh.user.dto.RegisterRequest;
import com.tcyh.user.entity.UserInfo;
import com.tcyh.user.entity.UserLoginLog;
import com.tcyh.user.mapper.UserLoginLogMapper;
import com.tcyh.user.service.AuthService;
import com.tcyh.user.service.UserInfoService;
import com.tcyh.user.util.JwtUtil;
import com.tcyh.user.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务实现类 - 基于Redisson
 * 
 * @author fp
 * @since 2025-09-17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserInfoService userInfoService;
    private final UserLoginLogMapper userLoginLogMapper;
    private final JwtUtil jwtUtil;
    private final PasswordUtil passwordUtil;
    private final RedissonClient redissonClient;

    private static final String LOGIN_LOCK_PREFIX = "login:lock:";
    private static final String LOGIN_COUNT_PREFIX = "login:count:";
    private static final String BLACKLIST_PREFIX = "blacklist:";
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final int LOCK_TIME_MINUTES = 30;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginResponse login(LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        
        // 分布式锁防止暴力破解
        String lockKey = LOGIN_LOCK_PREFIX + username;
        RLock lock = redissonClient.getLock(lockKey);
        
        try {
            // 尝试获取锁，最多等待1秒，锁定30分钟
            if (lock.tryLock(1, LOCK_TIME_MINUTES, TimeUnit.MINUTES)) {
                try {
                    // 检查是否在黑名单中
                    if (isInBlacklist(username)) {
                        throw new BusinessException(ErrorCodeEnum.USER_LOCK_AUTH_FAIL);
                    }
                    
                    // 检查登录次数
                    if (isLoginAttemptsExceeded(username)) {
                        addToBlacklist(username);
                        throw new BusinessException(ErrorCodeEnum.USER_LOGIN_MORE_FAIL);
                    }
                    
                    // 查找用户
                    UserInfo userInfo = findUserByUsername(username);
                    if (userInfo == null) {
                        incrementLoginAttempts(username);
                        throw new BusinessException(ErrorCodeEnum.USER_PASSWORD_ERROR);
                    }
                    
                    // 验证密码
                    if (!passwordUtil.verifyPassword(password, userInfo.getSalt(), userInfo.getPassword())) {
                        incrementLoginAttempts(username);
                        throw new BusinessException(ErrorCodeEnum.USER_PASSWORD_ERROR);
                    }
                    
                    // 检查用户状态
                    if (userInfo.getStatus() != 1) {
                        throw new BusinessException(ErrorCodeEnum.USER_IS_ENABLE_FAIL);
                    }
                    
                    // 清除登录失败次数
                    clearLoginAttempts(username);
                    
                    // 生成token
                    String accessToken = jwtUtil.generateToken(userInfo.getId(), userInfo.getUsername());
                    String refreshToken = jwtUtil.generateToken(userInfo.getId(), userInfo.getUsername());
                    
                    // 记录登录日志
                    recordLoginLog(userInfo.getId(), loginRequest.getLoginIp(), 
                                 loginRequest.getLoginDevice(), loginRequest.getLoginLocation());
                    
                    // 构建响应
                    LoginResponse response = new LoginResponse();
                    response.setAccessToken(accessToken);
                    response.setRefreshToken(refreshToken);
                    response.setExpiresIn(86400L); // 24小时
                    response.setUserId(userInfo.getId());
                    response.setUsername(userInfo.getUsername());
                    response.setNickname(userInfo.getNickname());
                    response.setAvatar(userInfo.getAvatar());
                    response.setLoginTime(LocalDateTime.now());
                    
                    // 缓存用户信息
                    cacheUserInfo(userInfo);
                    
                    return response;
                    
                } finally {
                    lock.unlock();
                }
            } else {
                throw new RuntimeException("系统繁忙，请稍后再试");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("登录被中断");
        } catch (Exception e) {
            log.error("用户登录失败: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean register(RegisterRequest registerRequest) {
        // 验证密码一致性
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new RuntimeException("两次输入的密码不一致");
        }
        
        // 检查用户名是否已存在
        if (userInfoService.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查手机号是否已存在
        if (StringUtils.hasText(registerRequest.getPhone()) && 
            userInfoService.existsByPhone(registerRequest.getPhone())) {
            throw new RuntimeException("手机号已被注册");
        }
        
        // 检查邮箱是否已存在
        if (StringUtils.hasText(registerRequest.getEmail()) && 
            userInfoService.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("邮箱已被注册");
        }
        
        // 创建用户
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername(registerRequest.getUsername());
        userInfo.setNickname(StringUtils.hasText(registerRequest.getNickname()) ? 
                           registerRequest.getNickname() : registerRequest.getUsername());
        userInfo.setPhone(registerRequest.getPhone());
        userInfo.setEmail(registerRequest.getEmail());
        userInfo.setStatus(1); // 正常状态
        userInfo.setIsVerified(0); // 未认证
        
        // 生成盐值和加密密码
        String salt = passwordUtil.generateSalt();
        String encryptedPassword = passwordUtil.encryptPassword(registerRequest.getPassword(), salt);
        userInfo.setSalt(salt);
        userInfo.setPassword(encryptedPassword);
        
        return userInfoService.createUser(userInfo);
    }

    @Override
    public String refreshToken(String refreshToken) {
        try {
            String userName = jwtUtil.getUsernameFromToken(refreshToken);
            Long userId = jwtUtil.getUserIdFromToken(refreshToken);
            return jwtUtil.generateToken(userId, userName);
        } catch (Exception e) {
            log.error("刷新token失败: {}", e.getMessage());
            throw new RuntimeException("刷新token失败");
        }
    }

    @Override
    @CacheEvict(value = "user", allEntries = true)
    public boolean logout(String token) {
        try {
            // 将token加入黑名单
            String username = jwtUtil.getUsernameFromToken(token);
            if (StringUtils.hasText(username)) {
                RBucket<String> blacklistBucket = redissonClient.getBucket(BLACKLIST_PREFIX + token);
                blacklistBucket.set(username, 86400, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            log.error("用户登出失败: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean validateToken(String token) {
        try {
            // 检查是否在黑名单中
            RBucket<String> blacklistBucket = redissonClient.getBucket(BLACKLIST_PREFIX + token);
            if (blacklistBucket.isExists()) {
                return false;
            }
            
            String username = jwtUtil.getUsernameFromToken(token);
            return jwtUtil.validateToken(token, username);
        } catch (Exception e) {
            log.error("验证token失败: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public void recordLoginLog(Long userId, String loginIp, String loginDevice, String loginLocation) {
        try {
            UserLoginLog loginLog = new UserLoginLog();
            loginLog.setUserId(userId);
            loginLog.setLoginIp(loginIp);
            loginLog.setLoginDevice(loginDevice);
            loginLog.setLoginLocation(loginLocation);
            loginLog.setLoginTime(LocalDateTime.now());
            
            userLoginLogMapper.insert(loginLog);
        } catch (Exception e) {
            log.error("记录登录日志失败: {}", e.getMessage());
        }
    }

    /**
     * 根据用户名查找用户
     */
    private UserInfo findUserByUsername(String username) {
        UserInfo userInfo = userInfoService.getByUsername(username);
        if (userInfo == null) {
            userInfo = userInfoService.getByPhone(username);
        }
        if (userInfo == null) {
            userInfo = userInfoService.getByEmail(username);
        }
        return userInfo;
    }

    /**
     * 检查是否在黑名单中
     */
    private boolean isInBlacklist(String username) {
        RBucket<String> blacklistBucket = redissonClient.getBucket(BLACKLIST_PREFIX + username);
        return blacklistBucket.isExists();
    }

    /**
     * 检查登录尝试次数是否超限
     */
    private boolean isLoginAttemptsExceeded(String username) {
        String countKey = LOGIN_COUNT_PREFIX + username;
        RAtomicLong counter = redissonClient.getAtomicLong(countKey);
        return counter.get() >= MAX_LOGIN_ATTEMPTS;
    }

    /**
     * 增加登录尝试次数
     */
    private void incrementLoginAttempts(String username) {
        String countKey = LOGIN_COUNT_PREFIX + username;
        RAtomicLong counter = redissonClient.getAtomicLong(countKey);
        counter.incrementAndGet();
        counter.expire(LOCK_TIME_MINUTES, TimeUnit.MINUTES);
    }

    /**
     * 清除登录尝试次数
     */
    private void clearLoginAttempts(String username) {
        String countKey = LOGIN_COUNT_PREFIX + username;
        RAtomicLong counter = redissonClient.getAtomicLong(countKey);
        counter.delete();
    }

    /**
     * 添加到黑名单
     */
    private void addToBlacklist(String username) {
        RBucket<String> blacklistBucket = redissonClient.getBucket(BLACKLIST_PREFIX + username);
        blacklistBucket.set(username, LOCK_TIME_MINUTES, TimeUnit.MINUTES);
    }

    /**
     * 缓存用户信息
     */
    private void cacheUserInfo(UserInfo userInfo) {
        String cacheKey = "user:info:" + userInfo.getId();
        RBucket<UserInfo> userBucket = redissonClient.getBucket(cacheKey);
        userBucket.set(userInfo, 3600, TimeUnit.SECONDS);
    }
}