<template>
  <div>
    <div class="error" v-if="error">{{ error }}</div>
    <form @submit.prevent="savePost">
      <input type="hidden" v-model="form.id">
      <div class="form-group">
        <label>用户ID</label>
        <input v-model="form.userId" required />
      </div>
      <div class="form-group">
        <label>类型</label>
        <input v-model="form.type" required />
      </div>
      <div class="form-group">
        <label>内容</label>
        <textarea v-model="form.content" required></textarea>
      </div>
      <button type="submit">{{form.id ? '保存' : '新增'}}</button>
      <button type="button" @click="resetForm">重置</button>
    </form>
    <table class="admin-table">
      <thead>
        <tr><th>ID</th><th>用户ID</th><th>类型</th><th>内容</th><th>操作</th></tr>
      </thead>
      <tbody>
        <tr v-for="p in posts" :key="p.id">
          <td>{{p.id}}</td><td>{{p.userId}}</td><td>{{p.type}}</td><td>{{p.content}}</td>
          <td>
            <button @click="editPost(p)">编辑</button>
            <button @click="deletePost(p.id)">删除</button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from '@/utils/axios'

const posts = ref([])
const error = ref('')
const form = ref({ id: '', userId: '', type: '', content: '' })

function loadPosts() {
  error.value = ''
  axios.get('/posts').then(res => {
    posts.value = res.data || []
  }).catch(() => { error.value = '加载失败' })
}
function savePost() {
  error.value = ''
  const data = { ...form.value }
  const method = data.id ? 'put' : 'post'
  const url = data.id ? `/posts/${data.id}` : '/posts'
  axios[method](url, data).then(() => {
    resetForm()
    loadPosts()
  }).catch(() => { error.value = '保存失败' })
}
function editPost(p) {
  form.value = { ...p }
}
function resetForm() {
  form.value = { id: '', userId: '', type: '', content: '' }
}
function deletePost(id) {
  if (!confirm('确定要删除这条社区互动吗？')) return
  axios.delete(`/posts/${id}`).then(() => {
    loadPosts()
  }).catch(() => { error.value = '删除失败' })
}
onMounted(loadPosts)
</script>

<style scoped>
.form-group { margin-bottom: 16px; }
label { display: block; margin-bottom: 4px; }
input, textarea { width: 100%; padding: 8px; box-sizing: border-box; }
.admin-table { width: 100%; border-collapse: collapse; margin-top: 16px; }
th, td { border: 1px solid #ddd; padding: 8px; text-align: center; }
th { background: #f0f0f0; }
.error { color: #d00; margin-bottom: 12px; }
button { margin-right: 6px; }
</style>

