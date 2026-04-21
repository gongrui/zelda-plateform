# Zelda Demo App

订单管理演示应用，用于测试 Sa-Token OAuth2 注解功能。

## 功能特性

### Sa-Token 三大核心注解

1. **@SaCheckLogin** - 登录校验
   - 只有登录之后才能进入该方法
   - 应用于所有订单接口

2. **@SaCheckRole("admin")** - 角色校验
   - 必须具有指定角色标识才能进入该方法
   - 应用于创建订单接口

3. **@SaCheckPermission("user:add")** - 权限校验
   - 必须具有指定权限才能进入该方法
   - 应用于更新和删除订单接口

## API 接口

### 订单管理

| 方法 | 路径 | 说明 | 所需权限 |
|------|------|------|----------|
| GET | /orders | 查询订单列表 | 登录 |
| GET | /orders/{id} | 查询订单详情 | 登录 |
| POST | /orders | 创建订单 | 登录 + admin 角色 |
| PUT | /orders/{id} | 更新订单 | 登录 + user:update 权限 |
| DELETE | /orders/{id} | 删除订单 | 登录 + user:add 权限 |

## 快速开始

### 1. 数据库初始化

```sql
-- 执行 src/main/resources/schema.sql 创建订单表
```

### 2. 配置修改

修改 `application.yml` 中的数据库和 Redis 配置：

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/zelda_demo
    username: postgres
    password: your_password
  data:
    redis:
      host: localhost
      port: 6379
```

### 3. 启动应用

```bash
mvn clean install
cd zelda-demo-app
mvn spring-boot:run
```

### 4. 测试接口

#### 获取 Token（通过 UAA 认证服务）

```bash
POST http://localhost:8080/oauth/token
Content-Type: application/x-www-form-urlencoded

grant_type=password&username=admin&password=123456&client_id=web_app&client_secret=secret
```

#### 调用订单接口

```bash
# 查询订单列表
GET http://localhost:8082/orders
Authorization: Bearer {token}

# 创建订单（需要 admin 角色）
POST http://localhost:8082/orders
Authorization: Bearer {token}
Content-Type: application/json

{
  "orderNo": "ORD20250101001",
  "userId": 1,
  "productName": "测试商品",
  "amount": 99.99,
  "status": 0
}

# 更新订单（需要 user:update 权限）
PUT http://localhost:8082/orders/1
Authorization: Bearer {token}
Content-Type: application/json

{
  "status": 1
}

# 删除订单（需要 user:add 权限）
DELETE http://localhost:8082/orders/1
Authorization: Bearer {token}
```

## 权限说明

- 普通用户：只能查看订单
- admin 角色：可以创建订单
- 拥有 user:update 权限：可以更新订单
- 拥有 user:add 权限：可以删除订单

## 技术栈

- Spring Boot 3.x
- Sa-Token 1.45.0
- MyBatis-Flex
- PostgreSQL
- Redis
- Nacos
