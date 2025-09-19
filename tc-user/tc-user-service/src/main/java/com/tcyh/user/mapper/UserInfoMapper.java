package com.tcyh.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tcyh.user.entity.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户基础信息表 Mapper 接口
 * 
 * @author fp
 * @since 2025-09-17
 */
@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {

    /**
     * 根据用户名查询用户信息
     * 
     * @param username 用户名
     * @return 用户信息
     */
    UserInfo selectByUsername(@Param("username") String username);

    /**
     * 根据手机号查询用户信息
     * 
     * @param phone 手机号
     * @return 用户信息
     */
    UserInfo selectByPhone(@Param("phone") String phone);

    /**
     * 根据邮箱查询用户信息
     * 
     * @param email 邮箱
     * @return 用户信息
     */
    UserInfo selectByEmail(@Param("email") String email);
}
