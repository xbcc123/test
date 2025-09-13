<template>
  <div class="container">
    <h2>资讯中心</h2>
    <div class="error" v-if="error">{{ error }}</div>
    <ul>
      <li v-for="a in articles" :key="a.id">
        <b>{{ a.title }}</b> [{{ a.category }}]<br />
        {{ a.content }}
      </li>
    </ul>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from '../utils/axios'

const articles = ref([])
const error = ref('')

const loadArticles = async () => {
  error.value = ''
  try {
    const res = await axios.get('/articles')
    articles.value = res.data || []
  } catch (e) {
    error.value = '加载失败'
  }
}
onMounted(loadArticles)
</script>

<style scoped>
.container { max-width: 700px; margin: 40px auto; background: #fff; padding: 24px; border-radius: 8px; box-shadow: 0 2px 8px #0001; }
.error { color: #d00; margin-bottom: 12px; }
ul { padding-left: 20px; }
</style>

