# TC用户服务系统

## 项目简介

这是一个基于Spring Boot + Spring Security + MyBatis Plus + Redis的分布式用户服务系统，支持大量用户并发登录，具备完整的用户管理功能。

## 技术栈

- **Spring Boot 3.0.5** - 主框架
- **Spring Security** - 安全框架
- **MyBatis Plus 3.5.3** - ORM框架
- **Redis** - 缓存和分布式锁
- **Redisson** - 分布式锁实现
- **JWT** - 无状态认证
- **MySQL 8.0** - 数据库
- **Swagger** - API文档

## 功能特性

### 核心功能
- ✅ 用户注册/登录
- ✅ JWT无状态认证
- ✅ 密码加密存储
- ✅ 登录日志记录
- ✅ 分布式锁防暴力破解
- ✅ Redis缓存优化
- ✅ 用户状态管理

### 用户管理
- ✅ 用户基础信息管理
- ✅ 用户关注/粉丝关系
- ✅ 用户标签管理
- ✅ 用户收藏功能
- ✅ 用户浏览历史
- ✅ 用户点赞功能
- ✅ 用户评论系统
- ✅ 用户私信功能
- ✅ 用户设置管理
- ✅ 用户举报功能

### 安全特性
- ✅ 密码SHA-256加密
- ✅ 随机盐值防彩虹表攻击
- ✅ JWT token认证
- ✅ 登录失败次数限制
- ✅ 账户锁定机制
- ✅ Token黑名单机制
- ✅ 分布式锁防并发攻击

## 快速开始

### 1. 环境要求

- JDK 17+
- MySQL 8.0+
- Redis 6.0+
- Maven 3.6+

### 2. 数据库初始化

执行 `doc/sql/user.sql` 文件创建数据库和表结构。

### 3. 配置修改

修改 `src/main/resources/application.yml` 中的数据库和Redis连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/tc_user?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: your_username
    password: your_password
  
  redis:
    host: localhost
    port: 6379
    password: your_redis_password
```

### 4. 启动应用

```bash
mvn clean install
mvn spring-boot:run
```

### 5. 访问服务

- 服务地址: http://localhost:8080/tc-user-service
- API文档: http://localhost:8080/tc-user-service/swagger-ui.html

## API接口

### 认证接口

#### 用户登录
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "13800138001",
  "password": "123456"
}
```

#### 用户注册
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "testuser",
  "password": "Test123456",
  "confirmPassword": "Test123456",
  "nickname": "测试用户",
  "phone": "13800138001",
  "email": "test@example.com"
}
```

#### 刷新Token
```http
POST /api/auth/refresh?refreshToken=your_refresh_token
```

#### 用户登出
```http
POST /api/auth/logout
Authorization: Bearer your_access_token
```

## 分布式系统支持

### 高并发登录处理

1. **分布式锁**: 使用Redisson实现分布式锁，防止同一用户并发登录
2. **登录限制**: 支持登录失败次数限制和账户锁定
3. **缓存优化**: 用户信息缓存，减少数据库查询
4. **Token管理**: JWT无状态认证，支持水平扩展

### 缓存策略

- **用户信息缓存**: 30分钟过期
- **登录失败计数**: 30分钟过期
- **Token黑名单**: 24小时过期
- **分布式锁**: 30分钟自动释放

### 安全机制

- **密码加密**: SHA-256 + 随机盐值
- **防暴力破解**: 登录失败5次锁定30分钟
- **Token黑名单**: 登出后Token立即失效
- **IP限制**: 支持IP级别的登录限制

## 数据库设计

### 核心表结构

- `user_info` - 用户基础信息表
- `user_follow` - 用户关注关系表
- `user_fans` - 用户粉丝统计表
- `user_login_log` - 用户登录日志表
- `user_tag` - 用户标签表
- `user_collection` - 用户收藏表
- `user_browse_history` - 用户浏览历史表
- `user_like` - 用户点赞表
- `user_comment` - 用户评论表
- `user_message` - 用户私信表
- `user_settings` - 用户设置表
- `user_report` - 用户举报表

## 性能优化

### 数据库优化
- 合理的索引设计
- 分页查询支持
- 连接池配置优化

### 缓存优化
- Redis缓存用户信息
- 分布式锁减少数据库压力
- 缓存预热和更新策略

### 并发处理
- 分布式锁防并发
- 异步处理登录日志
- 连接池和线程池优化

## 监控和运维

### 健康检查
- Spring Boot Actuator健康检查
- 数据库连接状态监控
- Redis连接状态监控

### 日志管理
- 结构化日志输出
- 登录日志记录
- 异常日志追踪

## 扩展功能

### 可扩展的模块
- 短信验证码登录
- 第三方登录（微信、QQ等）
- 用户权限管理
- 消息推送服务
- 数据统计分析

## 注意事项

1. **生产环境部署**:
   - 修改JWT密钥
   - 配置HTTPS
   - 设置合适的连接池大小
   - 配置Redis集群

2. **安全建议**:
   - 定期更新密码策略
   - 监控异常登录行为
   - 设置IP白名单
   - 定期清理过期数据

3. **性能调优**:
   - 根据实际负载调整缓存过期时间
   - 优化数据库查询
   - 监控系统资源使用情况

## 许可证

本项目采用MIT许可证。
