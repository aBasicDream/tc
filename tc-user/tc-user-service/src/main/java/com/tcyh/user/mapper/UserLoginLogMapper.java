package com.tcyh.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tcyh.user.entity.UserLoginLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户登录日志表 Mapper 接口
 * 
 * @author fp
 * @since 2025-09-17
 */
@Mapper
public interface UserLoginLogMapper extends BaseMapper<UserLoginLog> {

    /**
     * 查询用户最近的登录记录
     * 
     * @param userId 用户ID
     * @param limit 限制条数
     * @return 登录记录列表
     */
    List<UserLoginLog> selectRecentLoginLogs(@Param("userId") Long userId, @Param("limit") Integer limit);

    /**
     * 查询用户今日登录次数
     * 
     * @param userId 用户ID
     * @return 今日登录次数
     */
    Long countTodayLoginTimes(@Param("userId") Long userId);

    /**
     * 查询用户指定IP的登录次数
     * 
     * @param userId 用户ID
     * @param loginIp 登录IP
     * @return 登录次数
     */
    Long countLoginTimesByIp(@Param("userId") Long userId, @Param("loginIp") String loginIp);
}
