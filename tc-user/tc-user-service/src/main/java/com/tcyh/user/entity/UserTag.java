package com.tcyh.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户标签表
 * 
 * @author fp
 * @since 2025-09-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user_tag")
public class UserTag implements Serializable {

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
     * 标签名称
     */
    @TableField("tag_name")
    private String tagName;

    /**
     * 标签类型:1-兴趣 2-职业 3-生活
     */
    @TableField("tag_type")
    private Integer tagType;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
