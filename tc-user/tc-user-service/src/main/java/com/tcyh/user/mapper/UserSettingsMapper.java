package com.tcyh.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tcyh.user.entity.UserSettings;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户设置表 Mapper 接口
 * 
 * @author fp
 * @since 2025-09-17
 */
@Mapper
public interface UserSettingsMapper extends BaseMapper<UserSettings> {

    /**
     * 查询用户的所有设置
     * 
     * @param userId 用户ID
     * @return 设置列表
     */
    List<UserSettings> selectSettingsByUserId(@Param("userId") Long userId);

    /**
     * 查询用户指定键的设置值
     * 
     * @param userId 用户ID
     * @param settingKey 设置键
     * @return 设置记录
     */
    UserSettings selectSettingByUserAndKey(@Param("userId") Long userId, @Param("settingKey") String settingKey);
}
