package com.tcyh.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户评论表
 * 
 * @author fp
 * @since 2025-09-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user_comment")
public class UserComment implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 评论用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 内容ID
     */
    @TableField("content_id")
    private Long contentId;

    /**
     * 内容类型:1-笔记 2-商品
     */
    @TableField("content_type")
    private Integer contentType;

    /**
     * 父评论ID(0表示顶级评论)
     */
    @TableField("parent_id")
    private Long parentId;

    /**
     * 评论内容
     */
    @TableField("content")
    private String content;

    /**
     * 点赞数
     */
    @TableField("like_count")
    private Integer likeCount;

    /**
     * 回复数
     */
    @TableField("reply_count")
    private Integer replyCount;

    /**
     * 状态:0-删除 1-正常 2-审核中
     */
    @TableField("status")
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
