package com.tcyh.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户基础信息表
 * 
 * @author fp
 * @since 2025-09-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user_info")
public class UserInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户名/手机号
     */
    @TableField("username")
    private String username;

    /**
     * 密码(加密)
     */
    @TableField("password")
    private String password;

    /**
     * 加密盐值
     */
    @TableField("salt")
    private String salt;

    /**
     * 昵称
     */
    @TableField("nickname")
    private String nickname;

    /**
     * 头像URL
     */
    @TableField("avatar")
    private String avatar;

    /**
     * 性别:0-未知 1-男 2-女
     */
    @TableField("gender")
    private Integer gender;

    /**
     * 生日
     */
    @TableField("birthday")
    private LocalDate birthday;

    /**
     * 手机号
     */
    @TableField("phone")
    private String phone;

    /**
     * 邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 个人简介
     */
    @TableField("bio")
    private String bio;

    /**
     * 所在地
     */
    @TableField("location")
    private String location;

    /**
     * 状态:0-禁用 1-正常 2-待审核
     */
    @TableField("status")
    private Integer status;

    /**
     * 是否认证:0-未认证 1-已认证
     */
    @TableField("is_verified")
    private Integer isVerified;

    /**
     * 认证类型:1-个人 2-企业 3-达人
     */
    @TableField("verified_type")
    private Integer verifiedType;

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
