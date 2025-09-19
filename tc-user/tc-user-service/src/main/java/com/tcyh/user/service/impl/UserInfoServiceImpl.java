package com.tcyh.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcyh.user.entity.UserInfo;
import com.tcyh.user.mapper.UserInfoMapper;
import com.tcyh.user.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户基础信息表 服务实现类
 * 
 * @author fp
 * @since 2025-09-17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    private final UserInfoMapper userInfoMapper;

    @Override
    public UserInfo getByUsername(String username) {
        return userInfoMapper.selectByUsername(username);
    }

    @Override
    public UserInfo getByPhone(String phone) {
        return userInfoMapper.selectByPhone(phone);
    }

    @Override
    public UserInfo getByEmail(String email) {
        return userInfoMapper.selectByEmail(email);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createUser(UserInfo userInfo) {
        try {
            return save(userInfo);
        } catch (Exception e) {
            log.error("创建用户失败: {}", e.getMessage());
            throw new RuntimeException("创建用户失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUser(UserInfo userInfo) {
        try {
            return updateById(userInfo);
        } catch (Exception e) {
            log.error("更新用户失败: {}", e.getMessage());
            throw new RuntimeException("更新用户失败", e);
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInfo::getUsername, username);
        return count(wrapper) > 0;
    }

    @Override
    public boolean existsByPhone(String phone) {
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInfo::getPhone, phone);
        return count(wrapper) > 0;
    }

    @Override
    public boolean existsByEmail(String email) {
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInfo::getEmail, email);
        return count(wrapper) > 0;
    }
}
