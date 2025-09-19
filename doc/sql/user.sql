-- =============================================
-- 类似小红书的用户模块表结构设计
-- =============================================

CREATE DATABASE IF NOT EXISTS `tc_user` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `tc_user`;

SET NAMES utf8mb4;

-- ----------------------------
-- Table structure for user_info (用户基础信息表)
-- ----------------------------
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info` (
    `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` varchar(50) NOT NULL COMMENT '用户名/手机号',
    `password` varchar(100) NOT NULL COMMENT '密码(加密)',
    `salt` varchar(8) NOT NULL COMMENT '加密盐值',
    `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
    `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
    `gender` tinyint(3) unsigned DEFAULT NULL COMMENT '性别:0-未知 1-男 2-女',
    `birthday` date DEFAULT NULL COMMENT '生日',
    `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
    `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
    `bio` varchar(500) DEFAULT NULL COMMENT '个人简介',
    `location` varchar(100) DEFAULT NULL COMMENT '所在地',
    `status` tinyint(3) unsigned NOT NULL DEFAULT '1' COMMENT '状态:0-禁用 1-正常 2-待审核',
    `is_verified` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '是否认证:0-未认证 1-已认证',
    `verified_type` tinyint(3) unsigned DEFAULT NULL COMMENT '认证类型:1-个人 2-企业 3-达人',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_phone` (`phone`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户基础信息表';

-- ----------------------------
-- Table structure for user_follow (用户关注关系表)
-- ----------------------------
DROP TABLE IF EXISTS `user_follow`;
CREATE TABLE `user_follow` (
    `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` bigint(20) unsigned NOT NULL COMMENT '关注者ID',
    `followed_user_id` bigint(20) unsigned NOT NULL COMMENT '被关注者ID',
    `status` tinyint(3) unsigned NOT NULL DEFAULT '1' COMMENT '状态:0-取消关注 1-已关注',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '关注时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_followed` (`user_id`, `followed_user_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_followed_user_id` (`followed_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户关注关系表';

-- ----------------------------
-- Table structure for user_fans (用户粉丝统计表)
-- ----------------------------
DROP TABLE IF EXISTS `user_fans`;
CREATE TABLE `user_fans` (
    `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` bigint(20) unsigned NOT NULL COMMENT '用户ID',
    `fans_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '粉丝数量',
    `follow_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '关注数量',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户粉丝统计表';

-- ----------------------------
-- Table structure for user_tag (用户标签表)
-- ----------------------------
DROP TABLE IF EXISTS `user_tag`;
CREATE TABLE `user_tag` (
    `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` bigint(20) unsigned NOT NULL COMMENT '用户ID',
    `tag_name` varchar(50) NOT NULL COMMENT '标签名称',
    `tag_type` tinyint(3) unsigned NOT NULL DEFAULT '1' COMMENT '标签类型:1-兴趣 2-职业 3-生活',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_tag_name` (`tag_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户标签表';

-- ----------------------------
-- Table structure for user_collection (用户收藏表)
-- ----------------------------
DROP TABLE IF EXISTS `user_collection`;
CREATE TABLE `user_collection` (
    `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` bigint(20) unsigned NOT NULL COMMENT '用户ID',
    `content_id` bigint(20) unsigned NOT NULL COMMENT '内容ID(笔记/商品)',
    `content_type` tinyint(3) unsigned NOT NULL COMMENT '内容类型:1-笔记 2-商品 3-用户',
    `collection_type` tinyint(3) unsigned NOT NULL DEFAULT '1' COMMENT '收藏类型:1-默认 2-喜欢 3-稍后看',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_content` (`user_id`, `content_id`, `content_type`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_content_id` (`content_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户收藏表';

-- ----------------------------
-- Table structure for user_browse_history (用户浏览历史表)
-- ----------------------------
DROP TABLE IF EXISTS `user_browse_history`;
CREATE TABLE `user_browse_history` (
    `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` bigint(20) unsigned NOT NULL COMMENT '用户ID',
    `content_id` bigint(20) unsigned NOT NULL COMMENT '内容ID',
    `content_type` tinyint(3) unsigned NOT NULL COMMENT '内容类型:1-笔记 2-商品 3-用户',
    `browse_duration` int(10) unsigned DEFAULT '0' COMMENT '浏览时长(秒)',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '浏览时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_content_id` (`content_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户浏览历史表';

-- ----------------------------
-- Table structure for user_like (用户点赞表)
-- ----------------------------
DROP TABLE IF EXISTS `user_like`;
CREATE TABLE `user_like` (
    `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` bigint(20) unsigned NOT NULL COMMENT '用户ID',
    `content_id` bigint(20) unsigned NOT NULL COMMENT '内容ID',
    `content_type` tinyint(3) unsigned NOT NULL COMMENT '内容类型:1-笔记 2-评论 3-用户',
    `status` tinyint(3) unsigned NOT NULL DEFAULT '1' COMMENT '状态:0-取消点赞 1-已点赞',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_content` (`user_id`, `content_id`, `content_type`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_content_id` (`content_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户点赞表';

-- ----------------------------
-- Table structure for user_comment (用户评论表)
-- ----------------------------
DROP TABLE IF EXISTS `user_comment`;
CREATE TABLE `user_comment` (
    `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` bigint(20) unsigned NOT NULL COMMENT '评论用户ID',
    `content_id` bigint(20) unsigned NOT NULL COMMENT '内容ID',
    `content_type` tinyint(3) unsigned NOT NULL COMMENT '内容类型:1-笔记 2-商品',
    `parent_id` bigint(20) unsigned DEFAULT '0' COMMENT '父评论ID(0表示顶级评论)',
    `content` text NOT NULL COMMENT '评论内容',
    `like_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '点赞数',
    `reply_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '回复数',
    `status` tinyint(3) unsigned NOT NULL DEFAULT '1' COMMENT '状态:0-删除 1-正常 2-审核中',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_content_id` (`content_id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户评论表';

-- ----------------------------
-- Table structure for user_message (用户私信表)
-- ----------------------------
DROP TABLE IF EXISTS `user_message`;
CREATE TABLE `user_message` (
    `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `sender_id` bigint(20) unsigned NOT NULL COMMENT '发送者ID',
    `receiver_id` bigint(20) unsigned NOT NULL COMMENT '接收者ID',
    `content` text NOT NULL COMMENT '消息内容',
    `message_type` tinyint(3) unsigned NOT NULL DEFAULT '1' COMMENT '消息类型:1-文本 2-图片 3-语音 4-视频',
    `is_read` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '是否已读:0-未读 1-已读',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
    `read_time` datetime DEFAULT NULL COMMENT '阅读时间',
    PRIMARY KEY (`id`),
    KEY `idx_sender_id` (`sender_id`),
    KEY `idx_receiver_id` (`receiver_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户私信表';

-- ----------------------------
-- Table structure for user_settings (用户设置表)
-- ----------------------------
DROP TABLE IF EXISTS `user_settings`;
CREATE TABLE `user_settings` (
    `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` bigint(20) unsigned NOT NULL COMMENT '用户ID',
    `setting_key` varchar(50) NOT NULL COMMENT '设置键',
    `setting_value` text COMMENT '设置值',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_key` (`user_id`, `setting_key`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户设置表';

-- ----------------------------
-- Table structure for user_login_log (用户登录日志表)
-- ----------------------------
DROP TABLE IF EXISTS `user_login_log`;
CREATE TABLE `user_login_log` (
    `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` bigint(20) unsigned NOT NULL COMMENT '用户ID',
    `login_ip` varchar(50) DEFAULT NULL COMMENT '登录IP',
    `login_device` varchar(100) DEFAULT NULL COMMENT '登录设备',
    `login_location` varchar(100) DEFAULT NULL COMMENT '登录地点',
    `login_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_login_time` (`login_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户登录日志表';

-- ----------------------------
-- Table structure for user_report (用户举报表)
-- ----------------------------
DROP TABLE IF EXISTS `user_report`;
CREATE TABLE `user_report` (
    `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `reporter_id` bigint(20) unsigned NOT NULL COMMENT '举报人ID',
    `reported_user_id` bigint(20) unsigned DEFAULT NULL COMMENT '被举报用户ID',
    `content_id` bigint(20) unsigned DEFAULT NULL COMMENT '被举报内容ID',
    `content_type` tinyint(3) unsigned DEFAULT NULL COMMENT '内容类型:1-笔记 2-评论 3-用户',
    `report_type` tinyint(3) unsigned NOT NULL COMMENT '举报类型:1-垃圾信息 2-色情内容 3-暴力内容 4-侵权 5-其他',
    `report_reason` varchar(500) DEFAULT NULL COMMENT '举报原因',
    `status` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '处理状态:0-待处理 1-已处理 2-已驳回',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '举报时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_reporter_id` (`reporter_id`),
    KEY `idx_reported_user_id` (`reported_user_id`),
    KEY `idx_content_id` (`content_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户举报表';

-- =============================================
-- 插入示例数据
-- =============================================

-- 插入示例用户数据
INSERT INTO `user_info` (`username`, `password`, `salt`, `nickname`, `avatar`, `gender`, `phone`, `bio`, `location`, `status`, `is_verified`) VALUES
('13800138001', 'e10adc3949ba59abbe56e057f20f883e', 'abc123', '小红书用户1', 'https://example.com/avatar1.jpg', 2, '13800138001', '热爱生活的小仙女', '北京', 1, 1),
('13800138002', 'e10adc3949ba59abbe56e057f20f883e', 'def456', '小红书用户2', 'https://example.com/avatar2.jpg', 1, '13800138002', '美食达人', '上海', 1, 0),
('13800138003', 'e10adc3949ba59abbe56e057f20f883e', 'ghi789', '小红书用户3', 'https://example.com/avatar3.jpg', 2, '13800138003', '旅行爱好者', '广州', 1, 1);

-- 插入示例关注关系
INSERT INTO `user_follow` (`user_id`, `followed_user_id`, `status`) VALUES
(1, 2, 1),
(1, 3, 1),
(2, 1, 1),
(3, 1, 1);

-- 插入示例粉丝统计
INSERT INTO `user_fans` (`user_id`, `fans_count`, `follow_count`) VALUES
(1, 2, 2),
(2, 1, 1),
(3, 1, 1);

-- 插入示例用户标签
INSERT INTO `user_tag` (`user_id`, `tag_name`, `tag_type`) VALUES
(1, '美妆', 1),
(1, '时尚', 1),
(2, '美食', 1),
(2, '烹饪', 2),
(3, '旅行', 1),
(3, '摄影', 1);

-- 插入示例用户设置
INSERT INTO `user_settings` (`user_id`, `setting_key`, `setting_value`) VALUES
(1, 'privacy_level', '1'),
(1, 'notification_enabled', 'true'),
(2, 'privacy_level', '2'),
(2, 'notification_enabled', 'false'),
(3, 'privacy_level', '1'),
(3, 'notification_enabled', 'true');

-- =============================================
-- 创建视图
-- =============================================

-- 用户详细信息视图
CREATE VIEW `v_user_detail` AS
SELECT
    u.id,
    u.username,
    u.nickname,
    u.avatar,
    u.gender,
    u.birthday,
    u.phone,
    u.email,
    u.bio,
    u.location,
    u.status,
    u.is_verified,
    u.verified_type,
    f.fans_count,
    f.follow_count,
    u.create_time,
    u.update_time
FROM user_info u
LEFT JOIN user_fans f ON u.id = f.user_id;

-- 用户关注列表视图
CREATE VIEW `v_user_follow_list` AS
SELECT
    f.id,
    f.user_id,
    f.followed_user_id,
    u.nickname as followed_nickname,
    u.avatar as followed_avatar,
    u.bio as followed_bio,
    f.create_time as follow_time
FROM user_follow f
JOIN user_info u ON f.followed_user_id = u.id
WHERE f.status = 1;

-- =============================================
-- 创建存储过程
-- =============================================

DELIMITER //

-- 更新用户粉丝统计的存储过程
CREATE PROCEDURE UpdateUserFansCount(IN user_id BIGINT)
BEGIN
    DECLARE fans_count INT DEFAULT 0;
    DECLARE follow_count INT DEFAULT 0;

    -- 计算粉丝数
    SELECT COUNT(*) INTO fans_count
    FROM user_follow
    WHERE followed_user_id = user_id AND status = 1;

    -- 计算关注数
    SELECT COUNT(*) INTO follow_count
    FROM user_follow
    WHERE user_id = user_id AND status = 1;

    -- 更新或插入粉丝统计
    INSERT INTO user_fans (user_id, fans_count, follow_count)
    VALUES (user_id, fans_count, follow_count)
    ON DUPLICATE KEY UPDATE
        fans_count = fans_count,
        follow_count = follow_count,
        update_time = CURRENT_TIMESTAMP;
END //

DELIMITER ;

-- =============================================
-- 创建触发器
-- =============================================

-- 关注关系变更时自动更新粉丝统计
DELIMITER //
CREATE TRIGGER tr_user_follow_after_insert
AFTER INSERT ON user_follow
FOR EACH ROW
BEGIN
    CALL UpdateUserFansCount(NEW.user_id);
    CALL UpdateUserFansCount(NEW.followed_user_id);
END //

CREATE TRIGGER tr_user_follow_after_update
AFTER UPDATE ON user_follow
FOR EACH ROW
BEGIN
    CALL UpdateUserFansCount(NEW.user_id);
    CALL UpdateUserFansCount(NEW.followed_user_id);
    IF OLD.user_id != NEW.user_id OR OLD.followed_user_id != NEW.followed_user_id THEN
        CALL UpdateUserFansCount(OLD.user_id);
        CALL UpdateUserFansCount(OLD.followed_user_id);
    END IF;
END //

DELIMITER ;
