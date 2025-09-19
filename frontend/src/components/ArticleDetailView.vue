<template>
  <section class="article-detail-section" v-if="article">
    <h2 class="article-detail-title">{{ article.title }}</h2>
    <div class="article-detail-meta">
      <span>分类：{{ article.category }}</span>
      <span v-if="article.author"> | 作者：{{ article.author }}</span>
      <span v-if="article.createTime"> | 发布时间：{{ formatDate(article.createTime) }}</span>
    </div>
    <div class="article-detail-content">{{ article.content }}</div>
    <div class="like-section">
      <LikeButton :postId="article.id" />
    </div>
  </section>
  <div v-else class="error">{{ error || '未找到资讯' }}</div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import axios from '../utils/axios'
import LikeButton from './ui/LikeButton.vue'

const route = useRoute()
const article = ref(null)
const error = ref('')

function formatDate(dateStr) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString()
}

const loadArticle = async () => {
  error.value = ''
  try {
    const res = await axios.get(`/articles/${route.params.id}`)
    article.value = res
  } catch (e) {
    error.value = '加载失败'
  }
}

onMounted(loadArticle)
</script>

<style scoped>
.article-detail-section {
  margin: 24px auto;
  max-width: 700px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px #e3e3e3;
  padding: 32px 24px;
}
.article-detail-title {
  font-size: 26px;
  font-weight: bold;
  margin-bottom: 16px;
  color: #1565c0;
}
.article-detail-meta {
  color: #888;
  font-size: 14px;
  margin-bottom: 18px;
}
.article-detail-content {
  font-size: 18px;
  line-height: 1.8;
  color: #222;
  white-space: pre-line;
}
.error {
  color: #d00;
  margin: 32px auto;
  text-align: center;
}
.like-section {
  margin-top: 24px;
  display: flex;
  align-items: center;
  gap: 12px;
}
</style>
