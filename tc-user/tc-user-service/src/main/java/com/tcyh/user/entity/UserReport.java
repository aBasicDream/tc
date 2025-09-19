package com.tcyh.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户举报表
 * 
 * @author fp
 * @since 2025-09-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user_report")
public class UserReport implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 举报人ID
     */
    @TableField("reporter_id")
    private Long reporterId;

    /**
     * 被举报用户ID
     */
    @TableField("reported_user_id")
    private Long reportedUserId;

    /**
     * 被举报内容ID
     */
    @TableField("content_id")
    private Long contentId;

    /**
     * 内容类型:1-笔记 2-评论 3-用户
     */
    @TableField("content_type")
    private Integer contentType;

    /**
     * 举报类型:1-垃圾信息 2-色情内容 3-暴力内容 4-侵权 5-其他
     */
    @TableField("report_type")
    private Integer reportType;

    /**
     * 举报原因
     */
    @TableField("report_reason")
    private String reportReason;

    /**
     * 处理状态:0-待处理 1-已处理 2-已驳回
     */
    @TableField("status")
    private Integer status;

    /**
     * 举报时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
