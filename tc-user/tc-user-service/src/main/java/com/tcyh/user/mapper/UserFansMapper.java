package com.tcyh.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tcyh.user.entity.UserFans;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户粉丝统计表 Mapper 接口
 * 
 * @author fp
 * @since 2025-09-17
 */
@Mapper
public interface UserFansMapper extends BaseMapper<UserFans> {

    /**
     * 根据用户ID查询粉丝统计信息
     * 
     * @param userId 用户ID
     * @return 粉丝统计信息
     */
    UserFans selectByUserId(@Param("userId") Long userId);

    /**
     * 更新用户粉丝统计
     * 
     * @param userId 用户ID
     * @param fansCount 粉丝数量
     * @param followCount 关注数量
     * @return 影响行数
     */
    int updateFansCount(@Param("userId") Long userId, 
                       @Param("fansCount") Integer fansCount, 
                       @Param("followCount") Integer followCount);
}
