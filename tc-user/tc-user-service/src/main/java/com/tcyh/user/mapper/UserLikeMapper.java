package com.tcyh.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tcyh.user.entity.UserLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户点赞表 Mapper 接口
 * 
 * @author fp
 * @since 2025-09-17
 */
@Mapper
public interface UserLikeMapper extends BaseMapper<UserLike> {

    /**
     * 查询用户对指定内容的点赞状态
     * 
     * @param userId 用户ID
     * @param contentId 内容ID
     * @param contentType 内容类型
     * @return 点赞记录
     */
    UserLike selectLikeByUserAndContent(@Param("userId") Long userId, 
                                       @Param("contentId") Long contentId, 
                                       @Param("contentType") Integer contentType);

    /**
     * 统计内容的点赞数量
     * 
     * @param contentId 内容ID
     * @param contentType 内容类型
     * @return 点赞数量
     */
    Long countLikesByContent(@Param("contentId") Long contentId, @Param("contentType") Integer contentType);
}
