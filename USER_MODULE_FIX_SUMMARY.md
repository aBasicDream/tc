# 用户模块启动错误修复总结

## 问题描述

用户模块启动时出现以下错误：
```
Property 'sqlSessionFactory' or 'sqlSessionTemplate' are required
```

## 问题原因

1. **缺少tc-config模块依赖** - 用户服务没有引入tc-config模块，导致MyBatis配置无法加载
2. **缺少MyBatis Plus Spring Boot Starter** - 只有MyBatis核心依赖，缺少Spring Boot集成
3. **缺少MyBatis Plus配置类** - 没有配置MyBatis Plus的拦截器和分页插件

## 修复方案

### 1. 添加tc-config模块依赖

在 `tc-user/tc-user-service/pom.xml` 中添加：

```xml
<!-- TC Config Module -->
<dependency>
    <groupId>com.tcyh</groupId>
    <artifactId>tc-config</artifactId>
    <version>2.0.0-SNAPSHOT</version>
</dependency>
```

### 2. 添加MyBatis Plus Spring Boot Starter

```xml
<!-- MyBatis Plus Spring Boot Starter -->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
    <version>3.5.3</version>
</dependency>

<!-- MySQL Driver -->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
</dependency>
```

### 3. 移除重复的MyBatis依赖

移除了以下重复依赖：
- `mybatis`
- `mybatis-plus-core`
- `mybatis-spring`
- `mybatis-plus-extension`

### 4. 创建MyBatis Plus配置类

创建了 `tc-user/tc-user-service/src/main/java/com/tcyh/user/config/MybatisPlusConfig.java`：

```java
@Configuration
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        
        // 分页插件
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        paginationInnerInterceptor.setDbType(DbType.MYSQL);
        paginationInnerInterceptor.setMaxLimit(1000L);
        interceptor.addInnerInterceptor(paginationInnerInterceptor);
        
        return interceptor;
    }
}
```

### 5. 修复AuthController缺失方法

在 `tc-auth/tc-auth-service/src/main/java/com/tcyh/auth/controller/AuthController.java` 中添加了：

- `buildLoginResponse()` 方法
- `buildUserInfoResponse()` 方法
- 添加了缺失的import

## 修复后的依赖结构

### tc-user-service模块依赖
- ✅ tc-common (公共模块)
- ✅ tc-config (配置模块)
- ✅ spring-boot-starter-web
- ✅ spring-boot-starter-data-jpa
- ✅ mybatis-plus-boot-starter
- ✅ mysql-connector-java
- ✅ spring-boot-starter-security
- ✅ spring-boot-starter-cache

### 配置文件
- ✅ application.yml (包含tc-config配置)
- ✅ logback-spring.xml (日志配置)

## 验证方法

1. **编译验证**：
   ```bash
   cd tc-user/tc-user-service
   mvn clean compile
   ```

2. **启动验证**：
   ```bash
   mvn spring-boot:run
   ```

3. **检查日志**：
   - 确认MyBatis Plus配置加载成功
   - 确认数据库连接正常
   - 确认Mapper扫描成功

## 预期结果

修复后，用户服务应该能够正常启动，不再出现SqlSessionFactory相关的错误。

## 注意事项

1. **数据库连接**：确保MySQL服务正在运行，数据库`tc_user`存在
2. **配置文件**：确保`application-common.yml`中的数据库配置正确
3. **依赖版本**：确保所有依赖版本兼容

## 相关文件

- `tc-user/tc-user-service/pom.xml` - 依赖配置
- `tc-user/tc-user-service/src/main/java/com/tcyh/user/config/MybatisPlusConfig.java` - MyBatis配置
- `tc-core/tc-config/src/main/resources/application-common.yml` - 公共配置
- `tc-auth/tc-auth-service/src/main/java/com/tcyh/auth/controller/AuthController.java` - 认证控制器
