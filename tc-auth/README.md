# TC OAuth2认证服务模块

## 概述

TC OAuth2认证服务模块是基于Spring Security OAuth2 Authorization Server实现的用户认证和授权服务。该模块提供了完整的OAuth2认证流程，包括用户注册、登录、JWT令牌管理等功能。

## 功能特性

- ✅ **OAuth2授权服务器** - 支持Authorization Code、Refresh Token、Client Credentials等授权类型
- ✅ **JWT令牌管理** - 使用RSA256算法生成和验证JWT令牌
- ✅ **用户管理** - 用户注册、登录、信息管理
- ✅ **客户端管理** - OAuth2客户端注册和管理
- ✅ **角色权限** - 基于角色的访问控制
- ✅ **安全配置** - 密码加密、会话管理、CSRF防护
- ✅ **日志记录** - 完整的认证日志记录

## 技术栈

- **Spring Boot 3.0.5** - 应用框架
- **Spring Security OAuth2** - 认证授权框架
- **Spring Data JPA** - 数据访问层
- **MySQL** - 数据库
- **JWT** - 令牌管理
- **Logback** - 日志框架

## 项目结构

```
tc-auth/
├── tc-auth-service/                 # OAuth2认证服务
│   ├── src/main/java/com/tcyh/auth/
│   │   ├── config/                 # 配置类
│   │   │   ├── OAuth2AuthorizationServerConfig.java
│   │   │   └── OAuth2ResourceServerConfig.java
│   │   ├── controller/             # 控制器
│   │   │   ├── AuthController.java
│   │   │   └── OAuth2ClientController.java
│   │   ├── dto/                    # 数据传输对象
│   │   │   ├── UserRegisterRequest.java
│   │   │   ├── UserLoginRequest.java
│   │   │   ├── UserInfoResponse.java
│   │   │   └── OAuth2ClientRequest.java
│   │   ├── entity/                 # 实体类
│   │   │   ├── AuthUser.java
│   │   │   └── OAuth2Client.java
│   │   ├── repository/             # 数据访问层
│   │   │   ├── AuthUserRepository.java
│   │   │   └── OAuth2ClientRepository.java
│   │   ├── service/                # 服务层
│   │   │   ├── AuthUserService.java
│   │   │   └── OAuth2ClientService.java
│   │   └── TcAuthServiceApplication.java
│   ├── src/main/resources/
│   │   ├── application.yml         # 应用配置
│   │   ├── logback-spring.xml      # 日志配置
│   │   └── sql/                    # 数据库脚本
│   │       └── init.sql
│   └── pom.xml
├── pom.xml                         # 父级POM
└── README.md
```

## 快速开始

### 1. 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+

### 2. 数据库配置

1. 创建数据库：
```sql
CREATE DATABASE tc_auth CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 执行初始化脚本：
```bash
mysql -u root -p tc_auth < tc-auth/tc-auth-service/src/main/resources/sql/init.sql
```

### 3. 应用配置

修改 `application.yml` 中的数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/tc_auth?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
```

### 4. 启动应用

```bash
# 编译项目
mvn clean compile

# 启动认证服务
cd tc-auth/tc-auth-service
mvn spring-boot:run
```

服务将在 `http://localhost:9000/auth` 启动。

## API接口

### 认证接口

#### 用户注册
```http
POST /auth/api/auth/register
Content-Type: application/json

{
    "username": "testuser",
    "password": "password123",
    "confirmPassword": "password123",
    "email": "test@example.com",
    "phone": "13800138000",
    "nickname": "测试用户"
}
```

#### 用户登录
```http
POST /auth/api/auth/login
Content-Type: application/json

{
    "username": "testuser",
    "password": "password123",
    "rememberMe": false
}
```

#### 获取当前用户信息
```http
GET /auth/api/auth/me
Authorization: Bearer <access_token>
```

#### 用户登出
```http
POST /auth/api/auth/logout
Authorization: Bearer <access_token>
```

### OAuth2客户端管理接口

#### 获取所有客户端
```http
GET /auth/api/admin/oauth2/clients
Authorization: Bearer <admin_access_token>
```

#### 创建客户端
```http
POST /auth/api/admin/oauth2/clients
Content-Type: application/json
Authorization: Bearer <admin_access_token>

{
    "clientId": "my-client",
    "clientSecret": "my-secret",
    "clientName": "我的客户端",
    "description": "客户端描述",
    "redirectUris": ["http://localhost:8080/callback"],
    "scopes": ["read", "write"],
    "grantTypes": ["authorization_code", "refresh_token"],
    "enabled": true
}
```

## OAuth2授权流程

### 1. Authorization Code流程

```http
# 1. 重定向到授权服务器
GET /auth/oauth2/authorize?response_type=code&client_id=tc-client&redirect_uri=http://127.0.0.1:8080/callback&scope=openid profile read write

# 2. 用户登录后重定向到客户端
GET http://127.0.0.1:8080/callback?code=authorization_code

# 3. 客户端使用授权码换取访问令牌
POST /auth/oauth2/token
Content-Type: application/x-www-form-urlencoded

grant_type=authorization_code&code=authorization_code&redirect_uri=http://127.0.0.1:8080/callback&client_id=tc-client&client_secret=tc-secret
```

### 2. Client Credentials流程

```http
POST /auth/oauth2/token
Content-Type: application/x-www-form-urlencoded

grant_type=client_credentials&client_id=tc-client&client_secret=tc-secret&scope=read write
```

### 3. 刷新令牌

```http
POST /auth/oauth2/token
Content-Type: application/x-www-form-urlencoded

grant_type=refresh_token&refresh_token=refresh_token&client_id=tc-client&client_secret=tc-secret
```

## 配置说明

### 授权服务器配置

- **发行者URI**: `http://localhost:9000/auth`
- **JWT签名算法**: RS256
- **访问令牌有效期**: 1小时
- **刷新令牌有效期**: 1天

### 默认客户端配置

- **客户端ID**: `tc-client`
- **客户端密钥**: `tc-secret`
- **授权类型**: `authorization_code`, `refresh_token`, `client_credentials`
- **授权范围**: `openid`, `profile`, `read`, `write`
- **重定向URI**: `http://127.0.0.1:8080/login/oauth2/code/tc-client`

### 默认用户

- **用户名**: `admin`
- **密码**: `admin123`
- **角色**: `ADMIN`, `USER`

## 安全特性

### 密码安全
- 使用BCrypt算法加密密码
- 密码强度验证（6-20个字符）

### 令牌安全
- JWT令牌使用RS256算法签名
- 访问令牌和刷新令牌分离
- 令牌有效期控制

### 会话安全
- CSRF防护
- 会话超时控制
- 安全的Cookie配置

## 日志配置

### 日志级别
- **开发环境**: DEBUG
- **测试环境**: INFO
- **生产环境**: WARN

### 日志文件
- **应用日志**: `logs/tc-auth-service.log`
- **错误日志**: `logs/tc-auth-service-error.log`
- **认证日志**: `logs/tc-auth-service-auth.log`

## 监控和健康检查

### Actuator端点
- **健康检查**: `http://localhost:9000/auth/actuator/health`
- **应用信息**: `http://localhost:9000/auth/actuator/info`
- **指标监控**: `http://localhost:9000/auth/actuator/metrics`

## 故障排除

### 常见问题

1. **数据库连接失败**
   - 检查数据库服务是否启动
   - 验证连接配置是否正确
   - 确认数据库用户权限

2. **JWT令牌验证失败**
   - 检查令牌是否过期
   - 验证签名密钥是否正确
   - 确认令牌格式是否正确

3. **OAuth2授权失败**
   - 检查客户端配置是否正确
   - 验证重定向URI是否匹配
   - 确认授权范围是否正确

### 调试模式

启用调试日志：
```yaml
logging:
  level:
    com.tcyh.auth: DEBUG
    org.springframework.security: DEBUG
    org.springframework.security.oauth2: DEBUG
```

## 扩展开发

### 添加新的授权类型
1. 在`OAuth2Client`实体中添加新的授权类型
2. 在`OAuth2AuthorizationServerConfig`中配置新的授权类型
3. 实现相应的授权逻辑

### 自定义用户属性
1. 在`AuthUser`实体中添加新字段
2. 更新数据库表结构
3. 修改相关的DTO和API接口

### 集成第三方认证
1. 实现`OAuth2UserService`接口
2. 配置第三方OAuth2提供商
3. 处理用户信息映射

## 许可证

本项目采用MIT许可证，详情请参阅LICENSE文件。

## 贡献指南

欢迎提交Issue和Pull Request来改进本项目。

## 联系方式

如有问题或建议，请联系开发团队。
