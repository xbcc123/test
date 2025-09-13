<template>
  <section class="community-section">
    <h2 class="community-title">社区互动</h2>
    <div class="error" v-if="error">{{ error }}</div>
    <ul class="community-list">
      <li v-for="p in posts" :key="p.id" class="community-list-item">
        <b>用户{{ p.userId }}</b> <span class="community-type">[{{ p.type }}]</span><br />
        <span class="community-content">{{ p.content }}</span>
      </li>
    </ul>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from '../utils/axios'

const posts = ref([])
const error = ref('')

const loadPosts = async () => {
  error.value = ''
  try {
    const res = await axios.get('/posts')
    posts.value = res.data || []
  } catch (e) {
    error.value = '加载失败'
  }
}
onMounted(loadPosts)
</script>

<style scoped>
.community-section {
  margin-bottom: 24px;
  padding: 0;
  border: none;
}
.community-title {
  color: #1565c0;
  font-size: 22px;
  font-weight: 700;
  margin-bottom: 18px;
  letter-spacing: 1px;
}
.community-list {
  list-style: none;
  padding: 0;
  margin: 0;
}
.community-list-item {
  padding: 12px 0 10px 0;
  border-bottom: 1px solid #e3f2fd;
  color: #1976d2;
  font-size: 16px;
  transition: background 0.2s;
}
.community-list-item:last-child {
  border-bottom: none;
}
.community-list-item:hover {
  background: #f5fafd;
}
.community-type {
  color: #64b5f6;
  font-size: 13px;
  font-weight: 500;
}
.community-content {
  color: #333;
  font-size: 15px;
  display: inline-block;
  margin-top: 2px;
}
.error { color: #1976d2; margin-bottom: 12px; }
</style>
