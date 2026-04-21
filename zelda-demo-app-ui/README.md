# zelda-demo-app-ui

订单管理演示应用前端 - 基于 Vue3 + NaiveUI + TypeScript

## 技术栈

- **Vue 3.5+** - 渐进式 JavaScript 框架
- **TypeScript** - 类型安全的 JavaScript 超集
- **Vite 6+** - 下一代前端构建工具
- **Naive UI** - 完整、可调校的 Vue 3 组件库
- **Pinia** - Vue 官方状态管理库
- **Vue Router 4** - Vue.js 官方路由管理器
- **Axios** - HTTP 客户端

## 功能特性

### OAuth2 认证集成
- 支持 Sa-Token OAuth2 授权码模式
- 自动 Token 管理和刷新
- 基于角色的权限控制
- 登录状态持久化

### 订单管理
- 订单列表查询（支持分页、搜索、筛选）
- 新建订单（需要 `order:create` 权限）
- 编辑订单（需要 `order:update` 权限）
- 删除订单（需要 `order:delete` 权限）

## 快速开始

### 环境要求

- Node.js >= 18.0.0
- npm >= 9.0.0

### 安装依赖

```bash
npm install
```

### 启动开发服务器

```bash
npm run dev
```

访问 http://localhost:3000

### 构建生产版本

```bash
npm run build
```

### 预览生产构建

```bash
npm run preview
```

## 项目结构

```
zelda-demo-app-ui/
├── src/
│   ├── api/              # API 接口定义
│   │   ├── request.ts    # Axios 封装
│   │   └── order.ts      # 订单相关接口
│   ├── assets/           # 静态资源
│   ├── components/       # 公共组件
│   ├── layouts/          # 布局组件
│   │   └── BasicLayout.vue
│   ├── router/           # 路由配置
│   │   └── index.ts
│   ├── stores/           # Pinia 状态管理
│   │   └── auth.ts       # 认证状态
│   ├── views/            # 页面视图
│   │   ├── Login.vue     # 登录页
│   │   ├── OAuth2Callback.vue  # OAuth2 回调页
│   │   └── order/        # 订单相关页面
│   │       └── OrderList.vue
│   ├── App.vue           # 根组件
│   └── main.ts           # 入口文件
├── index.html
├── package.json
├── tsconfig.json
├── tsconfig.node.json
└── vite.config.ts
```

## OAuth2 认证流程

1. 用户访问应用，未登录时自动跳转到 UAA 授权页面
2. 用户在 UAA 完成登录和授权
3. UAA 重定向回 `/oauth2/callback`，携带授权码
4. 前端使用授权码换取 Access Token
5. 后续请求在 Header 中携带 Token：`Authorization: Bearer <token>`

## 权限控制

前端通过判断用户权限来控制按钮显示：

```typescript
const hasPermission = (permission: string) => {
  const permissions = authStore.permissions || []
  return permissions.includes('*') || 
         permissions.includes(permission) || 
         permissions.some(p => p.endsWith(':*'))
}
```

后端通过 Sa-Token 注解进行接口级权限校验：

- `@SaCheckLogin` - 登录校验
- `@SaCheckRole("admin")` - 角色校验
- `@SaCheckPermission("user:add")` - 权限校验

## 代理配置

开发环境下，通过 Vite 代理将 API 请求转发到 Gateway：

```typescript
// vite.config.ts
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080', // Gateway 地址
      changeOrigin: true,
    },
  },
}
```

## 环境变量

创建 `.env` 文件配置环境变量：

```env
VITE_API_BASE_URL=/api
VITE_OAUTH2_AUTH_URL=http://localhost:9000/oauth2/authorize
VITE_OAUTH2_TOKEN_URL=http://localhost:8080/api/oauth/token
VITE_CLIENT_ID=order-app
```

## License

MIT
