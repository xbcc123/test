<template>
  <div>
    <h2 class="home-title">🐾 宠物之家</h2>
    <div class="home-desc">欢迎来到宠物之家！这里有最新的宠物资讯、百科、社区互动和丰富的服务资源。</div>
    <section class="home-section">
      <h3 class="section-title">推荐文章</h3>
      <ul class="home-list">
        <li v-for="a in articles" :key="a.id" class="home-list-item">{{ a.title }}</li>
      </ul>
    </section>
    <section class="home-section">
      <h3 class="section-title">热门宠物</h3>
      <ul class="home-list">
        <li v-for="c in cats" :key="c.id" class="home-list-item">
          {{ c.name }}<span v-if="c.breed">（{{ c.breed }}）</span>
        </li>
      </ul>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from '../utils/axios'
import { getToken, removeToken } from '../utils/auth'
import Layout from './Layout.vue'

const articles = ref([])
const cats = ref([])
const router = useRouter()

const loadHomeArticles = async () => {
  try {
    const res = await axios.get('/articles')
    articles.value = (res.data || []).slice(0, 5)
  } catch {}
  try {
    const res = await axios.get('/cats')
    cats.value = (res.data || []).slice(0, 5)
  } catch {}
}

const logout = () => {
  removeToken()
  localStorage.removeItem('username')
  router.push('/login')
}

onMounted(() => {
  loadHomeArticles()
})
</script>

<style scoped>
.home-title {
  color: #1565c0;
  font-size: 28px;
  font-weight: 800;
  margin-bottom: 10px;
  letter-spacing: 1px;
}
.home-desc {
  color: #1976d2;
  font-size: 16px;
  margin-bottom: 24px;
}
.home-section {
  margin-bottom: 24px;
  padding: 0;
  border: none;
}
.section-title {
  color: #1976d2;
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 12px;
}
.home-list {
  list-style: none;
  padding: 0;
  margin: 0;
}
.home-list-item {
  padding: 8px 0;
  border-bottom: 1px solid #e3f2fd;
  color: #1565c0;
  font-size: 16px;
  transition: background 0.2s;
}
.home-list-item:last-child {
  border-bottom: none;
}
.home-list-item:hover {
  background: #f5fafd;
}
</style>
