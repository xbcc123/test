<template>
  <section class="article-section">
    <h2 class="article-title">资讯中心</h2>
    <div v-if="error" class="error-msg">{{ error }}</div>
    <div v-for="(list, category) in groupedArticles" :key="category" class="article-category-block">
      <h3 class="category-title">{{ category }}（共{{ list.length }}条）</h3>
      <div class="article-card-list">
        <Card v-for="item in list" :key="item.id" class="article-card">
          <div class="article-card-content">
            <div v-if="item.imgUrl" class="article-card-img-wrapper">
              <img :src="item.imgUrl" alt="封面" class="article-card-img" />
            </div>
            <div class="article-card-info">
              <a :href="`#/articles/${item.id}`" class="article-card-title">{{ item.title }}</a>
              <div class="article-card-meta">
                <span>{{ item.category }}</span>
                <span v-if="item.createTime"> | {{ formatDate(item.createTime) }}</span>
              </div>
              <div class="article-card-desc">{{ item.content ? item.content.slice(0, 80) + (item.content.length > 80 ? '...' : '') : '' }}</div>
              <div class="article-card-actions">
                <LikeButton :postId="item.id" />
              </div>
            </div>
          </div>
        </Card>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from '../utils/axios'
import Card from './ui/Card.vue'
import LikeButton from './ui/LikeButton.vue'

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
    articles.value = res || []
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
.article-section { padding: 24px; }
.article-title { font-size: 2em; margin-bottom: 18px; }
.error-msg { color: #d00; margin-bottom: 16px; }
.category-title { font-size: 1.2em; margin: 24px 0 12px 0; color: #1565c0; }
.article-card-list {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}
.article-card {
  width: 350px;
  min-height: 160px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.article-card-content {
  display: flex;
  gap: 16px;
}
.article-card-img-wrapper {
  flex-shrink: 0;
}
.article-card-img {
  width: 90px;
  height: 90px;
  object-fit: cover;
  border-radius: 8px;
  border: 1px solid #eee;
}
.article-card-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.article-card-title {
  font-size: 1.1em;
  font-weight: bold;
  color: #1565c0;
  text-decoration: none;
}
.article-card-title:hover { text-decoration: underline; }
.article-card-meta {
  color: #888;
  font-size: 13px;
}
.article-card-desc {
  color: #444;
  font-size: 15px;
  margin: 4px 0 0 0;
  line-height: 1.5;
}
.article-card-actions {
  margin-top: 8px;
}
@media (max-width: 800px) {
  .article-card-list { flex-direction: column; gap: 16px; }
  .article-card { width: 100%; }
  .article-card-content { flex-direction: row; }
}
</style>
