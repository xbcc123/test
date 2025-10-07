<template>
  <div class="p-4">
    <router-link to="/videos" class="text-blue-500">返回视频列表</router-link>
    <div v-if="video" class="mt-4">
      <h1 class="text-2xl font-bold mb-2">{{ video.title }}</h1>
      <video :src="getFullUrl(video.playUrl)" controls class="w-full h-64 object-cover mb-2"></video>
      <p class="mb-2">{{ video.description }}</p>
      <div class="text-sm text-gray-500">上传时间：{{ video.uploadTime }}</div>
    </div>
    <div v-else>加载中...</div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import axios from '../utils/axios'
import { getFullUrl } from '../utils/url'

const route = useRoute()
const video = ref(null)

onMounted(async () => {
  const res = await axios.get(`/api/videos/${route.params.id}`)
  video.value = res
})
</script>
