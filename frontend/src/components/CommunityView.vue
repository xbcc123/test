<template>
  <div class="container">
    <h2>社区互动</h2>
    <div class="error" v-if="error">{{ error }}</div>
    <ul>
      <li v-for="p in posts" :key="p.id">
        <b>用户{{ p.userId }}</b> [{{ p.type }}]<br />
        {{ p.content }}
      </li>
    </ul>
  </div>
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
.container { max-width: 700px; margin: 40px auto; background: #fff; padding: 24px; border-radius: 8px; box-shadow: 0 2px 8px #0001; }
.error { color: #d00; margin-bottom: 12px; }
ul { padding-left: 20px; }
</style>

