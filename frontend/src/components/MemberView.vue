<template>
  <div>
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
      <button @click="showEdit = !showEdit" style="margin-top:16px;">{{ showEdit ? '取消编辑' : '编辑信息' }}</button>
      <form v-if="showEdit" class="edit-form" @submit.prevent="saveInfo">
        <div><label>昵称：</label><input v-model="edit.nickname" /></div>
        <div><label>邮箱：</label><input v-model="edit.email" /></div>
        <div><label>手机号：</label><input v-model="edit.phone" /></div>
        <div><label>头像：</label><input type="text" v-model="edit.avatar" placeholder="头像URL" /></div>
        <div><label>新密码：</label><input type="password" v-model="edit.password" placeholder="不修改可留空" /></div>
        <button type="submit">保存</button>
      </form>
      <div v-if="saveMsg" class="save-msg">{{ saveMsg }}</div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from '../utils/axios'

const user = ref(null)
const favorites = ref([])
const error = ref('')
const showEdit = ref(false)
const edit = ref({ nickname: '', email: '', phone: '', avatar: '', password: '' })
const saveMsg = ref('')

const loadMember = async () => {
  error.value = ''
  const username = localStorage.getItem('username')
  if (!username) return
  try {
    const res = await axios.get(`/users/${username}`)
    user.value = res
    edit.value.nickname = user.value.nickname || ''
    edit.value.email = user.value.email || ''
    edit.value.phone = user.value.phone || ''
    edit.value.avatar = user.value.avatar || ''
    edit.value.password = ''
  } catch (e) {
    error.value = '加载失败'
  }
  try {
    const res = await axios.get(`/users/${username}/favorites`)
    favorites.value = res || []
  } catch {}
}

const saveInfo = async () => {
  saveMsg.value = ''
  const username = localStorage.getItem('username')
  if (!username) return
  const data = {
    ...user.value,
    nickname: edit.value.nickname,
    email: edit.value.email,
    phone: edit.value.phone,
    avatar: edit.value.avatar
  }
  if (edit.value.password) data.password = edit.value.password
  try {
    await axios.put(`/users/${username}`, data)
    saveMsg.value = '保存成功'
    showEdit.value = false
    await loadMember()
  } catch (e) {
    saveMsg.value = '保存失败'
  }
}
onMounted(loadMember)
</script>

<style scoped>
.error { color: #1976d2; margin-bottom: 12px; }
.edit-form { margin-top: 16px; }
.edit-form div { margin-bottom: 8px; }
.edit-form label { display: inline-block; width: 60px; }
.save-msg { color: #1976d2; margin-top: 10px; }
</style>
