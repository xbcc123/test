<template>
  <div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-50 to-white">
    <Card customClass="w-full max-w-md p-8">
      <div class="flex flex-col items-center mb-6">
        <img src="/favicon.ico" class="w-12 h-12 mb-2" alt="logo" />
        <h2 class="text-2xl font-bold text-blue-900">欢迎登录宠物之家</h2>
      </div>
      <div v-if="error" class="text-red-500 text-sm mb-4">{{ error }}</div>
      <form @submit.prevent="login" class="space-y-4">
        <Input v-model="username" placeholder="用户名" />
        <Input v-model="password" type="password" placeholder="密码" />
        <Button block :loading="loading" type="submit">登录</Button>
      </form>
      <div class="flex justify-between mt-6 text-sm text-blue-600">
        <a href="#" @click.prevent="showRegister = true">注册</a>
        <a href="#" @click.prevent="showReset = true">找回密码</a>
      </div>
    </Card>
    <!-- 注册弹窗 -->
    <Modal :visible="showRegister" @close="showRegister = false">
      <h3 class="text-xl font-bold mb-4">注册账号</h3>
      <div v-if="registerError" class="text-red-500 text-sm mb-2">{{ registerError }}</div>
      <form @submit.prevent="register" class="space-y-3">
        <Input v-model="reg.username" placeholder="用户名" required />
        <Input v-model="reg.password" type="password" placeholder="密码" required />
        <Input v-model="reg.confirm" type="password" placeholder="确认密码" required />
        <Input v-model="reg.nickname" placeholder="昵称" />
        <Input v-model="reg.email" placeholder="邮箱" />
        <Input v-model="reg.phone" placeholder="手机号" />
        <div class="flex gap-2 mt-2">
          <Button type="submit" block>注册</Button>
          <Button variant="secondary" type="button" block @click="showRegister = false">取消</Button>
        </div>
      </form>
    </Modal>
    <!-- 找回密码弹窗 -->
    <Modal :visible="showReset" @close="showReset = false">
      <h3 class="text-xl font-bold mb-4">找回密码</h3>
      <div v-if="resetError" class="text-red-500 text-sm mb-2">{{ resetError }}</div>
      <form @submit.prevent="resetPassword" class="space-y-3">
        <Input v-model="reset.username" placeholder="用户名" required />
        <Input v-model="reset.newPassword" type="password" placeholder="新密码" required />
        <div class="flex gap-2 mt-2">
          <Button type="submit" block>重置密码</Button>
          <Button variant="secondary" type="button" block @click="showReset = false">取消</Button>
        </div>
      </form>
    </Modal>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { setToken } from '../utils/auth'
import axios from '../utils/axios'
import Card from './ui/Card.vue'
import Input from './ui/Input.vue'
import Button from './ui/Button.vue'
import Modal from './ui/Modal.vue'

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
    if (res.data && res.data.token) {
      setToken(res.data.token)
      localStorage.setItem('username', username.value)
      router.push('/')
    } else {
      error.value = res.data.error || '登录失败'
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
</style>
