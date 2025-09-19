package com.tcyh.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户私信表
 * 
 * @author fp
 * @since 2025-09-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user_message")
public class UserMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 发送者ID
     */
    @TableField("sender_id")
    private Long senderId;

    /**
     * 接收者ID
     */
    @TableField("receiver_id")
    private Long receiverId;

    /**
     * 消息内容
     */
    @TableField("content")
    private String content;

    /**
     * 消息类型:1-文本 2-图片 3-语音 4-视频
     */
    @TableField("message_type")
    private Integer messageType;

    /**
     * 是否已读:0-未读 1-已读
     */
    @TableField("is_read")
    private Integer isRead;

    /**
     * 发送时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 阅读时间
     */
    @TableField("read_time")
    private LocalDateTime readTime;
}
