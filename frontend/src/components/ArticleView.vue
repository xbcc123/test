<template>
  <section class="article-section">
    <h2 class="article-title">资讯中心</h2>
    <div class="error" v-if="error">{{ error }}</div>
    <div v-for="(list, category) in groupedArticles" :key="category" class="article-category-block">
      <div class="category-header">
        <span class="category-title">{{ category }}</span>
        <span class="category-count">共{{ list.length }}条</span>
      </div>
      <ul class="article-list">
        <li v-for="a in list" :key="a.id" class="article-list-item">
          <router-link :to="`/articles/${a.id}`">
            <b>{{ a.title }}</b>
          </router-link>
          <span class="article-time" v-if="a.createTime">{{ formatDate(a.createTime) }}</span>
        </li>
      </ul>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from '../utils/axios'

const articles = ref([])
const error = ref('')

function formatDate(dateStr) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString()
}

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

const groupedArticles = computed(() => {
  const groups = {}
  for (const a of articles.value) {
    const cat = a.category || '未分类'
    if (!groups[cat]) groups[cat] = []
    groups[cat].push(a)
  }
  return groups
})
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
.article-category-block {
  background: #f8fbff;
  border-radius: 10px;
  margin-bottom: 28px;
  box-shadow: 0 2px 8px #e3eaf7;
  padding: 18px 22px 10px 22px;
}
.category-header {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}
.category-title {
  font-size: 18px;
  font-weight: bold;
  color: #1976d2;
  margin-right: 16px;
}
.category-count {
  font-size: 13px;
  color: #90caf9;
}
.article-list {
  list-style: none;
  padding: 0;
  margin: 0;
}
.article-list-item {
  padding: 10px 0 8px 0;
  border-bottom: 1px solid #e3f2fd;
  color: #1976d2;
  font-size: 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  transition: background 0.2s;
}
.article-list-item:last-child {
  border-bottom: none;
}
.article-list-item:hover {
  background: #f0f7ff;
}
.article-time {
  color: #90caf9;
  font-size: 13px;
  margin-left: 12px;
  white-space: nowrap;
}
.error { color: #1976d2; margin-bottom: 12px; }
</style>
