package com.tcyh.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tcyh.user.entity.UserMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户私信表 Mapper 接口
 * 
 * @author fp
 * @since 2025-09-17
 */
@Mapper
public interface UserMessageMapper extends BaseMapper<UserMessage> {

    /**
     * 查询两个用户之间的私信记录
     * 
     * @param userId1 用户1ID
     * @param userId2 用户2ID
     * @param limit 限制条数
     * @return 私信记录列表
     */
    List<UserMessage> selectMessagesBetweenUsers(@Param("userId1") Long userId1, 
                                                 @Param("userId2") Long userId2, 
                                                 @Param("limit") Integer limit);

    /**
     * 查询用户的未读消息数量
     * 
     * @param userId 用户ID
     * @return 未读消息数量
     */
    Long countUnreadMessages(@Param("userId") Long userId);

    /**
     * 标记消息为已读
     * 
     * @param userId 用户ID
     * @param senderId 发送者ID
     * @return 影响行数
     */
    int markMessagesAsRead(@Param("userId") Long userId, @Param("senderId") Long senderId);
}
