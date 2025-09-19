# TC Gateway 网关服务

## 项目简介

TC Gateway 是基于 Spring Cloud Gateway 构建的微服务网关，负责统一认证、路由转发、负载均衡等功能。

## 主要功能

### 🔐 统一认证
- JWT Token 验证
- 黑名单机制
- 用户信息传递

### 🚀 路由转发
- 动态路由配置
- 负载均衡
- 路径重写

### 🛡️ 安全防护
- CORS 跨域处理
- 请求头添加
- 异常统一处理

### 📊 监控管理
- 健康检查
- 链路追踪
- 性能监控

## 技术栈

- **Spring Cloud Gateway**: 微服务网关
- **Spring Boot**: 应用框架
- **Redis**: 缓存和会话管理
- **JWT**: 无状态认证
- **Redisson**: 分布式锁

## 项目结构

```
tc-gateway/
├── src/main/java/com/tcyh/gateway/
│   ├── TcGatewayApplication.java          # 启动类
│   ├── config/                           # 配置类
│   │   ├── CorsConfig.java              # CORS配置
│   │   ├── GatewayConfig.java           # 网关路由配置
│   │   └── RedisConfig.java             # Redis配置
│   ├── filter/                          # 过滤器
│   │   └── JwtAuthenticationFilter.java # JWT认证过滤器
│   ├── handler/                         # 处理器
│   │   └── GlobalExceptionHandler.java  # 全局异常处理
│   └── util/                           # 工具类
│       └── JwtUtil.java                 # JWT工具类
├── src/main/resources/
│   ├── application.yml                 # 应用配置
│   └── application-docker.yml           # Docker环境配置
├── Dockerfile                          # Docker镜像构建
├── docker-compose.yml                  # Docker编排
├── start.bat                           # Windows启动脚本
├── start.sh                            # Linux启动脚本
└── pom.xml                             # Maven配置
```

## 快速开始

### 1. 环境要求

- JDK 17+
- Maven 3.6+
- Redis 6.0+
- MySQL 8.0+

### 2. 本地运行

```bash
# 启动Redis
redis-server

# 启动MySQL
# 创建数据库 tc_user

# 编译运行
mvn clean package -DskipTests
java -jar target/tc-gateway.jar
```

### 3. Docker运行

```bash
# 构建镜像
docker build -t tc-gateway .

# 运行容器
docker run -d -p 8080:8080 --name tc-gateway tc-gateway
```

### 4. Docker Compose运行

```bash
# 启动所有服务
docker-compose up -d

# 查看日志
docker-compose logs -f tc-gateway
```

## 配置说明

### 路由配置

网关支持以下路由：

- `/api/auth/**` → 认证服务
- `/api/user/**` → 用户服务
- `/api/public/**` → 公开接口
- `/api/test/**` → 测试接口
- `/swagger-ui/**` → API文档
- `/actuator/**` → 健康检查

### JWT配置

```yaml
jwt:
  secret: tc-gateway-jwt-secret-key-for-distributed-system
  expiration: 86400  # 24小时
```

### Redis配置

```yaml
spring:
  redis:
    host: localhost
    port: 6379
    database: 0
```

## API接口

### 认证流程

1. **用户登录**
   ```bash
   POST /api/auth/login
   Content-Type: application/json
   
   {
     "username": "testuser",
     "password": "Test123456"
   }
   ```

2. **访问受保护资源**
   ```bash
   GET /api/user/profile
   Authorization: Bearer <token>
   ```

3. **用户登出**
   ```bash
   POST /api/auth/logout
   Authorization: Bearer <token>
   ```

## 部署说明

### 生产环境配置

1. **修改JWT密钥**
   ```yaml
   jwt:
     secret: ${JWT_SECRET:your-production-secret-key}
   ```

2. **配置Redis集群**
   ```yaml
   spring:
     redis:
       cluster:
         nodes: redis1:6379,redis2:6379,redis3:6379
   ```

3. **启用HTTPS**
   ```yaml
   server:
     ssl:
       enabled: true
       key-store: classpath:keystore.p12
       key-store-password: password
   ```

### 监控配置

1. **健康检查**
   ```bash
   GET /actuator/health
   ```

2. **指标监控**
   ```bash
   GET /actuator/metrics
   ```

## 常见问题

### Q: 如何添加新的服务路由？

A: 在 `GatewayConfig.java` 中添加新的路由配置：

```java
.route("new-service", r -> r
    .path("/api/new/**")
    .uri("lb://new-service")
    .filters(f -> f
        .stripPrefix(1)
        .addRequestHeader("X-Gateway", "tc-gateway")
    )
)
```

### Q: 如何自定义认证逻辑？

A: 修改 `JwtAuthenticationFilter.java` 中的 `filter` 方法。

### Q: 如何处理跨域问题？

A: 网关已配置CORS，支持所有来源的跨域请求。

## 联系方式

- 作者: fp
- 邮箱: fp@example.com
- 项目地址: https://github.com/your-org/tc-gateway
