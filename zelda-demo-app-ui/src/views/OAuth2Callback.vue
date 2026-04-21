<template>
  <div class="oauth-callback">
    <n-spin size="large" description="登录中...">
      <p>正在处理 OAuth2 授权回调</p>
    </n-spin>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { http } from '@/api/request'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

onMounted(async () => {
  const code = route.query.code as string
  
  if (!code) {
    console.error('未获取到授权码')
    router.push('/login')
    return
  }

  try {
    // 从 sessionStorage 获取 code_verifier
    const codeVerifier = sessionStorage.getItem('code_verifier')
    
    if (!codeVerifier) {
      console.error('未找到 code_verifier，PKCE 验证失败')
      router.push('/login')
      return
    }

    // 使用授权码换取 token（携带 code_verifier）
    const response = await http.post('/oauth/token', null, {
      params: {
        grant_type: 'authorization_code',
        code,
        redirect_uri: window.location.origin + '/oauth2/callback',
        client_id: 'order-app',
        code_verifier: codeVerifier,
      },
    })

    // 清除存储的 code_verifier
    sessionStorage.removeItem('code_verifier')

    if (response.code === 200 || response.data?.access_token) {
      const token = response.data?.access_token || response.access_token
      authStore.setToken(token)
      
      // 获取用户信息（从 token 解析或调用接口）
      // 这里简化处理，实际项目中应该调用 /auth/userinfo 接口
      authStore.setUserInfo({
        id: 1,
        username: 'admin',
        nickname: '管理员',
        roles: ['admin'],
        permissions: ['user:add', 'user:update', 'order:*'],
      })
      
      router.push('/orders')
    } else {
      throw new Error('获取 token 失败')
    }
  } catch (error) {
    console.error('OAuth2 回调处理失败:', error)
    router.push('/login')
  }
})
</script>

<style scoped>
.oauth-callback {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}
</style>
