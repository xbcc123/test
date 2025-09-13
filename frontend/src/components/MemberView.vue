<template>
  <div class="container">
    <h2>会员中心</h2>
    <div class="error" v-if="error">{{ error }}</div>
    <div v-if="user">
      <div id="member-info">
        <b>用户名：</b>{{ user.username }}<br />
        <b>积分：</b>{{ user.score || 0 }}<br />
        <b>达人称号：</b>{{ user.role || '' }}
      </div>
      <div id="member-favorites" style="margin-top:12px;">
        <b>收藏：</b>{{ favorites && favorites.length ? favorites.join(', ') : '无' }}
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from '../utils/axios'

const user = ref(null)
const favorites = ref([])
const error = ref('')

const loadMember = async () => {
  error.value = ''
  const username = localStorage.getItem('username')
  if (!username) return
  try {
    const res = await axios.get(`/users/${username}`)
    user.value = res.data
  } catch (e) {
    error.value = '加载失败'
  }
  try {
    const res = await axios.get(`/users/${username}/favorites`)
    favorites.value = res.data || []
  } catch {}
}
onMounted(loadMember)
</script>

<style scoped>
.container { max-width: 700px; margin: 40px auto; background: #fff; padding: 24px; border-radius: 8px; box-shadow: 0 2px 8px #0001; }
.error { color: #d00; margin-bottom: 12px; }
</style>

