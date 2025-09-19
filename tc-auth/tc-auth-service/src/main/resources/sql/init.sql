-- OAuth2认证服务数据库初始化脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS tc_auth CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE tc_auth;

-- 用户表
CREATE TABLE IF NOT EXISTS auth_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    email VARCHAR(100) UNIQUE COMMENT '邮箱',
    phone VARCHAR(20) UNIQUE COMMENT '手机号',
    nickname VARCHAR(50) COMMENT '昵称',
    avatar VARCHAR(500) COMMENT '头像URL',
    enabled BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否启用',
    account_non_expired BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否未过期',
    account_non_locked BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否未锁定',
    credentials_non_expired BOOLEAN NOT NULL DEFAULT TRUE COMMENT '凭证是否未过期',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    last_login_time DATETIME COMMENT '最后登录时间',
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_phone (phone),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 用户角色表
CREATE TABLE IF NOT EXISTS auth_user_role (
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role VARCHAR(20) NOT NULL COMMENT '角色',
    PRIMARY KEY (user_id, role),
    FOREIGN KEY (user_id) REFERENCES auth_user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色表';

-- OAuth2客户端表
CREATE TABLE IF NOT EXISTS oauth2_client (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '客户端ID',
    client_id VARCHAR(100) NOT NULL UNIQUE COMMENT '客户端ID',
    client_secret VARCHAR(255) NOT NULL COMMENT '客户端密钥',
    client_name VARCHAR(100) NOT NULL COMMENT '客户端名称',
    description VARCHAR(500) COMMENT '客户端描述',
    enabled BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否启用',
    access_token_validity_seconds INT NOT NULL DEFAULT 3600 COMMENT '访问令牌有效期（秒）',
    refresh_token_validity_seconds INT NOT NULL DEFAULT 86400 COMMENT '刷新令牌有效期（秒）',
    require_authorization_consent BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否需要授权确认',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_client_id (client_id),
    INDEX idx_enabled (enabled)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='OAuth2客户端表';

-- OAuth2客户端重定向URI表
CREATE TABLE IF NOT EXISTS oauth2_client_redirect_uri (
    client_id BIGINT NOT NULL COMMENT '客户端ID',
    redirect_uri VARCHAR(500) NOT NULL COMMENT '重定向URI',
    PRIMARY KEY (client_id, redirect_uri),
    FOREIGN KEY (client_id) REFERENCES oauth2_client(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='OAuth2客户端重定向URI表';

-- OAuth2客户端授权范围表
CREATE TABLE IF NOT EXISTS oauth2_client_scope (
    client_id BIGINT NOT NULL COMMENT '客户端ID',
    scope VARCHAR(100) NOT NULL COMMENT '授权范围',
    PRIMARY KEY (client_id, scope),
    FOREIGN KEY (client_id) REFERENCES oauth2_client(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='OAuth2客户端授权范围表';

-- OAuth2客户端授权类型表
CREATE TABLE IF NOT EXISTS oauth2_client_grant_type (
    client_id BIGINT NOT NULL COMMENT '客户端ID',
    grant_type VARCHAR(50) NOT NULL COMMENT '授权类型',
    PRIMARY KEY (client_id, grant_type),
    FOREIGN KEY (client_id) REFERENCES oauth2_client(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='OAuth2客户端授权类型表';

-- 插入默认管理员用户
INSERT IGNORE INTO auth_user (username, password, email, nickname, enabled) 
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyVqjJqJqJqJqJqJqJqJqJqJqJq', 'admin@example.com', '系统管理员', TRUE);

-- 插入默认用户角色
INSERT IGNORE INTO auth_user_role (user_id, role) 
VALUES (1, 'ADMIN'), (1, 'USER');

-- 插入默认OAuth2客户端
INSERT IGNORE INTO oauth2_client (client_id, client_secret, client_name, description, enabled) 
VALUES ('tc-client', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyVqjJqJqJqJqJqJqJqJqJqJqJq', 'TC客户端', '默认TC系统客户端', TRUE);

-- 插入默认重定向URI
INSERT IGNORE INTO oauth2_client_redirect_uri (client_id, redirect_uri) 
VALUES (1, 'http://127.0.0.1:8080/login/oauth2/code/tc-client'), (1, 'http://127.0.0.1:8080/authorized');

-- 插入默认授权范围
INSERT IGNORE INTO oauth2_client_scope (client_id, scope) 
VALUES (1, 'openid'), (1, 'profile'), (1, 'read'), (1, 'write');

-- 用户表（添加新字段）
ALTER TABLE auth_user ADD COLUMN wechat_open_id VARCHAR(100) UNIQUE COMMENT '微信OpenID';
ALTER TABLE auth_user ADD COLUMN wechat_union_id VARCHAR(100) UNIQUE COMMENT '微信UnionID';
ALTER TABLE auth_user ADD COLUMN wechat_nickname VARCHAR(100) COMMENT '微信昵称';
ALTER TABLE auth_user ADD COLUMN wechat_avatar VARCHAR(500) COMMENT '微信头像';
ALTER TABLE auth_user ADD COLUMN login_type VARCHAR(20) NOT NULL DEFAULT 'PASSWORD' COMMENT '登录类型';
ALTER TABLE auth_user ADD COLUMN last_login_ip VARCHAR(50) COMMENT '最后登录IP';
ALTER TABLE auth_user ADD COLUMN device_info VARCHAR(200) COMMENT '登录设备信息';

-- 短信验证码表
CREATE TABLE IF NOT EXISTS sms_code (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    phone VARCHAR(20) NOT NULL COMMENT '手机号',
    code VARCHAR(10) NOT NULL COMMENT '验证码',
    code_type VARCHAR(20) NOT NULL COMMENT '验证码类型',
    expire_time DATETIME NOT NULL COMMENT '过期时间',
    used BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否已使用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    used_time DATETIME COMMENT '使用时间',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    device_info VARCHAR(200) COMMENT '设备信息',
    INDEX idx_phone_type (phone, code_type),
    INDEX idx_expire_time (expire_time),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='短信验证码表';

-- 微信用户表
CREATE TABLE IF NOT EXISTS wechat_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    user_id BIGINT NOT NULL COMMENT '关联的用户ID',
    open_id VARCHAR(100) NOT NULL UNIQUE COMMENT '微信OpenID',
    union_id VARCHAR(100) UNIQUE COMMENT '微信UnionID',
    nickname VARCHAR(100) COMMENT '微信昵称',
    avatar VARCHAR(500) COMMENT '微信头像',
    gender INT COMMENT '性别',
    country VARCHAR(50) COMMENT '国家',
    province VARCHAR(50) COMMENT '省份',
    city VARCHAR(50) COMMENT '城市',
    language VARCHAR(20) COMMENT '语言',
    subscribe BOOLEAN COMMENT '是否关注公众号',
    subscribe_time DATETIME COMMENT '关注时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_open_id (open_id),
    INDEX idx_union_id (union_id),
    FOREIGN KEY (user_id) REFERENCES auth_user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='微信用户表';
