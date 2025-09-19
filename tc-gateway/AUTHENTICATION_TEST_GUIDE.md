# TC 微服务认证链路完整测试指南

## 🧪 测试环境准备

### 1. 启动服务

```bash
# 启动Redis
redis-server

# 启动MySQL
# 创建数据库: tc_user
# 执行SQL脚本: tc_user.sql

# 启动用户服务
cd tc-user/tc-user-service
mvn clean package -DskipTests
java -jar target/tc-user-service.jar

# 启动网关服务
cd tc-gateway
mvn clean package -DskipTests
java -jar target/tc-gateway.jar
```

### 2. 服务地址

- **网关服务**: http://localhost:8080
- **用户服务**: http://localhost:8081
- **Redis**: localhost:6379
- **MySQL**: localhost:3306

## 🔐 认证链路测试流程

### 阶段1: 用户注册测试

```bash
# 1. 用户注册
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "Test123456",
    "confirmPassword": "Test123456",
    "nickname": "测试用户",
    "phone": "13800138001",
    "email": "test@example.com"
  }'

# 预期响应
{
  "code": 200,
  "message": "注册成功"
}
```

### 阶段2: 用户登录测试

```bash
# 2. 用户登录
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "Test123456"
  }'

# 预期响应
{
  "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
  "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400,
  "userId": 1,
  "username": "testuser",
  "nickname": "测试用户",
  "avatar": null,
  "loginTime": "2025-09-17T10:30:00"
}
```

### 阶段3: 访问受保护资源测试

```bash
# 3. 访问用户信息（需要认证）
curl -X GET http://localhost:8080/api/user/profile \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."

# 预期响应
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": 1,
    "username": "testuser",
    "nickname": "测试用户",
    "email": "test@example.com",
    "phone": "13800138001"
  }
}
```

### 阶段4: Token验证测试

```bash
# 4. 验证Token有效性
curl -X GET "http://localhost:8080/api/auth/validate?token=eyJhbGciOiJIUzUxMiJ9..."

# 预期响应
true
```

### 阶段5: 用户登出测试

```bash
# 5. 用户登出
curl -X POST http://localhost:8080/api/auth/logout \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."

# 预期响应
{
  "code": 200,
  "message": "登出成功"
}

# 6. 验证Token已失效
curl -X GET http://localhost:8080/api/user/profile \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."

# 预期响应
{
  "code": 401,
  "message": "Token已失效",
  "timestamp": 1697123456789,
  "path": "/api/user/profile"
}
```

## 📊 监控接口测试

### 1. 安全统计信息

```bash
# 获取今日认证统计
curl -X GET http://localhost:8080/api/monitor/stats

# 预期响应
{
  "loginSuccessCount": 1,
  "loginFailedCount": 0,
  "tokenValidateSuccessCount": 2,
  "tokenValidateFailedCount": 1,
  "loginSuccessRate": "100.00%",
  "tokenValidateSuccessRate": "66.67%",
  "timestamp": 1697123456789
}
```

### 2. 健康检查

```bash
# 网关健康检查
curl -X GET http://localhost:8080/api/monitor/health

# 预期响应
{
  "status": "UP",
  "service": "tc-gateway",
  "timestamp": 1697123456789,
  "version": "1.0.0"
}
```

### 3. 路由信息

```bash
# 获取路由配置
curl -X GET http://localhost:8080/api/monitor/routes

# 预期响应
{
  "routes": {
    "/api/auth/**": "认证服务路由",
    "/api/user/**": "用户服务路由",
    "/api/public/**": "公开接口路由",
    "/api/test/**": "测试接口路由",
    "/swagger-ui/**": "API文档路由",
    "/actuator/**": "健康检查路由"
  },
  "totalRoutes": 6,
  "timestamp": 1697123456789
}
```

## 🔍 错误场景测试

### 1. 无效Token测试

```bash
# 使用无效Token
curl -X GET http://localhost:8080/api/user/profile \
  -H "Authorization: Bearer invalid_token"

# 预期响应
{
  "code": 401,
  "message": "Token无效或已过期",
  "timestamp": 1697123456789,
  "path": "/api/user/profile"
}
```

### 2. 缺少认证信息测试

```bash
# 不提供Authorization头
curl -X GET http://localhost:8080/api/user/profile

# 预期响应
{
  "code": 401,
  "message": "缺少认证信息",
  "timestamp": 1697123456789,
  "path": "/api/user/profile"
}
```

### 3. 错误密码测试

```bash
# 使用错误密码登录
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "WrongPassword"
  }'

# 预期响应
{
  "code": 500,
  "message": "用户名或密码错误"
}
```

## 📈 性能测试

### 1. 并发登录测试

```bash
# 使用Apache Bench进行并发测试
ab -n 100 -c 10 -H "Content-Type: application/json" \
  -p login_data.json \
  http://localhost:8080/api/auth/login

# login_data.json内容
{
  "username": "testuser",
  "password": "Test123456"
}
```

### 2. Token验证性能测试

```bash
# 并发Token验证测试
ab -n 1000 -c 50 -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..." \
  http://localhost:8080/api/user/profile
```

## 🔐 安全测试

### 1. 暴力破解测试

```bash
# 连续错误登录测试
for i in {1..10}; do
  curl -X POST http://localhost:8080/api/auth/login \
    -H "Content-Type: application/json" \
    -d '{
      "username": "testuser",
      "password": "WrongPassword'$i'"
    }'
done

# 预期结果：第6次开始账户被锁定
```

### 2. Token重放攻击测试

```bash
# 使用已登出的Token
curl -X GET http://localhost:8080/api/user/profile \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."

# 预期响应：Token已失效
```

## 📋 测试检查清单

### ✅ 功能测试
- [ ] 用户注册功能正常
- [ ] 用户登录功能正常
- [ ] JWT Token生成正确
- [ ] Token验证功能正常
- [ ] 用户信息传递正确
- [ ] 用户登出功能正常
- [ ] 黑名单机制有效

### ✅ 安全测试
- [ ] 无效Token被拒绝
- [ ] 过期Token被拒绝
- [ ] 黑名单Token被拒绝
- [ ] 缺少认证信息被拒绝
- [ ] 暴力破解被阻止
- [ ] 密码加密正确

### ✅ 性能测试
- [ ] 登录响应时间 < 500ms
- [ ] Token验证响应时间 < 100ms
- [ ] 并发处理能力 > 1000 QPS
- [ ] 内存使用合理
- [ ] CPU使用合理

### ✅ 监控测试
- [ ] 统计信息准确
- [ ] 日志记录完整
- [ ] 健康检查正常
- [ ] 路由信息正确
- [ ] 性能指标正常

## 🚨 故障排查

### 1. 常见问题

**问题**: 登录返回500错误
**排查**: 
- 检查MySQL连接
- 检查Redis连接
- 查看用户服务日志

**问题**: Token验证失败
**排查**:
- 检查JWT密钥配置
- 检查Token格式
- 查看网关日志

**问题**: 路由转发失败
**排查**:
- 检查服务发现配置
- 检查负载均衡配置
- 查看网关路由日志

### 2. 日志查看

```bash
# 查看网关日志
tail -f tc-gateway/logs/gateway.log

# 查看用户服务日志
tail -f tc-user/tc-user-service/logs/user-service.log

# 查看Redis日志
redis-cli monitor

# 查看MySQL日志
tail -f /var/log/mysql/error.log
```

## 📊 测试报告模板

### 测试环境
- 操作系统: Linux/Windows/macOS
- Java版本: 17
- Redis版本: 7.0
- MySQL版本: 8.0
- 测试时间: 2025-09-17

### 测试结果
- 功能测试: ✅ 通过
- 安全测试: ✅ 通过
- 性能测试: ✅ 通过
- 监控测试: ✅ 通过

### 性能指标
- 平均响应时间: 200ms
- 最大响应时间: 500ms
- 并发处理能力: 1500 QPS
- 错误率: 0.01%

### 问题记录
- 无重大问题
- 建议优化: 增加缓存预热机制
