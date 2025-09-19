package com.tcyh.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tcyh.user.entity.UserFollow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户关注关系表 Mapper 接口
 * 
 * @author fp
 * @since 2025-09-17
 */
@Mapper
public interface UserFollowMapper extends BaseMapper<UserFollow> {

    /**
     * 查询用户的关注列表
     * 
     * @param userId 用户ID
     * @return 关注列表
     */
    List<UserFollow> selectFollowListByUserId(@Param("userId") Long userId);

    /**
     * 查询用户的粉丝列表
     * 
     * @param userId 用户ID
     * @return 粉丝列表
     */
    List<UserFollow> selectFansListByUserId(@Param("userId") Long userId);

    /**
     * 查询两个用户之间的关注关系
     * 
     * @param userId 用户ID
     * @param followedUserId 被关注用户ID
     * @return 关注关系
     */
    UserFollow selectFollowRelation(@Param("userId") Long userId, @Param("followedUserId") Long followedUserId);

    /**
     * 统计用户关注数量
     * 
     * @param userId 用户ID
     * @return 关注数量
     */
    Long countFollowByUserId(@Param("userId") Long userId);

    /**
     * 统计用户粉丝数量
     * 
     * @param userId 用户ID
     * @return 粉丝数量
     */
    Long countFansByUserId(@Param("userId") Long userId);
}
