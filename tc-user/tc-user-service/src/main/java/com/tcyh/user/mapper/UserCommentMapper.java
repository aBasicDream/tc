package com.tcyh.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tcyh.user.entity.UserComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户评论表 Mapper 接口
 * 
 * @author fp
 * @since 2025-09-17
 */
@Mapper
public interface UserCommentMapper extends BaseMapper<UserComment> {

    /**
     * 查询内容的评论列表
     * 
     * @param contentId 内容ID
     * @param contentType 内容类型
     * @return 评论列表
     */
    List<UserComment> selectCommentsByContent(@Param("contentId") Long contentId, @Param("contentType") Integer contentType);

    /**
     * 查询评论的回复列表
     * 
     * @param parentId 父评论ID
     * @return 回复列表
     */
    List<UserComment> selectRepliesByParentId(@Param("parentId") Long parentId);

    /**
     * 统计内容的评论数量
     * 
     * @param contentId 内容ID
     * @param contentType 内容类型
     * @return 评论数量
     */
    Long countCommentsByContent(@Param("contentId") Long contentId, @Param("contentType") Integer contentType);
}
