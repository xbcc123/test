<template>
  <section class="cat-section">
    <h2 class="cat-title">Cat 列表</h2>
    <div class="error" v-if="error">{{ error }}</div>
    <div class="cat-table-wrapper">
      <table class="cat-table">
        <thead>
          <tr><th>ID</th><th>名字</th><th>年龄</th><th>品种</th><th>性别</th><th>体重</th><th>颜色</th><th>描述</th><th>图片</th></tr>
        </thead>
        <tbody>
          <tr v-for="cat in cats" :key="cat.id">
            <td>{{ cat.id }}</td>
            <td>{{ cat.name }}</td>
            <td>{{ cat.age }}</td>
            <td>{{ cat.breed }}</td>
            <td>{{ cat.gender }}</td>
            <td>{{ cat.weight }}</td>
            <td>{{ cat.color }}</td>
            <td>{{ cat.description }}</td>
            <td>
              <img v-if="cat.imageUrl" :src="cat.imageUrl" alt="cat image" style="max-width:60px;max-height:60px;object-fit:cover;" />
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from '../utils/axios'

const cats = ref([])
const error = ref('')

const loadCats = async () => {
  console.log(4444)
  error.value = ''
  try {
    const res = await axios.get('/cats')
    cats.value = res.data || []
  } catch (e) {
    error.value = '加载失败'
  }
}
onMounted(loadCats)
</script>

<style scoped>
.cat-section {
  margin-bottom: 24px;
  padding: 0;
  border: none;
}
.cat-title {
  color: #1565c0;
  font-size: 22px;
  font-weight: 700;
  margin-bottom: 18px;
  letter-spacing: 1px;
}
.cat-table-wrapper {
  overflow-x: auto;
}
.cat-table {
  width: 100%;
  border-collapse: collapse;
  background: transparent;
}
.cat-table th, .cat-table td {
  border: 1px solid #dde6f6;
  padding: 8px;
  text-align: center;
}
.cat-table th {
  background: #e3f2fd;
  color: #1976d2;
}
.cat-table tr:nth-child(even) {
  background: #f5fafd;
}
.error { color: #1976d2; margin-bottom: 12px; }
</style>
