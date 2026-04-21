import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false },
  },
  {
    path: '/',
    name: 'Layout',
    redirect: '/orders',
    component: () => import('@/layouts/BasicLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: 'orders',
        name: 'Orders',
        component: () => import('@/views/order/OrderList.vue'),
        meta: { title: '订单管理', requiresAuth: true },
      },
    ],
  },
  {
    path: '/oauth2/callback',
    name: 'OAuth2Callback',
    component: () => import('@/views/OAuth2Callback.vue'),
    meta: { requiresAuth: false },
  },
]

export const router = createRouter({
  history: createWebHistory(),
  routes,
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  
  // 恢复 token
  if (!authStore.token) {
    authStore.restoreToken()
  }

  const requiresAuth = to.meta.requiresAuth !== false
  
  if (requiresAuth && !authStore.isLoggedIn) {
    // 未登录，跳转到 OAuth2 授权页面
    const oauth2AuthUrl = `http://localhost:9000/oauth2/authorize?response_type=code&client_id=order-app&redirect_uri=${encodeURIComponent(window.location.origin + '/oauth2/callback')}&scope=read`
    window.location.href = oauth2AuthUrl
    return
  }

  next()
})

export default router
