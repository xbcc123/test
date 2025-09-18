<template>
  <div>
    <div class="error" v-if="error">{{ error }}</div>
    <form @submit.prevent="saveEncyclopedia">
      <input type="hidden" v-model="form.id">
      <div class="form-group">
        <label>标题</label>
        <input v-model="form.title" required />
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
        <tr><th>ID</th><th>标题</th><th>类型</th><th>内容</th><th>操作</th></tr>
      </thead>
      <tbody>
        <tr v-for="e in encyclopedias" :key="e.id">
          <td>{{e.id}}</td><td>{{e.title}}</td><td>{{e.type}}</td><td>{{e.content}}</td>
          <td>
            <button @click="editEncyclopedia(e)">编辑</button>
            <button @click="deleteEncyclopedia(e.id)">删除</button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from '@/utils/axios'

const encyclopedias = ref([])
const error = ref('')
const form = ref({ id: '', title: '', type: '', content: '' })

function loadEncyclopedias() {
  error.value = ''
  axios.get('/encyclopedias').then(res => {
    encyclopedias.value = res || []
  }).catch(() => { error.value = '加载失败' })
}
function saveEncyclopedia() {
  error.value = ''
  const data = { ...form.value }
  const method = data.id ? 'put' : 'post'
  const url = data.id ? `/encyclopedias/${data.id}` : '/encyclopedias'
  axios[method](url, data).then(() => {
    resetForm()
    loadEncyclopedias()
  }).catch(() => { error.value = '保存失败' })
}
function editEncyclopedia(e) {
  form.value = { ...e }
}
function resetForm() {
  form.value = { id: '', title: '', type: '', content: '' }
}
function deleteEncyclopedia(id) {
  if (!confirm('确定要删除这条百科吗？')) return
  axios.delete(`/encyclopedias/${id}`).then(() => {
    loadEncyclopedias()
  }).catch(() => { error.value = '删除失败' })
}
onMounted(loadEncyclopedias)
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
