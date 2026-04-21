<template>
  <n-layout has-sider class="layout">
    <n-layout-sider bordered collapse-mode="width" :collapsed-width="64" :width="200" class="sider">
      <div class="logo">订单管理系统</div>
      <n-menu :options="menuOptions" :value="currentRoute" @update:value="handleMenuClick" />
    </n-layout-sider>
    <n-layout-content class="content">
      <n-page-header title="订单管理">
        <template #extra>
          <n-space>
            <span>欢迎，{{ authStore.userInfo?.nickname }}</span>
            <n-button size="small" @click="handleLogout">退出登录</n-button>
          </n-space>
        </template>
      </n-page-header>
      <div class="page-content">
        <router-view />
      </div>
    </n-layout-content>
  </n-layout>
</template>

<script setup lang="ts">
import { computed, h } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import type { MenuOption } from 'naive-ui'
import { NIcon } from 'naive-ui'
import { ListOutline, LogOutOutline } from '@vicons/ionicons5'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const currentRoute = computed(() => route.name as string)

const renderIcon = (icon: any) => () => h(NIcon, null, { default: () => h(icon) })

const menuOptions: MenuOption[] = [
  {
    label: '订单列表',
    key: 'Orders',
    icon: renderIcon(ListOutline),
  },
]

const handleMenuClick = (key: string) => {
  router.push({ name: key })
}

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.layout {
  height: 100vh;
}

.sider {
  display: flex;
  flex-direction: column;
}

.logo {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: bold;
  color: #fff;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.content {
  padding: 16px;
}

.page-content {
  margin-top: 16px;
}
</style>
