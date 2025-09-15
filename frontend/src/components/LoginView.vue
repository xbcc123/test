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
      <div class="login-links">
        <a @click="showRegister = true">注册</a> |
        <a @click="showReset = true">找回密码</a>
      </div>
    </div>
    <!-- 注册弹窗 -->
    <div v-if="showRegister" class="modal-bg">
      <div class="modal-card">
        <h3>注册账号</h3>
        <div class="error" v-if="registerError">{{ registerError }}</div>
        <form @submit.prevent="register">
          <input v-model="reg.username" placeholder="用户名" required />
          <input v-model="reg.password" type="password" placeholder="密码" required />
          <input v-model="reg.confirm" type="password" placeholder="确认密码" required />
          <input v-model="reg.nickname" placeholder="昵称" />
          <input v-model="reg.email" placeholder="邮箱" />
          <input v-model="reg.phone" placeholder="手机号" />
          <button type="submit">注册</button>
          <button type="button" @click="showRegister = false">取消</button>
        </form>
      </div>
    </div>
    <!-- 找回密码弹窗 -->
    <div v-if="showReset" class="modal-bg">
      <div class="modal-card">
        <h3>找回密码</h3>
        <div class="error" v-if="resetError">{{ resetError }}</div>
        <form @submit.prevent="resetPassword">
          <input v-model="reset.username" placeholder="用户名" required />
          <input v-model="reset.newPassword" type="password" placeholder="新密码" required />
          <button type="submit">重置密码</button>
          <button type="button" @click="showReset = false">取消</button>
        </form>
      </div>
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

const showRegister = ref(false)
const showReset = ref(false)
const registerError = ref('')
const resetError = ref('')
const reg = ref({ username: '', password: '', confirm: '', nickname: '', email: '', phone: '' })
const reset = ref({ username: '', newPassword: '' })

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

const register = async () => {
  registerError.value = ''
  if (!reg.value.username || !reg.value.password || !reg.value.confirm) {
    registerError.value = '请填写完整信息'
    return
  }
  if (reg.value.password !== reg.value.confirm) {
    registerError.value = '两次密码不一致'
    return
  }
  try {
    await axios.post('/register', reg.value)
    showRegister.value = false
    alert('注册成功，请登录')
  } catch (e) {
    registerError.value = e.response?.data || '注册失败'
  }
}

const resetPassword = async () => {
  resetError.value = ''
  if (!reset.value.username || !reset.value.newPassword) {
    resetError.value = '请填写完整信息'
    return
  }
  try {
    await axios.post('/reset-password', reset.value)
    showReset.value = false
    alert('密码重置成功，请登录')
  } catch (e) {
    resetError.value = e.response?.data || '重置失败'
  }
}
</script>

<style scoped>
.login-bg { min-height: 100vh; background: #f5f7fa; display: flex; align-items: center; justify-content: center; }
.login-card { background: #fff; border-radius: 8px; box-shadow: 0 2px 8px #e3e3e3; padding: 32px 24px; min-width: 320px; }
.login-header { text-align: center; margin-bottom: 18px; }
.login-logo { width: 48px; margin-bottom: 8px; }
.login-btn { margin-top: 8px; }
.error { color: #d00; margin-bottom: 12px; }
.login-links { margin-top: 12px; text-align: right; }
.login-links a { color: #1565c0; cursor: pointer; margin-left: 8px; }
.modal-bg { position: fixed; left: 0; top: 0; width: 100vw; height: 100vh; background: rgba(0,0,0,0.2); display: flex; align-items: center; justify-content: center; z-index: 1000; }
.modal-card { background: #fff; border-radius: 8px; box-shadow: 0 2px 8px #e3e3e3; padding: 24px 18px; min-width: 300px; }
.modal-card h3 { margin-bottom: 12px; }
.modal-card form { display: flex; flex-direction: column; }
.modal-card input { margin-bottom: 10px; padding: 6px 8px; border: 1px solid #ccc; border-radius: 4px; }
.modal-card button { margin-bottom: 6px; }
</style>
