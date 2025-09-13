<template>
  <div>
    <div class="error" v-if="error">{{ error }}</div>
    <form @submit.prevent="saveHospital">
      <input type="hidden" v-model="form.id">
      <div class="form-group">
        <label>名称</label>
        <input v-model="form.name" required />
      </div>
      <div class="form-group">
        <label>地址</label>
        <input v-model="form.address" required />
      </div>
      <div class="form-group">
        <label>电话</label>
        <input v-model="form.phone" required />
      </div>
      <div class="form-group">
        <label>评分</label>
        <input v-model.number="form.rating" type="number" min="0" max="5" step="0.1" />
      </div>
      <div class="form-group">
        <label>描述</label>
        <input v-model="form.description" />
      </div>
      <button type="submit">{{form.id ? '保存' : '新增'}}</button>
      <button type="button" @click="resetForm">重置</button>
    </form>
    <table class="admin-table">
      <thead>
        <tr><th>ID</th><th>名称</th><th>地址</th><th>电话</th><th>评分</th><th>描述</th><th>操作</th></tr>
      </thead>
      <tbody>
        <tr v-for="h in hospitals" :key="h.id">
          <td>{{h.id}}</td><td>{{h.name}}</td><td>{{h.address}}</td><td>{{h.phone}}</td><td>{{h.rating}}</td><td>{{h.description}}</td>
          <td>
            <button @click="editHospital(h)">编辑</button>
            <button @click="deleteHospital(h.id)">删除</button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from '@/utils/axios'

const hospitals = ref([])
const error = ref('')
const form = ref({ id: '', name: '', address: '', phone: '', rating: '', description: '' })

function loadHospitals() {
  error.value = ''
  axios.get('/hospitals').then(res => {
    hospitals.value = res.data || []
  }).catch(() => { error.value = '加载失败' })
}
function saveHospital() {
  error.value = ''
  const data = { ...form.value }
  const method = data.id ? 'put' : 'post'
  const url = data.id ? `/hospitals/${data.id}` : '/hospitals'
  axios[method](url, data).then(() => {
    resetForm()
    loadHospitals()
  }).catch(() => { error.value = '保存失败' })
}
function editHospital(h) {
  form.value = { ...h }
}
function resetForm() {
  form.value = { id: '', name: '', address: '', phone: '', rating: '', description: '' }
}
function deleteHospital(id) {
  if (!confirm('确定要删除这家医院吗？')) return
  axios.delete(`/hospitals/${id}`).then(() => {
    loadHospitals()
  }).catch(() => { error.value = '删除失败' })
}
onMounted(loadHospitals)
</script>

<style scoped>
.form-group { margin-bottom: 16px; }
label { display: block; margin-bottom: 4px; }
input { width: 100%; padding: 8px; box-sizing: border-box; }
.admin-table { width: 100%; border-collapse: collapse; margin-top: 16px; }
th, td { border: 1px solid #ddd; padding: 8px; text-align: center; }
th { background: #f0f0f0; }
.error { color: #d00; margin-bottom: 12px; }
button { margin-right: 6px; }
</style>

