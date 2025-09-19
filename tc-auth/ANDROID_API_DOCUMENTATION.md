# TC OAuth2认证服务 - 安卓对接HTTPS接口文档

## 概述

本文档描述了TC OAuth2认证服务为安卓应用提供的HTTPS接口，支持多种登录方式包括密码登录、手机号验证码登录和微信登录。

## 基础信息

- **服务地址**: `https://your-domain.com:9000/auth`
- **协议**: HTTPS (TLS 1.2+)
- **数据格式**: JSON
- **字符编码**: UTF-8
- **认证方式**: Bearer Token

## 通用响应格式

所有接口都使用统一的响应格式：

```json
{
    "code": "200",
    "message": "操作成功",
    "data": {},
    "timestamp": "2025-09-18T12:00:00Z"
}
```

### 响应字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| code | String | 响应码，200表示成功 |
| message | String | 响应消息 |
| data | Object | 响应数据 |
| timestamp | String | 响应时间戳 |

### 错误码说明

| 错误码 | 说明 |
|--------|------|
| A0112 | 用户注册失败 |
| A0211 | 用户登录失败 |
| A0231 | Token刷新失败 |
| A0232 | 用户登出失败 |
| A0233 | Token验证失败 |
| A0234 | 密码错误 |
| A0235 | 密码不一致 |

## 认证接口

### 1. 用户注册

**接口地址**: `POST /api/auth/register`

**请求参数**:
```json
{
    "username": "testuser",
    "password": "password123",
    "confirmPassword": "password123",
    "email": "test@example.com",
    "phone": "13800138000",
    "nickname": "测试用户"
}
```

**参数说明**:
| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | String | 是 | 用户名，3-50个字符，只能包含字母、数字和下划线 |
| password | String | 是 | 密码，6-20个字符 |
| confirmPassword | String | 是 | 确认密码 |
| email | String | 否 | 邮箱地址 |
| phone | String | 否 | 手机号，11位数字 |
| nickname | String | 否 | 昵称，最多50个字符 |

**响应示例**:
```json
{
    "code": "200",
    "message": "注册成功",
    "data": {
        "id": 1,
        "username": "testuser",
        "email": "test@example.com",
        "phone": "13800138000",
        "nickname": "测试用户",
        "avatar": null,
        "enabled": true,
        "roles": ["USER"],
        "createTime": "2025-09-18T12:00:00Z",
        "lastLoginTime": null
    },
    "timestamp": "2025-09-18T12:00:00Z"
}
```

### 2. 密码登录

**接口地址**: `POST /api/auth/login`

**请求参数**:
```json
{
    "username": "testuser",
    "password": "password123",
    "rememberMe": false
}
```

**参数说明**:
| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | String | 是 | 用户名或邮箱 |
| password | String | 是 | 密码 |
| rememberMe | Boolean | 否 | 是否记住登录状态 |

**响应示例**:
```json
{
    "code": "200",
    "message": "登录成功",
    "data": {
        "accessToken": "jwt_access_token_1",
        "refreshToken": "jwt_refresh_token_1",
        "tokenType": "Bearer",
        "expiresIn": 3600,
        "userInfo": {
            "id": 1,
            "username": "testuser",
            "email": "test@example.com",
            "phone": "13800138000",
            "nickname": "测试用户",
            "avatar": null,
            "enabled": true,
            "roles": ["USER"],
            "createTime": "2025-09-18T12:00:00Z",
            "lastLoginTime": "2025-09-18T12:00:00Z"
        },
        "firstLogin": false
    },
    "timestamp": "2025-09-18T12:00:00Z"
}
```

### 3. 发送短信验证码

**接口地址**: `POST /api/auth/send-sms`

**请求参数**:
```json
{
    "phone": "13800138000",
    "codeType": "LOGIN",
    "deviceInfo": "Android 12, Samsung Galaxy S21"
}
```

**参数说明**:
| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| phone | String | 是 | 手机号，11位数字 |
| codeType | String | 是 | 验证码类型：LOGIN(登录)、REGISTER(注册)、RESET_PASSWORD(重置密码)、BIND_PHONE(绑定手机) |
| deviceInfo | String | 否 | 设备信息 |

**响应示例**:
```json
{
    "code": "200",
    "message": "验证码发送成功",
    "data": null,
    "timestamp": "2025-09-18T12:00:00Z"
}
```

### 4. 手机号验证码登录

**接口地址**: `POST /api/auth/login/phone`

**请求参数**:
```json
{
    "phone": "13800138000",
    "code": "123456",
    "deviceInfo": "Android 12, Samsung Galaxy S21"
}
```

**参数说明**:
| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| phone | String | 是 | 手机号，11位数字 |
| code | String | 是 | 6位数字验证码 |
| deviceInfo | String | 否 | 设备信息 |

**响应示例**:
```json
{
    "code": "200",
    "message": "登录成功",
    "data": {
        "accessToken": "jwt_access_token_1",
        "refreshToken": "jwt_refresh_token_1",
        "tokenType": "Bearer",
        "expiresIn": 3600,
        "userInfo": {
            "id": 1,
            "username": "phone_13800138000",
            "email": null,
            "phone": "13800138000",
            "nickname": null,
            "avatar": null,
            "enabled": true,
            "roles": ["USER"],
            "createTime": "2025-09-18T12:00:00Z",
            "lastLoginTime": "2025-09-18T12:00:00Z"
        },
        "firstLogin": true
    },
    "timestamp": "2025-09-18T12:00:00Z"
}
```

### 5. 微信登录

**接口地址**: `POST /api/auth/login/wechat`

**请求参数**:
```json
{
    "code": "wechat_auth_code_from_wechat_sdk",
    "deviceInfo": "Android 12, Samsung Galaxy S21",
    "clientType": "android"
}
```

**参数说明**:
| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| code | String | 是 | 微信授权码，由微信SDK获取 |
| deviceInfo | String | 否 | 设备信息 |
| clientType | String | 否 | 客户端类型：android、ios、web |

**响应示例**:
```json
{
    "code": "200",
    "message": "登录成功",
    "data": {
        "accessToken": "jwt_access_token_1",
        "refreshToken": "jwt_refresh_token_1",
        "tokenType": "Bearer",
        "expiresIn": 3600,
        "userInfo": {
            "id": 1,
            "username": "wx_openid123456",
            "email": null,
            "phone": null,
            "nickname": "微信用户",
            "avatar": "https://thirdwx.qlogo.cn/...",
            "enabled": true,
            "roles": ["USER"],
            "createTime": "2025-09-18T12:00:00Z",
            "lastLoginTime": "2025-09-18T12:00:00Z"
        },
        "firstLogin": false
    },
    "timestamp": "2025-09-18T12:00:00Z"
}
```

### 6. 获取当前用户信息

**接口地址**: `GET /api/auth/me`

**请求头**:
```
Authorization: Bearer jwt_access_token_1
```

**响应示例**:
```json
{
    "code": "200",
    "message": "获取成功",
    "data": {
        "id": 1,
        "username": "testuser",
        "email": "test@example.com",
        "phone": "13800138000",
        "nickname": "测试用户",
        "avatar": null,
        "enabled": true,
        "roles": ["USER"],
        "createTime": "2025-09-18T12:00:00Z",
        "lastLoginTime": "2025-09-18T12:00:00Z"
    },
    "timestamp": "2025-09-18T12:00:00Z"
}
```

### 7. 用户登出

**接口地址**: `POST /api/auth/logout`

**请求头**:
```
Authorization: Bearer jwt_access_token_1
```

**响应示例**:
```json
{
    "code": "200",
    "message": "登出成功",
    "data": null,
    "timestamp": "2025-09-18T12:00:00Z"
}
```

### 8. 绑定微信账号

**接口地址**: `POST /api/auth/bind-wechat`

**请求头**:
```
Authorization: Bearer jwt_access_token_1
```

**请求参数**:
```json
{
    "code": "wechat_auth_code_from_wechat_sdk",
    "deviceInfo": "Android 12, Samsung Galaxy S21",
    "clientType": "android"
}
```

**响应示例**:
```json
{
    "code": "200",
    "message": "绑定成功",
    "data": null,
    "timestamp": "2025-09-18T12:00:00Z"
}
```

## 验证接口

### 9. 检查用户名是否存在

**接口地址**: `GET /api/auth/check-username`

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | String | 是 | 用户名 |

**响应示例**:
```json
{
    "code": "200",
    "message": "检查完成",
    "data": true,
    "timestamp": "2025-09-18T12:00:00Z"
}
```

### 10. 检查邮箱是否存在

**接口地址**: `GET /api/auth/check-email`

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| email | String | 是 | 邮箱地址 |

**响应示例**:
```json
{
    "code": "200",
    "message": "检查完成",
    "data": false,
    "timestamp": "2025-09-18T12:00:00Z"
}
```

### 11. 检查手机号是否存在

**接口地址**: `GET /api/auth/check-phone`

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| phone | String | 是 | 手机号 |

**响应示例**:
```json
{
    "code": "200",
    "message": "检查完成",
    "data": false,
    "timestamp": "2025-09-18T12:00:00Z"
}
```

## HTTPS配置

### SSL证书配置

服务使用HTTPS协议，需要配置SSL证书：

1. **证书格式**: PKCS12 (.p12)
2. **证书密码**: tc123456
3. **证书别名**: tc-auth
4. **支持的TLS版本**: TLS 1.2+

### 安卓客户端配置

在安卓应用中配置HTTPS连接：

```java
// 信任所有证书（仅用于开发环境）
public class TrustAllCerts implements X509TrustManager {
    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) {}
    
    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) {}
    
    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}

// 配置OkHttp客户端
OkHttpClient client = new OkHttpClient.Builder()
    .sslSocketFactory(sslContext.getSocketFactory(), trustAllCerts)
    .hostnameVerifier((hostname, session) -> true)
    .build();
```

## 错误处理

### 常见错误及处理

1. **网络连接失败**
   - 检查网络连接
   - 确认服务器地址和端口

2. **SSL证书验证失败**
   - 检查证书配置
   - 确认TLS版本支持

3. **Token过期**
   - 使用refreshToken刷新
   - 重新登录获取新Token

4. **验证码错误**
   - 检查验证码是否正确
   - 确认验证码是否过期

5. **微信授权失败**
   - 检查微信AppID配置
   - 确认微信SDK版本

## 安全建议

1. **Token安全**
   - 妥善保管AccessToken
   - 定期刷新Token
   - 登出时清除本地Token

2. **网络安全**
   - 使用HTTPS协议
   - 验证SSL证书
   - 避免在非安全网络下传输敏感信息

3. **数据安全**
   - 敏感信息加密存储
   - 定期清理本地缓存
   - 使用安全的密码策略

## 示例代码

### Android Retrofit配置

```java
public class ApiClient {
    private static final String BASE_URL = "https://your-domain.com:9000/auth/";
    
    public static ApiService getApiService() {
        OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(new AuthInterceptor())
            .build();
            
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
            
        return retrofit.create(ApiService.class);
    }
}

public interface ApiService {
    @POST("api/auth/login")
    Call<RestResp<LoginResponse>> login(@Body LoginRequest request);
    
    @POST("api/auth/login/phone")
    Call<RestResp<LoginResponse>> phoneLogin(@Body PhoneLoginRequest request);
    
    @POST("api/auth/login/wechat")
    Call<RestResp<LoginResponse>> wechatLogin(@Body WechatLoginRequest request);
    
    @GET("api/auth/me")
    Call<RestResp<UserInfoResponse>> getCurrentUser();
}
```

### 微信登录集成

```java
// 微信登录
public void wechatLogin() {
    IWXAPI api = WXAPIFactory.createWXAPI(this, "your_wechat_app_id");
    SendAuth.Req req = new SendAuth.Req();
    req.scope = "snsapi_userinfo";
    req.state = "wechat_sdk_demo";
    api.sendReq(req);
}

// 处理微信授权结果
@Override
public void onResp(BaseResp resp) {
    if (resp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {
        SendAuth.Resp authResp = (SendAuth.Resp) resp;
        if (authResp.errCode == 0) {
            String code = authResp.code;
            // 调用后端微信登录接口
            wechatLoginWithCode(code);
        }
    }
}
```

## 更新日志

### v1.0.0 (2025-09-18)
- 初始版本发布
- 支持密码登录、手机号登录、微信登录
- 支持HTTPS协议
- 完整的用户管理功能

## 联系方式

如有问题或建议，请联系开发团队：
- 邮箱: dev@tc-system.com
- 电话: 400-123-4567
