# JWT RS256算法改造和响应体统一化改造总结

## 完成的工作

### 1. JWT算法从HS256改为RS256

#### 1.1 生成RSA密钥对
- 创建了RSA密钥对生成工具类 `RsaKeyGenerator`
- 生成了2048位的RSA私钥和公钥
- 私钥文件：`tc-core/tc-common/src/main/resources/jwt-private-key.pem`
- 公钥文件：`tc-core/tc-common/src/main/resources/jwt-public-key.pem`
- 将密钥文件复制到各个模块的resources目录

#### 1.2 更新JWT工具类
- **tc-common/JwtUtils.java**：更新为支持RS256算法，使用RSA私钥签名，公钥验证
- **tc-user-service/JwtUtil.java**：更新为支持RS256算法，使用RSA私钥签名，公钥验证
- **tc-gateway/JwtUtil.java**：更新为支持RS256算法，使用RSA公钥验证

#### 1.3 密钥管理
- 使用Spring的ClassPathResource加载密钥文件
- 支持PEM格式的RSA密钥解析
- 添加了完整的异常处理机制

### 2. 统一所有Controller响应体使用RestResp和PageRespDto

#### 2.1 更新Controller响应格式
- **AuthController**：所有接口返回类型从`ResponseEntity<T>`改为`RestResp<T>`
- **TestController**：所有接口返回类型从`ResponseEntity<T>`改为`RestResp<T>`
- **SecurityMonitorController**：所有接口返回类型从`ResponseEntity<T>`改为`RestResp<T>`

#### 2.2 错误码统一
- 添加了缺失的错误码常量：
  - `USER_LOGIN_FAIL`：用户登录失败
  - `USER_REGISTER_FAIL`：用户注册失败
  - `USER_TOKEN_REFRESH_FAIL`：Token刷新失败
  - `USER_LOGOUT_FAIL`：用户登出失败

#### 2.3 依赖管理
- 在`tc-user-service/pom.xml`中添加了`tc-common`依赖
- 确保所有模块都能正确使用`RestResp`和`PageRespDto`

## 技术细节

### JWT RS256算法优势
1. **非对称加密**：使用RSA私钥签名，公钥验证，更安全
2. **密钥分离**：签名和验证使用不同密钥，符合最佳实践
3. **可扩展性**：支持多服务共享公钥进行验证

### 响应体统一化优势
1. **一致性**：所有API返回格式统一，便于前端处理
2. **错误处理**：统一的错误码和错误信息格式
3. **可维护性**：统一的响应结构便于后续维护和扩展

## 文件变更清单

### 新增文件
- `tc-core/tc-common/src/main/resources/jwt-private-key.pem`
- `tc-core/tc-common/src/main/resources/jwt-public-key.pem`
- `tc-user/tc-user-service/src/main/resources/jwt-private-key.pem`
- `tc-user/tc-user-service/src/main/resources/jwt-public-key.pem`
- `tc-gateway/src/main/resources/jwt-private-key.pem`
- `tc-gateway/src/main/resources/jwt-public-key.pem`

### 修改文件
- `tc-core/tc-common/src/main/java/com/tcyh/common/auth/JwtUtils.java`
- `tc-user/tc-user-service/src/main/java/com/tcyh/user/util/JwtUtil.java`
- `tc-gateway/src/main/java/com/tcyh/gateway/util/JwtUtil.java`
- `tc-user/tc-user-service/src/main/java/com/tcyh/user/controller/AuthController.java`
- `tc-user/tc-user-service/src/main/java/com/tcyh/user/controller/TestController.java`
- `tc-gateway/src/main/java/com/tcyh/gateway/controller/SecurityMonitorController.java`
- `tc-core/tc-common/src/main/java/com/tcyh/common/constant/ErrorCodeEnum.java`
- `tc-user/tc-user-service/pom.xml`

### 删除文件
- `tc-core/tc-common/src/main/java/com/tcyh/common/auth/RsaKeyGenerator.java`（临时工具类）

## 注意事项

1. **密钥安全**：RSA私钥文件需要妥善保管，建议在生产环境中使用环境变量或密钥管理服务
2. **密钥轮换**：建议定期轮换RSA密钥对，提高安全性
3. **异常处理**：所有JWT相关操作都添加了完整的异常处理
4. **向后兼容**：响应体格式变更可能影响现有客户端，需要协调前端团队更新

## 测试建议

1. **JWT功能测试**：
   - 测试token生成和验证
   - 测试token过期处理
   - 测试无效token处理

2. **API响应测试**：
   - 测试所有接口返回格式
   - 测试错误码返回
   - 测试分页接口（如果有）

3. **集成测试**：
   - 测试网关到用户服务的认证流程
   - 测试跨服务token验证
