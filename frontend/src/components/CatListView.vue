<template>
  <div class="container">
    <h2>Cat 列表</h2>
    <div class="error" v-if="error">{{ error }}</div>
    <table>
      <thead>
        <tr><th>ID</th><th>名字</th><th>年龄</th><th>品种</th><th>性别</th><th>体重</th><th>颜色</th><th>描述</th></tr>
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
        </tr>
      </tbody>
    </table>
  </div>
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
.container { max-width: 900px; margin: 40px auto; background: #fff; padding: 24px; border-radius: 8px; box-shadow: 0 2px 8px #0001; }
.error { color: #d00; margin-bottom: 12px; }
table { width: 100%; border-collapse: collapse; margin-top: 24px; }
th, td { border: 1px solid #ddd; padding: 8px; text-align: center; }
th { background: #f0f0f0; }
</style>

