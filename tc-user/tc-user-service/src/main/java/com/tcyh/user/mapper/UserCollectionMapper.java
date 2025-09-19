package com.tcyh.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tcyh.user.entity.UserCollection;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户收藏表 Mapper 接口
 * 
 * @author fp
 * @since 2025-09-17
 */
@Mapper
public interface UserCollectionMapper extends BaseMapper<UserCollection> {

    /**
     * 查询用户的收藏列表
     * 
     * @param userId 用户ID
     * @param contentType 内容类型
     * @return 收藏列表
     */
    List<UserCollection> selectCollectionsByUserId(@Param("userId") Long userId, @Param("contentType") Integer contentType);

    /**
     * 查询用户是否收藏了指定内容
     * 
     * @param userId 用户ID
     * @param contentId 内容ID
     * @param contentType 内容类型
     * @return 收藏记录
     */
    UserCollection selectCollectionByUserAndContent(@Param("userId") Long userId, 
                                                   @Param("contentId") Long contentId, 
                                                   @Param("contentType") Integer contentType);
}
