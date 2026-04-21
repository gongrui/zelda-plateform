# Zelda Platform - 统一认证授权平台

基于 Spring Cloud + Sa-Token 的微服务统一认证授权平台，支持 OAuth2.0 协议，提供完整的 RBAC 权限管理体系。

## 项目架构

```
zelda/
├── zelda-dependencies/          # 统一版本管理（BOM）
├── zelda-fw/                    # 框架核心模块
│   ├── zelda-fw-core/           # 核心工具类
│   ├── zelda-common-spring-boot-starter/    # 通用 starter
│   ├── zelda-db-spring-boot-starter/        # 数据库 starter
│   ├── zelda-security-spring-boot-starter/  # 安全认证 starter
│   └── zelda-code-spring-boot-starter/      # 代码生成器
├── zelda-plateform-gateway/     # API 网关（WebFlux + Sa-Token）
├── zelda-plateform-uaa/         # 统一认证中心（OAuth2 Server）
├── zelda-demo-app/              # 订单演示应用（后端）
└── zelda-demo-app-ui/           # 订单演示应用（前端 Vue3）
```

## 技术栈

### 后端技术栈
- **Java 21** - LTS 版本
- **Spring Boot 4.0.3** - 应用框架
- **Spring Cloud 2025.1.0** - 微服务框架
- **Spring Cloud Alibaba 2025.1.0.0** - 阿里云微服务生态
- **Sa-Token 1.45.0** - 轻量级权限认证框架
- **MyBatis-Flex 1.11.6** - ORM 框架
- **PostgreSQL** - 关系型数据库
- **Redis** - 缓存和会话存储
- **Nacos** - 配置中心和注册中心

### 前端技术栈
- **Vue 3.5+** - 渐进式框架
- **TypeScript 5.7+** - 类型安全
- **Vite 6+** - 构建工具
- **Naive UI 2.41+** - 组件库
- **Pinia 2.3+** - 状态管理
- **Vue Router 4.5+** - 路由管理

## 核心功能

### 1. OAuth2.0 认证服务器（UAA）
- 支持授权码模式（Authorization Code）
- 支持客户端凭证模式（Client Credentials）
- 支持密码模式（Password）
- 支持刷新令牌（Refresh Token）
- PKCE 扩展支持
- JWT Token 签发和验证

### 2. 统一网关认证（Gateway）
- 全局 Token 验证过滤器
- 用户信息透传到下游服务
- 动态路由配置
- 限流和熔断
- 统一异常处理

### 3. 多应用权限管理
- **应用管理**：注册和管理不同的应用程序
- **组织架构**：部门层级结构管理
- **人员管理**：用户与部门关联
- **角色管理**：角色定义和分配
- **权限管理**：细粒度权限控制（菜单、按钮、API）
- **应用维度隔离**：不同应用独立权限体系

### 4. Sa-Token 注解鉴权
```java
@SaCheckLogin              // 登录校验
@SaCheckRole("admin")      // 角色校验
@SaCheckPermission("user:add")  // 权限校验
@SaCheckPermission("order:*")   // 通配符权限
```

## 快速开始

### 环境准备
1. JDK 21+
2. PostgreSQL 14+
3. Redis 7+
4. Node.js 18+
5. Nacos 2.x

### 数据库初始化
```sql
-- 执行 zelda-plateform-uaa/src/main/resources/db/schema.sql
-- 执行 zelda-plateform-uaa/src/main/resources/db/data.sql
```

### 启动顺序
1. 启动 Nacos
2. 启动 Redis
3. 启动 UAA 服务（端口：18001）
4. 启动 Gateway 服务（端口：18000）
5. 启动 Demo 应用（端口：18002）
6. 启动前端开发服务器

```bash
# 前端启动
cd zelda-demo-app-ui
npm install
npm run dev
```

### 访问地址
- **前端应用**：http://localhost:3000
- **API 网关**：http://localhost:18000
- **UAA 服务**：http://localhost:18001
- **Swagger 文档**：http://localhost:18000/doc.html

## OAuth2 认证流程

```
┌─────────────┐      ┌─────────────┐      ┌─────────────┐      ┌─────────────┐
│   浏览器     │      │   Gateway   │      │     UAA     │      │  Demo App   │
└──────┬──────┘      └──────┬──────┘      └──────┬──────┘      └──────┬──────┘
       │                    │                    │                    │
       │ 1.访问应用          │                    │                    │
       │───────────────────>│                    │                    │
       │                    │                    │                    │
       │ 2.未登录，重定向到 OAuth2 授权             │                    │
       │<───────────────────│                    │                    │
       │                    │                    │                    │
       │ 3.跳转到 UAA 授权页面                       │                    │
       │────────────────────────────────────────>│                    │
       │                    │                    │                    │
       │ 4.用户登录并授权                           │                    │
       │<────────────────────────────────────────│                    │
       │                    │                    │                    │
       │ 5.携带 code 回调                          │                    │
       │────────────────────────────────────────>│                    │
       │                    │                    │                    │
       │ 6.code 换取 token                         │                    │
       │───────────────────>│───────────────────>│                    │
       │                    │                    │                    │
       │ 7.返回 access_token                      │                    │
       │<───────────────────│<───────────────────│                    │
       │                    │                    │                    │
       │ 8.携带 token 请求业务接口                  │                    │                    │
       │───────────────────>│                    │                    │
       │                    │                    │                    │
       │ 9.Token 验证，透传用户信息                 │                    │                    │
       │                    │────────────────────────────────────────>│
       │                    │                    │                    │
       │ 10.返回业务数据                        │                    │                    │
       │<───────────────────│<────────────────────────────────────────│
       │                    │                    │                    │
```

## 权限模型

### RBAC 模型
```
用户 (User) ──→ 角色 (Role) ──→ 权限 (Permission)
    ↓                              ↓
 部门 (Dept)                    资源 (Resource)
    ↓                              ↓
 应用 (App) ──────────────────────┘
```

### 权限标识规范
- **格式**：`{模块}:{操作}`
- **示例**：
  - `user:add` - 用户新增
  - `user:update` - 用户修改
  - `user:delete` - 用户删除
  - `order:*` - 订单所有权限
  - `*` - 超级管理员

## API 示例

### 获取 Token
```bash
POST /oauth/token?grant_type=authorization_code&code=xxx&redirect_uri=http://localhost:3000/oauth2/callback&client_id=order-app
```

### 创建订单（需要 admin 角色）
```bash
POST /api/orders
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "orderNo": "ORD20250101001",
  "customerName": "张三",
  "amount": 199.99,
  "status": "PENDING"
}
```

### 删除订单（需要 user:add 权限）
```bash
DELETE /api/orders/1
Authorization: Bearer <access_token>
```

## 项目约定

### 代码规范
- 遵循阿里巴巴 Java 开发手册
- 使用 Lombok 简化代码
- 统一异常处理
- 统一响应格式

### 目录结构
```
src/main/java/com/blue/
├── controller/     # 控制器层
├── service/        # 服务层
│   └── impl/       # 服务实现
├── mapper/         # 数据访问层
├── entity/         # 实体类
├── dto/            # 数据传输对象
├── config/         # 配置类
└── constant/       # 常量定义
```

### Git 提交规范
- `feat`: 新功能
- `fix`: 修复 bug
- `docs`: 文档更新
- `style`: 代码格式调整
- `refactor`: 重构
- `test`: 测试相关
- `chore`: 构建/工具链相关

## 开发计划

- [x] 死代码清理和编码规范统一
- [x] Sa-Token 集成
- [x] OAuth2 认证服务器
- [x] Gateway 统一认证过滤
- [x] 多应用权限管理模型
- [x] 订单演示应用（后端）
- [x] 订单演示应用（前端）
- [ ] 完善单元测试
- [ ] 性能优化
- [ ] 监控告警集成
- [ ] 审计日志

## License

MIT License
