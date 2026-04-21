<template>
  <div class="login-container">
    <n-card title="订单管理系统登录" class="login-card">
      <p>正在跳转到 OAuth2 授权页面...</p>
      <n-button type="primary" @click="goToOAuth2">立即登录</n-button>
    </n-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'

const goToOAuth2 = () => {
  const oauth2AuthUrl = `http://localhost:9000/oauth2/authorize?response_type=code&client_id=order-app&redirect_uri=${encodeURIComponent(window.location.origin + '/oauth2/callback')}&scope=read`
  window.location.href = oauth2AuthUrl
}

onMounted(() => {
  // 自动跳转
  setTimeout(goToOAuth2, 500)
})
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  width: 400px;
  text-align: center;
}
</style>
