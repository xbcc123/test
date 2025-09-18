<template>
  <div>
    <div class="error" v-if="error">{{ error }}</div>
    <form @submit.prevent="saveArticle">
      <input type="hidden" v-model="form.id">
      <div class="form-group">
        <label>标题</label>
        <input v-model="form.title" required />
      </div>
      <div class="form-group">
        <label>分类</label>
        <input v-model="form.category" required />
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
        <tr><th>ID</th><th>标题</th><th>分类</th><th>内容</th><th>操作</th></tr>
      </thead>
      <tbody>
        <tr v-for="a in articles" :key="a.id">
          <td>{{a.id}}</td><td>{{a.title}}</td><td>{{a.category}}</td><td>{{a.content}}</td>
          <td>
            <button @click="editArticle(a)">编辑</button>
            <button @click="deleteArticle(a.id)">删除</button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from '../../utils/axios'

const articles = ref([])
const error = ref('')
const form = ref({ id: '', title: '', category: '', content: '' })

function loadArticles() {
  error.value = ''
  axios.get('/articles').then(res => {
    articles.value = res || []
  }).catch(() => { error.value = '加载失败' })
}
function saveArticle() {
  error.value = ''
  const data = { ...form.value }
  const method = data.id ? 'put' : 'post'
  const url = data.id ? `/articles/${data.id}` : '/articles'
  axios[method](url, data).then(() => {
    resetForm()
    loadArticles()
  }).catch(() => { error.value = '保存失败' })
}
function editArticle(a) {
  form.value = { ...a }
}
function resetForm() {
  form.value = { id: '', title: '', category: '', content: '' }
}
function deleteArticle(id) {
  if (!confirm('确定要删除这篇文章吗？')) return
  axios.delete(`/articles/${id}`).then(() => {
    loadArticles()
  }).catch(() => { error.value = '删除失败' })
}
onMounted(loadArticles)
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

