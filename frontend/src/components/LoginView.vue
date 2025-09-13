<template>
  <div class="login-bg">
    <div class="login-card">
      <div class="login-header">
        <img src="/favicon.ico" class="login-logo" alt="logo" />
        <h2>欢迎登录宠物之家</h2>
      </div>
      <div class="error" v-if="error">{{ error }}</div>
      <a-form @submit.prevent="login">
        <a-form-item label="用户名">
          <a-input v-model:value="username" placeholder="请输入用户名" size="large" />
        </a-form-item>
        <a-form-item label="密码">
          <a-input-password v-model:value="password" placeholder="请输入密码" size="large" />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" html-type="submit" block size="large" class="login-btn">登录</a-button>
        </a-form-item>
      </a-form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { setToken } from '../utils/auth'
import axios from '../utils/axios'

const username = ref('')
const password = ref('')
const error = ref('')
const router = useRouter()

const login = async () => {
  error.value = ''
  try {
    const res = await axios.post('/login', { username: username.value, password: password.value })
    if (res.data && res.data.token) {
      setToken(res.data.token)
      localStorage.setItem('username', username.value)
      router.push('/')
    } else {
      error.value = res.data.error || '登录失败'
    }
  } catch (e) {
    error.value = '网络错误'
  }
}
</script>

<style scoped>
.login-bg {
  min-height: 100vh;
  background: linear-gradient(135deg, #eaf6fb 0%, #c9e7fa 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}
.login-card {
  width: 380px;
  padding: 32px 24px 24px 24px;
  border-radius: 20px;
  border: none;
}
.login-header {
  text-align: center;
  margin-bottom: 24px;
}
.login-logo {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  margin-bottom: 8px;
}
.login-btn {
  background: linear-gradient(90deg, #2196f3 0%, #90caf9 100%);
  border: none;
  color: #fff;
  font-weight: bold;
  letter-spacing: 2px;
  box-shadow: 0 2px 8px 0 rgba(33,150,243,0.10);
}
.login-btn:hover {
  background: linear-gradient(90deg, #1976d2 0%, #64b5f6 100%);
  color: #fff;
}
.error { color: #1976d2; margin-bottom: 12px; text-align: center; }
</style>
