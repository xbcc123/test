<template>
  <section class="community-section">
    <h2 class="community-title">社区互动</h2>
    <div class="community-actions">
      <input v-model="searchKeyword" @keyup.enter="searchPosts" placeholder="搜索内容..." />
      <button @click="showPostForm = !showPostForm">发帖</button>
    </div>
    <form v-if="showPostForm" class="post-form" @submit.prevent="submitPost">
      <textarea v-model="newPost.content" placeholder="说点什么..." required></textarea>
      <select v-model="newPost.type">
        <option value="">请选择类型</option>
        <option value="经验">经验</option>
        <option value="日常">日常</option>
      </select>
      <button type="submit">提交</button>
      <button type="button" @click="showPostForm = false">取消</button>
    </form>
    <div class="error" v-if="error">{{ error }}</div>
    <div v-if="loading">加载中...</div>
    <ul class="community-list" v-else-if="posts.length">
      <li v-for="p in posts" :key="p.id" class="community-list-item">
        <router-link :to="`/community/${p.id}`">
          <b>用户{{ p.userId }}</b>
        </router-link>
        <span class="community-type">[{{ p.type }}]</span>
        <span class="community-content">{{ p.content.slice(0, 30) }}...</span>
        <button @click="deletePost(p.id)" class="delete-btn">删除</button>
      </li>
    </ul>
    <div v-else>暂无数据</div>
    <div class="pagination">
      <button @click="prevPage" :disabled="page === 0">上一页</button>
      <span>第 {{ page + 1 }} 页 / 共 {{ totalPages }} 页</span>
      <button @click="nextPage" :disabled="page + 1 >= totalPages">下一页</button>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from '../utils/axios'

const posts = ref([])
const error = ref('')
const loading = ref(false)
const page = ref(0)
const size = ref(10)
const totalPages = ref(1)
const searchKeyword = ref('')
const showPostForm = ref(false)
const newPost = ref({ content: '', type: '' })

const loadPosts = async () => {
  error.value = ''
  loading.value = true
  try {
    let res
    if (searchKeyword.value) {
      res = await axios.get('/posts/search', { params: { keyword: searchKeyword.value, page: page.value, size: size.value } })
    } else {
      res = await axios.get('/posts/page', { params: { page: page.value, size: size.value } })
    }
    posts.value = res.data.content || []
    totalPages.value = res.data.totalPages || 1
  } catch (e) {
    error.value = '加载失败'
  } finally {
    loading.value = false
  }
}

const searchPosts = () => {
  page.value = 0
  loadPosts()
}

const prevPage = () => {
  if (page.value > 0) {
    page.value--
    loadPosts()
  }
}
const nextPage = () => {
  if (page.value + 1 < totalPages.value) {
    page.value++
    loadPosts()
  }
}

const submitPost = async () => {
  if (!newPost.value.content || !newPost.value.type) {
    error.value = '内容和类型不能为空'
    return
  }
  try {
    await axios.post('/posts', newPost.value)
    showPostForm.value = false
    newPost.value = { content: '', type: '' }
    loadPosts()
  } catch (e) {
    error.value = '发帖失败'
  }
}

const deletePost = async (id) => {
  if (!confirm('确定要删除吗？')) return
  try {
    await axios.delete(`/posts/${id}`)
    loadPosts()
  } catch (e) {
    error.value = '删除失败'
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
.community-actions {
  margin-bottom: 12px;
}
.community-actions input {
  padding: 4px 8px;
  margin-right: 8px;
}
.post-form {
  margin-bottom: 16px;
}
.post-form textarea {
  width: 60%;
  min-height: 60px;
  margin-right: 8px;
}
.post-form select {
  margin-right: 8px;
}
.delete-btn {
  margin-left: 12px;
  color: #d00;
  background: none;
  border: none;
  cursor: pointer;
}
.community-list {
  list-style: none;
  padding: 0;
}
.community-list-item {
  margin-bottom: 10px;
  padding: 8px 0;
  border-bottom: 1px solid #eee;
}
.community-type {
  color: #888;
  margin-left: 8px;
}
.community-content {
  margin-left: 12px;
  color: #333;
}
.pagination {
  margin-top: 18px;
  text-align: center;
}
.error {
  color: #d00;
  margin-bottom: 12px;
}
</style>
