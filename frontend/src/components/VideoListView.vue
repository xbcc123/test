<template>
  <div class="p-4">
    <div class="flex justify-between items-center mb-4">
      <h1 class="text-2xl font-bold">视频列表</h1>
      <router-link to="/videos/upload" class="bg-blue-500 text-white px-4 py-2 rounded">上传视频</router-link>
    </div>
    <div v-if="error" class="text-red-500">{{ error }}</div>
    <div v-if="videos.length === 0">暂无视频</div>
    <div v-else class="grid grid-cols-1 md:grid-cols-3 gap-4">
      <div v-for="video in videos" :key="video.id" class="border rounded p-2">
        <div class="mb-1 text-xs text-gray-400">src: {{ getFullUrl(video.playUrl) }}</div>
        <video :src="getFullUrl(video.playUrl)" controls class="w-full h-48 object-cover mb-2"></video>
        <h2 class="font-semibold">{{ video.title }}</h2>
        <p class="text-sm text-gray-500">{{ video.description }}</p>
        <router-link :to="'/videos/' + video.id" class="text-blue-500">查看详情</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from '../utils/axios'
import { getFullUrl } from '../utils/url'

const videos = ref([])
const error = ref('')

onMounted(async () => {
  try {
    const res = await axios.get('/api/videos?page=0&size=12')
    if (Array.isArray(res)) {
      videos.value = res
    } else if (res && Array.isArray(res.content)) {
      videos.value = res.content
    } else {
      videos.value = []
    }
  } catch (e) {
    error.value = e.message || '加载失败'
    videos.value = []
  }
})
</script>
