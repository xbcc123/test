<template>
  <section class="max-w-3xl mx-auto py-10">
    <h2 class="text-3xl font-bold text-blue-900 mb-6">社区互动</h2>
    <Card customClass="mb-6 p-6">
      <div class="flex gap-4 mb-4">
        <Input v-model="searchKeyword" placeholder="搜索内容..." @keyup.enter="searchPosts" />
        <Button @click="showPostForm = !showPostForm">{{ showPostForm ? '收起' : '发帖' }}</Button>
      </div>
      <form v-if="showPostForm" class="space-y-3 mb-4" @submit.prevent="submitPost">
        <textarea v-model="newPost.content" placeholder="说点什么..." required class="w-full border rounded-lg p-3 min-h-[60px] focus:ring-2 focus:ring-blue-400"></textarea>
        <select v-model="newPost.type" class="w-full border rounded-lg p-2">
          <option value="">请选择类型</option>
          <option value="经验">经验</option>
          <option value="日常">日常</option>
        </select>
        <div class="flex gap-2">
          <Button type="submit">提交</Button>
          <Button variant="secondary" type="button" @click="showPostForm = false">取消</Button>
        </div>
      </form>
      <div v-if="error" class="text-red-500 text-sm mb-2">{{ error }}</div>
      <div v-if="loading" class="text-gray-500">加载中...</div>
      <ul v-else-if="posts.length" class="space-y-3">
        <li v-for="p in posts" :key="p.id" class="flex items-center justify-between bg-blue-50 rounded-lg px-4 py-3">
          <div class="flex flex-col">
            <router-link :to="`/community/${p.id}`" class="font-bold text-blue-800 hover:underline">用户{{ p.userId }}</router-link>
            <span class="text-xs text-blue-500">[{{ p.type }}]</span>
            <span class="text-gray-700 mt-1">{{ p.content.slice(0, 30) }}...</span>
          </div>
          <Button variant="secondary" @click="deletePost(p.id)">删除</Button>
        </li>
      </ul>
      <div v-else class="text-gray-400">暂无数据</div>
      <div class="flex items-center justify-between mt-6">
        <Button variant="secondary" @click="prevPage" :disabled="page === 0">上一页</Button>
        <span class="text-gray-600">第 {{ page + 1 }} 页 / 共 {{ totalPages }} 页</span>
        <Button variant="secondary" @click="nextPage" :disabled="page + 1 >= totalPages">下一页</Button>
      </div>
    </Card>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from '../utils/axios'
import Card from './ui/Card.vue'
import Input from './ui/Input.vue'
import Button from './ui/Button.vue'

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
  try {
    await axios.delete(`/posts/${id}`)
    loadPosts()
  } catch (e) {
    error.value = '删除失败'
  }
}

onMounted(() => {
  loadPosts()
})
</script>
