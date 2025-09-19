# Logback XML配置修复说明

## 问题描述

在logback配置文件中，XML实体引用语法错误导致SAX解析异常：
```
Caused by: org.xml.sax.SAXParseException; systemId: file:/E:/work/reposite/tc/tc-gateway/target/classes/logback-spring.xml; lineNumber: 135; columnNumber: 32; 在实体引用中, 实体名称必须紧跟在 '&' 后面。
```

## 问题原因

在XML中，`&` 字符是特殊字符，必须转义为 `&amp;`。在springProfile配置中使用了未转义的 `&` 字符：

```xml
<!-- 错误的写法 -->
<springProfile name="!dev & !test & !prod">
```

## 修复方案

将所有logback配置文件中的 `&` 字符转义为 `&amp;`：

```xml
<!-- 正确的写法 -->
<springProfile name="!dev &amp; !test &amp; !prod">
```

## 修复的文件

### 1. tc-gateway模块
- `tc-gateway/src/main/resources/logback-spring.xml`
- 第135行：`<springProfile name="!dev & !test & !prod">`

### 2. tc-common模块
- `tc-core/tc-common/src/main/resources/logback-spring.xml`
- 第103行：`<springProfile name="!dev & !test & !prod">`
- `tc-core/tc-common/src/main/resources/logback-common.xml`
- 第127行：`<springProfile name="!dev & !test & !prod">`

### 3. tc-config模块
- `tc-core/tc-config/src/main/resources/logback-spring.xml`
- 第109行：`<springProfile name="!dev & !test & !prod">`

### 4. tc-user-service模块
- `tc-user/tc-user-service/src/main/resources/logback-spring.xml`
- 第141行：`<springProfile name="!dev & !test & !prod">`

## 修复内容

所有文件中的以下配置：
```xml
<springProfile name="!dev & !test & !prod">
```

修改为：
```xml
<springProfile name="!dev &amp; !test &amp; !prod">
```

## XML实体引用规则

在XML中，以下字符需要转义：

| 字符 | 转义后 | 说明 |
|------|--------|------|
| `&` | `&amp;` | 和号 |
| `<` | `&lt;` | 小于号 |
| `>` | `&gt;` | 大于号 |
| `"` | `&quot;` | 双引号 |
| `'` | `&apos;` | 单引号 |

## 验证方法

### 1. 编译验证
```bash
# 检查是否有编译错误
mvn clean compile
```

### 2. 运行验证
```bash
# 运行测试类验证logback配置
java -cp "target/classes:target/test-classes" com.tcyh.common.test.LogbackXmlTest
```

### 3. 启动验证
```bash
# 启动各个模块验证logback配置是否生效
# 检查控制台输出和日志文件生成
```

## 测试用例

创建了 `LogbackXmlTest.java` 测试类，包含以下测试：

1. **基本日志输出测试**
   - INFO、WARN、ERROR级别日志
   - 验证日志格式和输出

2. **MDC上下文测试**
   - 测试MDC（Mapped Diagnostic Context）
   - 验证上下文信息是否正确输出

3. **异常日志测试**
   - 测试异常堆栈信息输出
   - 验证异常日志格式

4. **模块日志测试**
   - 测试不同包名的日志输出
   - 验证模块特定的日志配置

## 注意事项

1. **XML语法严格性**
   - XML对语法要求非常严格
   - 任何特殊字符都必须正确转义

2. **Spring Profile语法**
   - `!dev & !test & !prod` 表示非开发、非测试、非生产环境
   - 转义后：`!dev &amp; !test &amp; !prod`

3. **配置文件一致性**
   - 所有模块的logback配置都需要保持一致
   - 确保XML语法正确性

## 修复效果

修复后的配置文件：
- ✅ 不再有SAX解析异常
- ✅ 可以正常启动各个模块
- ✅ 日志配置正常工作
- ✅ 支持不同环境的日志级别配置

## 总结

通过将XML中的 `&` 字符正确转义为 `&amp;`，成功修复了logback配置文件的SAX解析异常。这是一个典型的XML语法问题，修复后所有模块都可以正常启动和运行。
