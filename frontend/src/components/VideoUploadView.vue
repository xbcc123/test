<template>
  <div class="p-4 max-w-xl mx-auto">
    <h1 class="text-2xl font-bold mb-4">上传视频</h1>
    <form @submit.prevent="handleUpload">
      <div class="mb-2">
        <label class="block mb-1">标题</label>
        <input v-model="title" class="border rounded w-full p-2" required />
      </div>
      <div class="mb-2">
        <label class="block mb-1">描述</label>
        <textarea v-model="description" class="border rounded w-full p-2" rows="3"></textarea>
      </div>
      <div class="mb-2">
        <label class="block mb-1">选择视频文件</label>
        <input type="file" @change="onFileChange" accept="video/*" required />
      </div>
      <button type="submit" class="bg-blue-500 text-white px-4 py-2 rounded">上传</button>
    </form>
    <div v-if="uploading" class="mt-2 text-blue-500">上传中...</div>
    <div v-if="success" class="mt-2 text-green-500">上传成功！</div>
    <div v-if="error" class="mt-2 text-red-500">{{ error }}</div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from '../utils/axios'
import { useRouter } from 'vue-router'

const title = ref('')
const description = ref('')
const file = ref(null)
const uploading = ref(false)
const success = ref(false)
const error = ref('')
const router = useRouter()

function onFileChange(e) {
  file.value = e.target.files[0]
}

async function handleUpload() {
  if (!file.value) return
  uploading.value = true
  error.value = ''
  success.value = false
  try {
    const formData = new FormData()
    formData.append('title', title.value)
    formData.append('description', description.value)
    formData.append('file', file.value)
    await axios.post('/api/videos/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    success.value = true
    setTimeout(() => router.push('/videos'), 1000)
  } catch (e) {
    error.value = e.response?.data?.message || '上传失败'
  } finally {
    uploading.value = false
  }
}
</script>

