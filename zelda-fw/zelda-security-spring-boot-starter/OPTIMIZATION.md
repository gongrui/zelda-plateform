# Spring Boot Starter Zelda Security 优化建议

## 已实现的优化 ✅

### 1. 模板化页面（已实现）
- **问题**：登录页面和授权确认页面的 HTML 硬编码在 Java 代码中
- **优化**：创建 `OAuth2TemplateProvider`，支持从外部文件加载自定义模板
- **收益**：
  - 方便定制 UI
  - 代码更清晰易维护
  - 支持 CSS/JS 独立管理

**使用方式**：
```yaml
zelda:
  security:
    server:
      login-template-path: templates/oauth2/custom-login.html
      confirm-template-path: templates/oauth2/custom-confirm.html
```

---

## 其他优化建议 💡

### 2️⃣ **网关异常响应格式优化** 🔴 高优先级

**问题**：
```java
// 当前实现
response.writeWith(Mono.just(
    response.bufferFactory().wrap(body.toString().getBytes())
));
```
- `body.toString()` 直接调用 `HashMap.toString()`，格式不是标准 JSON
- 缺少 UTF-8 字符编码声明

**优化方案**：
```java
@Bean
public GatewayExceptionHandler gatewayExceptionHandler() {
    return new GatewayExceptionHandler();
}

@RestControllerAdvice
@Order(-1)
public static class GatewayExceptionHandler {
    @ExceptionHandler(NotLoginException.class)
    public Mono<Void> handleNotLogin(NotLoginException e, ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        // 使用 Jackson 转换为标准 JSON
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> body = new HashMap<>();
        body.put("code", 401);
        body.put("msg", "Token 无效或已过期");
        body.put("timestamp", System.currentTimeMillis());

        byte[] bytes = mapper.writeValueAsBytes(body);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
    }
}
```

**收益**：
- ✅ 标准的 JSON 格式
- ✅ 添加时间戳字段
- ✅ 更好的中文编码支持

---

### 3️⃣ **InMemoryClientDetailsService 硬编码问题** 🟡 中优先级

**问题**：
```java
public SaClientModel getClientByClientId(String clientId) {
    return new SaClientModel()
            .setClientId("1001")  // 硬编码！
            .setClientSecret("aaaa-bbbb-cccc-dddd")  // 硬编码！
            // ...
}
```
- 忽略了 `clientId` 参数，总是返回同一个客户端
- 注释说"实际使用时应该根据 clientId 参数查询"，但没有实现

**优化方案 A（简单）**：
```java
@Override
public SaClientModel getClientByClientId(String clientId) {
    // 仅返回匹配的客户端
    if (!"1001".equals(clientId)) {
        return null;
    }

    return new SaClientModel()
            .setClientId("1001")
            .setClientSecret("aaaa-bbbb-cccc-dddd")
            .setAllowRedirectUris(Arrays.asList("*"))
            .setContractScopes(Arrays.asList("openid", "userinfo"))
            .setAllowGrantTypes(Arrays.asList(
                    "authorization_code",
                    "password",
                    "refresh_token",
                    "client_credentials"
            ));
}
```

**优化方案 B（推荐）**：
```java
@Data
@ConfigurationProperties(prefix = "zelda.security.oauth2.clients")
public class OAuth2ClientProperties {
    private List<ClientInfo> clients = new ArrayList<>();

    @Data
    public static class ClientInfo {
        private String clientId;
        private String clientSecret;
        private List<String> redirectUris;
        private List<String> scopes;
        private List<String> grantTypes;
    }
}

public class InMemoryClientDetailsService implements OAuth2ClientDetailsService {
    private final Map<String, SaClientModel> clientMap = new HashMap<>();

    public InMemoryClientDetailsService(OAuth2ClientProperties properties) {
        // 从配置文件初始化客户端
        for (ClientInfo info : properties.getClients()) {
            SaClientModel model = new SaClientModel()
                    .setClientId(info.getClientId())
                    .setClientSecret(info.getClientSecret())
                    .setAllowRedirectUris(info.getRedirectUris())
                    .setContractScopes(info.getScopes())
                    .setAllowGrantTypes(info.getGrantTypes());
            clientMap.put(info.getClientId(), model);
        }
    }

    @Override
    public SaClientModel getClientByClientId(String clientId) {
        return clientMap.get(clientId);
    }
}
```

**配置示例**：
```yaml
zelda:
  security:
    oauth2:
      clients:
        - client-id: "web-app"
          client-secret: "web-secret"
          redirect-uris: ["http://localhost:8080/callback"]
          scopes: ["openid", "userinfo", "profile"]
          grant-types: ["authorization_code", "refresh_token"]
        - client-id: "mobile-app"
          client-secret: "mobile-secret"
          redirect-uris: ["myapp://callback"]
          scopes: ["openid", "userinfo"]
          grant-types: ["password", "refresh_token"]
```

**收益**：
- ✅ 支持多个客户端配置
- ✅ 配置化管理，无需改代码
- ✅ 避免硬编码安全问题

---

### 4️⃣ **网关用户信息透传 - 异常处理优化** 🟢 低优先级

**问题**：
```java
try {
    Long userId = StpUtil.getLoginIdAsLong();
    String username = (String) StpUtil.getSession().get("username");
    // ...
} catch (Exception e) {
    return chain.filter(exchange);  // 吞掉所有异常，日志丢失
}
```
- 宽泛的 `Exception` 捕获，可能隐藏真实问题
- 没有日志记录

**优化方案**：
```java
@Override
public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    if (!properties.getGateway().isEnabled()) return chain.filter(exchange);

    try {
        Long userId = StpUtil.getLoginIdAsLong();
        String username = (String) StpUtil.getSession().get("username");

        ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                .header(properties.getGateway().getUserIdHeader(), String.valueOf(userId))
                .header(properties.getGateway().getUsernameHeader(), username != null ? username : "")
                .build();
        return chain.filter(exchange.mutate().request(modifiedRequest).build());

    } catch (cn.dev33.satoken.exception.NotLoginException e) {
        // 未登录是正常情况，不透传用户信息
        log.debug("用户未登录，跳过用户信息透传");
        return chain.filter(exchange);
    } catch (Exception e) {
        // 其他异常记录日志
        log.error("获取用户信息失败", e);
        return chain.filter(exchange);
    }
}
```

**收益**：
- ✅ 更精细的异常处理
- ✅ 添加调试日志
- ✅ 避免隐藏真实问题

---

### 5️⃣ **安全性增强** 🔴 高优先级

**问题 A：敏感信息日志泄露**
```java
// 当前配置
private String clientSecret = "aaaa-bbbb-cccc-dddd";  // 写死在代码中！
```

**优化方案**：
- 使用配置文件（已在上面的方案 B 中说明）
- 生产环境禁止打印敏感信息

**问题 B：默认重定向 URI 过于宽泛**
```java
.setAllowRedirectUris(Arrays.asList("*"))  // 允许所有重定向！
```

**优化方案**：
```java
// 从配置读取，默认空列表，要求显式配置
.setAllowRedirectUris(info.getRedirectUris())
```

**问题 C：未验证客户端密钥强度**
- 建议添加密钥复杂度验证
- 生产环境要求密钥至少 32 位

---

### 6️⃣ **缓存优化** 🟢 低优先级

**建议：为元数据缓存添加过期时间配置**

当前配置：
```java
@Bean
@Primary
@ConditionalOnMissingBean
public RedisCacheManager cacheManager(RedisConnectionFactory factory) {
    RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofDays(7))  // 固定 7 天
            // ...
}
```

优化方案：
```java
@ConfigurationProperties(prefix = "zelda.security.cache")
public class CacheProperties {
    private Duration defaultTtl = Duration.ofDays(7);
    private Map<String, Duration> specificTtl = new HashMap<>();

    // getters and setters
}

// 在 Redis 配置中使用
Duration ttl = properties.getSpecificTtl().getOrDefault(cacheName, properties.getDefaultTtl());
RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
        .entryTtl(ttl)
        // ...
```

配置示例：
```yaml
zelda:
  security:
    cache:
      default-ttl: 7d
      specific-ttl:
        meta_view: 1d   # 视图配置 1 天
        meta_ds: 3d     # 数据源配置 3 天
```

---

### 7️⃣ **添加健康检查端点** 🟢 低优先级

**建议：提供安全模块的健康状态**

```java
@RestController
@RequestMapping("/actuator/security")
public class SecurityHealthController {

    @Autowired
    private CacheManager cacheManager;

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", System.currentTimeMillis());

        // 检查缓存状态
        Cache cache = cacheManager.getCache("meta_view");
        health.put("cache", cache != null ? "ACTIVE" : "INACTIVE");

        return health;
    }
}
```

---

## 优先级总结

| 优化项 | 优先级 | 影响范围 | 实施难度 |
|--------|---------|-----------|-----------|
| 模板化页面 | ✅ 已完成 | OAuth2 服务器 | 低 |
| 网关异常响应 | 🔴 高 | 网关 | 低 |
| 客户端配置化 | 🟡 中 | OAuth2 服务器 | 中 |
| 用户信息透传异常处理 | 🟢 低 | 网关 | 低 |
| 安全性增强 | 🔴 高 | OAuth2 服务器 | 中 |
| 缓存优化 | 🟢 低 | 缓存模块 | 中 |
| 健康检查 | 🟢 低 | 监控 | 低 |

---

## 快速实施指南

### 最小化改动（1-2 小时）
1. ✅ 模板化页面（已完成）
2. 🔴 网关异常响应格式优化
3. 🟡 客户端配置化（简单方案）

### 标准优化（1-2 天）
1. 🔴 安全性增强
2. 🟡 客户端配置化（完整方案）
3. 🟢 用户信息透传异常处理

### 完善优化（3-5 天）
1. 🟢 缓存优化
2. 🟢 健康检查端点
3. 完善单元测试
4. 编写迁移文档
