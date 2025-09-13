<template>
  <div class="container">
    <h2>登录</h2>
    <div class="error" v-if="error">{{ error }}</div>
    <div class="form-group">
      <label for="login-username">用户名</label>
      <input v-model="username" type="text" id="login-username" />
    </div>
    <div class="form-group">
      <label for="login-password">密码</label>
      <input v-model="password" type="password" id="login-password" />
    </div>
    <button @click="login">登录</button>
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
.container { max-width: 400px; margin: 40px auto; background: #fff; padding: 24px; border-radius: 8px; box-shadow: 0 2px 8px #0001; }
.error { color: #d00; margin-bottom: 12px; }
.form-group { margin-bottom: 16px; }
</style>

