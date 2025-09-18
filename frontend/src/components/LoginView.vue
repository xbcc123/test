<template>
  <div class="login-bg">
    <a-card class="login-card">
      <div class="flex flex-col items-center mb-6">
        <img src="/favicon.ico" class="w-12 h-12 mb-2" alt="logo" />
        <a-typography-title level="2" style="color:#1e40af;">欢迎登录宠物之家</a-typography-title>
      </div>
      <a-alert v-if="error" type="error" :message="error" show-icon style="margin-bottom: 16px;" />
      <a-form @submit.prevent="login" layout="vertical">
        <a-form-item label="用户名" required>
          <a-input v-model:value="username" placeholder="用户名" />
        </a-form-item>
        <a-form-item label="密码" required>
          <a-input v-model:value="password" type="password" placeholder="密码" />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" html-type="submit" :loading="loading" block>登录</a-button>
        </a-form-item>
      </a-form>
      <div class="flex justify-between mt-6 text-sm text-blue-600">
        <a href="#" @click.prevent="showRegister = true">注册</a>
        <a href="#" @click.prevent="showReset = true">找回密码</a>
      </div>
    </a-card>
    <!-- 注册弹窗 -->
    <a-modal v-model:open="showRegister" title="注册账号" @cancel="showRegister = false" ok-button-props="{ style: { display: 'none' } }" cancel-text="关闭">
      <a-alert v-if="registerError" type="error" :message="registerError" show-icon style="margin-bottom: 12px;" />
      <a-form @submit.prevent="register" layout="vertical">
        <a-form-item label="用户名" required>
          <a-input v-model:value="reg.username" placeholder="用户名" />
        </a-form-item>
        <a-form-item label="密码" required>
          <a-input v-model:value="reg.password" type="password" placeholder="密码" />
        </a-form-item>
        <a-form-item label="确认密码" required>
          <a-input v-model:value="reg.confirm" type="password" placeholder="确认密码" />
        </a-form-item>
        <a-form-item label="昵称">
          <a-input v-model:value="reg.nickname" placeholder="昵称" />
        </a-form-item>
        <a-form-item label="邮箱">
          <a-input v-model:value="reg.email" placeholder="邮箱" />
        </a-form-item>
        <a-form-item label="手机号">
          <a-input v-model:value="reg.phone" placeholder="手机号" />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" html-type="submit" block>注册</a-button>
          <a-button style="margin-top:8px;" block @click="showRegister = false">取消</a-button>
        </a-form-item>
      </a-form>
    </a-modal>
    <!-- 找回密码弹窗 -->
    <a-modal v-model:open="showReset" title="找回密码" @cancel="showReset = false" ok-button-props="{ style: { display: 'none' } }" cancel-text="关闭">
      <a-alert v-if="resetError" type="error" :message="resetError" show-icon style="margin-bottom: 12px;" />
      <a-form @submit.prevent="resetPassword" layout="vertical">
        <a-form-item label="用户名" required>
          <a-input v-model:value="reset.username" placeholder="用户名" />
        </a-form-item>
        <a-form-item label="新密码" required>
          <a-input v-model:value="reset.newPassword" type="password" placeholder="新密码" />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" html-type="submit" block>重置密码</a-button>
          <a-button style="margin-top:8px;" block @click="showReset = false">取消</a-button>
        </a-form-item>
      </a-form>
    </a-modal>
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
const loading = ref(false)
const router = useRouter()

const showRegister = ref(false)
const showReset = ref(false)
const registerError = ref('')
const resetError = ref('')
const reg = ref({ username: '', password: '', confirm: '', nickname: '', email: '', phone: '' })
const reset = ref({ username: '', newPassword: '' })

const login = async () => {
  error.value = ''
  loading.value = true
  try {
    const res = await axios.post('/login', { username: username.value, password: password.value })
    console.log(res)
    if (res && res.token) {
      setToken(res.token)
      // 缓存用户信息
      if (res.userId) localStorage.setItem('userId', res.userId)
      if (res.username) localStorage.setItem('username', res.username)
      if (res.role) localStorage.setItem('role', res.role)
      if (res.nickname) localStorage.setItem('nickname', res.nickname)
      router.push('/')
    } else {
      error.value = res.error || '登录失败'
    }
  } catch (e) {
    error.value = '网络错误'
  } finally {
    loading.value = false
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
.login-bg {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #eff6ff 0%, #fff 100%);
}
.login-card {
  width: 100%;
  max-width: 400px;
  padding: 32px 24px;
}
</style>
