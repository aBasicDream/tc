package com.tcyh.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tcyh.user.entity.UserInfo;

/**
 * 用户基础信息表 服务类
 * 
 * @author fp
 * @since 2025-09-17
 */
public interface UserInfoService extends IService<UserInfo> {

    /**
     * 根据用户名查询用户信息
     * 
     * @param username 用户名
     * @return 用户信息
     */
    UserInfo getByUsername(String username);

    /**
     * 根据手机号查询用户信息
     * 
     * @param phone 手机号
     * @return 用户信息
     */
    UserInfo getByPhone(String phone);

    /**
     * 根据邮箱查询用户信息
     * 
     * @param email 邮箱
     * @return 用户信息
     */
    UserInfo getByEmail(String email);

    /**
     * 创建用户
     * 
     * @param userInfo 用户信息
     * @return 是否成功
     */
    boolean createUser(UserInfo userInfo);

    /**
     * 更新用户信息
     * 
     * @param userInfo 用户信息
     * @return 是否成功
     */
    boolean updateUser(UserInfo userInfo);

    /**
     * 检查用户名是否存在
     * 
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 检查手机号是否存在
     * 
     * @param phone 手机号
     * @return 是否存在
     */
    boolean existsByPhone(String phone);

    /**
     * 检查邮箱是否存在
     * 
     * @param email 邮箱
     * @return 是否存在
     */
    boolean existsByEmail(String email);
}
