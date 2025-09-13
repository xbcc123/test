<template>
  <section class="article-section">
    <h2 class="article-title">资讯中心</h2>
    <div class="error" v-if="error">{{ error }}</div>
    <ul class="article-list">
      <li v-for="a in articles" :key="a.id" class="article-list-item">
        <b>{{ a.title }}</b> <span class="article-category">[{{ a.category }}]</span><br />
        <span class="article-content">{{ a.content }}</span>
      </li>
    </ul>
  </section>
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
.article-section {
  margin-bottom: 24px;
  padding: 0;
  border: none;
}
.article-title {
  color: #1565c0;
  font-size: 22px;
  font-weight: 700;
  margin-bottom: 18px;
  letter-spacing: 1px;
}
.article-list {
  list-style: none;
  padding: 0;
  margin: 0;
}
.article-list-item {
  padding: 12px 0 10px 0;
  border-bottom: 1px solid #e3f2fd;
  color: #1976d2;
  font-size: 16px;
  transition: background 0.2s;
}
.article-list-item:last-child {
  border-bottom: none;
}
.article-list-item:hover {
  background: #f5fafd;
}
.article-category {
  color: #64b5f6;
  font-size: 13px;
  font-weight: 500;
}
.article-content {
  color: #333;
  font-size: 15px;
  display: inline-block;
  margin-top: 2px;
}
.error { color: #1976d2; margin-bottom: 12px; }
</style>
