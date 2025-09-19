package com.tcyh.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户点赞表
 * 
 * @author fp
 * @since 2025-09-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user_like")
public class UserLike implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 内容ID
     */
    @TableField("content_id")
    private Long contentId;

    /**
     * 内容类型:1-笔记 2-评论 3-用户
     */
    @TableField("content_type")
    private Integer contentType;

    /**
     * 状态:0-取消点赞 1-已点赞
     */
    @TableField("status")
    private Integer status;

    /**
     * 点赞时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
