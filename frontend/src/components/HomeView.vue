<template>
  <div class="container">
    <nav style="margin-bottom:24px;">
      <router-link to="/">首页</router-link>
      <router-link to="/articles">资讯中心</router-link>
      <router-link to="/encyclopedia">宠物百科</router-link>
      <router-link to="/community">社区互动</router-link>
      <router-link to="/service">服务与资源</router-link>
      <router-link to="/member">会员中心</router-link>
      <button style="float:right" @click="logout">退出</button>
    </nav>
    <h2>🐾 宠物之家</h2>
    <div>欢迎来到宠物之家！这里有最新的宠物资讯、百科、社区互动和丰富的服务资源。</div>
    <div style="margin-top:20px;">
      <b>推荐文章：</b>
      <ul>
        <li v-for="a in articles" :key="a.id">{{ a.title }}</li>
      </ul>
    </div>
    <div style="margin-top:20px;">
      <b>热门宠物：</b>
      <ul>
        <li v-for="c in cats" :key="c.id">{{ c.name }}<span v-if="c.breed">（{{ c.breed }}）</span></li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from '../utils/axios'
import { getToken, removeToken } from '../utils/auth'

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
.container { max-width: 600px; margin: 40px auto; background: #fff; padding: 24px; border-radius: 8px; box-shadow: 0 2px 8px #0001; }
nav { margin-bottom: 24px; }
nav a { margin-right: 8px; }
</style>

