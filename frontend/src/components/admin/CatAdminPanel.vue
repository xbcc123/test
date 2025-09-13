<template>
  <div>
    <div class="error" v-if="error">{{ error }}</div>
    <form @submit.prevent="saveCat">
      <input type="hidden" v-model="form.id">
      <div class="form-group">
        <label>名字</label>
        <input v-model="form.name" required />
      </div>
      <div class="form-group">
        <label>年龄</label>
        <input v-model.number="form.age" type="number" min="0" required />
      </div>
      <div class="form-group">
        <label>品种</label>
        <input v-model="form.breed" />
      </div>
      <div class="form-group">
        <label>性别</label>
        <select v-model="form.gender">
          <option value="">请选择</option>
          <option value="公">公</option>
          <option value="母">母</option>
        </select>
      </div>
      <div class="form-group">
        <label>体重(kg)</label>
        <input v-model.number="form.weight" type="number" min="0" step="0.01" />
      </div>
      <div class="form-group">
        <label>颜色</label>
        <input v-model="form.color" />
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
        <tr><th>ID</th><th>名字</th><th>年龄</th><th>品种</th><th>性别</th><th>体重</th><th>颜色</th><th>描述</th><th>操作</th></tr>
      </thead>
      <tbody>
        <tr v-for="cat in cats" :key="cat.id">
          <td>{{cat.id}}</td><td>{{cat.name}}</td><td>{{cat.age}}</td><td>{{cat.breed}}</td><td>{{cat.gender}}</td><td>{{cat.weight}}</td><td>{{cat.color}}</td><td>{{cat.description}}</td>
          <td>
            <button @click="editCat(cat)">编辑</button>
            <button @click="deleteCat(cat.id)">删除</button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from '../../utils/axios'

const cats = ref([])
const error = ref('')
const form = ref({ id: '', name: '', age: '', breed: '', gender: '', weight: '', color: '', description: '' })

function loadCats() {
  error.value = ''
  axios.get('/cats').then(res => {
    cats.value = res.data || []
  }).catch(() => { error.value = '加载失败' })
}
function saveCat() {
  error.value = ''
  const data = { ...form.value }
  const method = data.id ? 'put' : 'post'
  const url = data.id ? `/cats/${data.id}` : '/cats'
  axios[method](url, data).then(() => {
    resetForm()
    loadCats()
  }).catch(() => { error.value = '保存失败' })
}
function editCat(cat) {
  form.value = { ...cat }
}
function resetForm() {
  form.value = { id: '', name: '', age: '', breed: '', gender: '', weight: '', color: '', description: '' }
}
function deleteCat(id) {
  if (!confirm('确定要删除这只猫吗？')) return
  axios.delete(`/cats/${id}`).then(() => {
    loadCats()
  }).catch(() => { error.value = '删除失败' })
}
onMounted(loadCats)
</script>

<style scoped>
.form-group { margin-bottom: 16px; }
label { display: block; margin-bottom: 4px; }
input, select { width: 100%; padding: 8px; box-sizing: border-box; }
.admin-table { width: 100%; border-collapse: collapse; margin-top: 16px; }
th, td { border: 1px solid #ddd; padding: 8px; text-align: center; }
th { background: #f0f0f0; }
.error { color: #d00; margin-bottom: 12px; }
button { margin-right: 6px; }
</style>

