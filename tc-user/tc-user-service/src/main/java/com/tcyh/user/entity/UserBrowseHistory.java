package com.tcyh.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户浏览历史表
 * 
 * @author fp
 * @since 2025-09-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user_browse_history")
public class UserBrowseHistory implements Serializable {

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
     * 内容类型:1-笔记 2-商品 3-用户
     */
    @TableField("content_type")
    private Integer contentType;

    /**
     * 浏览时长(秒)
     */
    @TableField("browse_duration")
    private Integer browseDuration;

    /**
     * 浏览时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
