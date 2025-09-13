<template>
  <div>
    <div class="error" v-if="error">{{ error }}</div>
    <table class="admin-table">
      <thead>
        <tr><th>ID</th><th>用户名</th><th>积分</th><th>角色</th><th>操作</th></tr>
      </thead>
      <tbody>
        <tr v-for="u in users" :key="u.id">
          <td>{{u.id}}</td><td>{{u.username}}</td><td>{{u.score}}</td><td>{{u.role}}</td>
          <td>
            <button @click="deleteUser(u.id)">删除</button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from '@/utils/axios'

const users = ref([])
const error = ref('')

function loadUsers() {
  error.value = ''
  axios.get('/users').then(res => {
    users.value = res.data || []
  }).catch(() => { error.value = '加载失败' })
}
function deleteUser(id) {
  if (!confirm('确定要删除该用户吗？')) return
  axios.delete(`/users/${id}`).then(() => {
    loadUsers()
  }).catch(() => { error.value = '删除失败' })
}
onMounted(loadUsers)
</script>

<style scoped>
.admin-table { width: 100%; border-collapse: collapse; margin-top: 16px; }
th, td { border: 1px solid #ddd; padding: 8px; text-align: center; }
th { background: #f0f0f0; }
.error { color: #d00; margin-bottom: 12px; }
button { margin-right: 6px; }
</style>

