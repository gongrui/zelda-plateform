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

// 生成随机字符串作为 code_verifier
function generateCodeVerifier(): string {
  const array = new Uint8Array(32)
  crypto.getRandomValues(array)
  return Array.from(array, byte => byte.toString(16).padStart(2, '0')).join('')
}

// 使用 SHA-256 生成 code_challenge
async function generateCodeChallenge(verifier: string): Promise<string> {
  const encoder = new TextEncoder()
  const data = encoder.encode(verifier)
  const digest = await crypto.subtle.digest('SHA-256', data)
  return Array.from(new Uint8Array(digest))
    .map(b => b.toString(16).padStart(2, '0'))
    .join('')
    .replace(/\+/g, '-')
    .replace(/\//g, '_')
    .replace(/=/g, '')
}

const goToOAuth2 = async () => {
  const codeVerifier = generateCodeVerifier()
  const codeChallenge = await generateCodeChallenge(codeVerifier)
  
  // 将 code_verifier 存储到 sessionStorage，供回调时使用
  sessionStorage.setItem('code_verifier', codeVerifier)
  
  const oauth2AuthUrl = `http://localhost:18000/oauth2/authorize?response_type=code&client_id=order-app&redirect_uri=${encodeURIComponent(window.location.origin + '/oauth2/callback')}&scope=read&code_challenge=${codeChallenge}&code_challenge_method=S256`
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
