package com.tcyh.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户登录日志表
 * 
 * @author fp
 * @since 2025-09-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user_login_log")
public class UserLoginLog implements Serializable {

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
     * 登录IP
     */
    @TableField("login_ip")
    private String loginIp;

    /**
     * 登录设备
     */
    @TableField("login_device")
    private String loginDevice;

    /**
     * 登录地点
     */
    @TableField("login_location")
    private String loginLocation;

    /**
     * 登录时间
     */
    @TableField(value = "login_time", fill = FieldFill.INSERT)
    private LocalDateTime loginTime;
}
