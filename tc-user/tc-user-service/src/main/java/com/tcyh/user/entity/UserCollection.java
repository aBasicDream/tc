package com.tcyh.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户收藏表
 * 
 * @author fp
 * @since 2025-09-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user_collection")
public class UserCollection implements Serializable {

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
     * 内容ID(笔记/商品)
     */
    @TableField("content_id")
    private Long contentId;

    /**
     * 内容类型:1-笔记 2-商品 3-用户
     */
    @TableField("content_type")
    private Integer contentType;

    /**
     * 收藏类型:1-默认 2-喜欢 3-稍后看
     */
    @TableField("collection_type")
    private Integer collectionType;

    /**
     * 收藏时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
