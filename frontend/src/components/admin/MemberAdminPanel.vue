<template>
  <div>
    <div class="admin-actions">
      <input v-model="keyword" placeholder="搜索用户名/昵称" @keyup.enter="searchUsers" />
      <button @click="searchUsers">搜索</button>
      <button @click="showAdd = true">新增用户</button>
    </div>
    <div class="error" v-if="error">{{ error }}</div>
    <table class="admin-table">
      <thead>
        <tr>
          <th>用户名</th><th>昵称</th><th>邮箱</th><th>手机号</th><th>积分</th><th>角色</th><th>状态</th><th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="u in users" :key="u.username">
          <td>{{u.username}}</td><td>{{u.nickname}}</td><td>{{u.email}}</td><td>{{u.phone}}</td><td>{{u.score}}</td><td>{{u.role}}</td><td>{{u.status}}</td>
          <td>
            <button @click="editUser(u)">编辑</button>
            <button @click="toggleStatus(u)">{{u.status==='禁用'?'启用':'禁用'}}</button>
            <button @click="showResetPwd(u)">重置密码</button>
            <button @click="deleteUser(u.username)">删除</button>
          </td>
        </tr>
      </tbody>
    </table>
    <div class="pagination">
      <button @click="prevPage" :disabled="page===0">上一页</button>
      <span>第{{page+1}}页/共{{totalPages}}页</span>
      <button @click="nextPage" :disabled="page+1>=totalPages">下一页</button>
    </div>
    <!-- 编辑弹窗 -->
    <div v-if="showEdit" class="modal-bg">
      <div class="modal-card">
        <h3>编辑用户</h3>
        <form @submit.prevent="saveEdit">
          <input v-model="edit.nickname" placeholder="昵称" />
          <input v-model="edit.email" placeholder="邮箱" />
          <input v-model="edit.phone" placeholder="手机号" />
          <input v-model="edit.role" placeholder="角色" />
          <select v-model="edit.status"><option value="正常">正常</option><option value="禁用">禁用</option></select>
          <button type="submit">保存</button>
          <button type="button" @click="showEdit=false">取消</button>
        </form>
      </div>
    </div>
    <!-- 新增弹窗 -->
    <div v-if="showAdd" class="modal-bg">
      <div class="modal-card">
        <h3>新增用户</h3>
        <form @submit.prevent="saveAdd">
          <input v-model="add.username" placeholder="用户名" required />
          <input v-model="add.password" type="password" placeholder="密码" required />
          <input v-model="add.nickname" placeholder="昵称" />
          <input v-model="add.email" placeholder="邮箱" />
          <input v-model="add.phone" placeholder="手机号" />
          <input v-model="add.role" placeholder="角色" />
          <select v-model="add.status"><option value="正常">正常</option><option value="禁用">禁用</option></select>
          <button type="submit">保存</button>
          <button type="button" @click="showAdd=false">取消</button>
        </form>
      </div>
    </div>
    <!-- 重置密码弹窗 -->
    <div v-if="showReset" class="modal-bg">
      <div class="modal-card">
        <h3>重置密码</h3>
        <form @submit.prevent="saveResetPwd">
          <input v-model="resetPwd" type="password" placeholder="新密码" required />
          <button type="submit">保存</button>
          <button type="button" @click="showReset=false">取消</button>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from '@/utils/axios'

const users = ref([])
const error = ref('')
const page = ref(0)
const size = ref(10)
const totalPages = ref(1)
const keyword = ref('')
const showEdit = ref(false)
const showAdd = ref(false)
const showReset = ref(false)
const edit = ref({})
const add = ref({ username: '', password: '', nickname: '', email: '', phone: '', role: '', status: '正常' })
const resetUser = ref(null)
const resetPwd = ref('')

function loadUsers() {
  error.value = ''
  axios.get('/users', { params: { page: page.value, size: size.value, keyword: keyword.value } }).then(res => {
    users.value = res.content || []
    totalPages.value = res.totalPages || 1
  }).catch(() => { error.value = '加载失败' })
}
function searchUsers() {
  page.value = 0
  loadUsers()
}
function prevPage() {
  if (page.value > 0) { page.value--; loadUsers() }
}
function nextPage() {
  if (page.value + 1 < totalPages.value) { page.value++; loadUsers() }
}
function deleteUser(username) {
  if (!confirm('确定要删除该用户吗？')) return
  axios.delete(`/users/${username}`).then(() => { loadUsers() }).catch(() => { error.value = '删除失败' })
}
function editUser(u) {
  edit.value = { ...u }
  showEdit.value = true
}
function saveEdit() {
  axios.put(`/users/${edit.value.username}`, edit.value).then(() => {
    showEdit.value = false
    loadUsers()
  }).catch(() => { error.value = '保存失败' })
}
function saveAdd() {
  axios.post('/users', add.value).then(() => {
    showAdd.value = false
    loadUsers()
  }).catch(() => { error.value = '新增失败' })
}
function toggleStatus(u) {
  const newStatus = u.status === '禁用' ? '正常' : '禁用'
  axios.put(`/users/${u.username}/status`, null, { params: { status: newStatus } }).then(() => { loadUsers() }).catch(() => { error.value = '操作失败' })
}
function showResetPwd(u) {
  resetUser.value = u
  resetPwd.value = ''
  showReset.value = true
}
function saveResetPwd() {
  axios.put(`/users/${resetUser.value.username}/reset-password`, null, { params: { newPassword: resetPwd.value } }).then(() => {
    showReset.value = false
    loadUsers()
  }).catch(() => { error.value = '重置失败' })
}
onMounted(loadUsers)
</script>

<style scoped>
.admin-actions { margin-bottom: 12px; }
.admin-actions input { padding: 4px 8px; margin-right: 8px; }
.admin-table { width: 100%; border-collapse: collapse; margin-top: 16px; }
th, td { border: 1px solid #ddd; padding: 8px; text-align: center; }
th { background: #f0f0f0; }
.error { color: #d00; margin-bottom: 12px; }
button { margin-right: 6px; }
.pagination { margin-top: 16px; text-align: center; }
.modal-bg { position: fixed; left: 0; top: 0; width: 100vw; height: 100vh; background: rgba(0,0,0,0.2); display: flex; align-items: center; justify-content: center; z-index: 1000; }
.modal-card { background: #fff; border-radius: 8px; box-shadow: 0 2px 8px #e3e3e3; padding: 24px 18px; min-width: 300px; }
.modal-card h3 { margin-bottom: 12px; }
.modal-card form { display: flex; flex-direction: column; }
.modal-card input, .modal-card select { margin-bottom: 10px; padding: 6px 8px; border: 1px solid #ccc; border-radius: 4px; }
.modal-card button { margin-bottom: 6px; }
</style>
