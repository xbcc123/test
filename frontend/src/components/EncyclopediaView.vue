<template>
  <section class="max-w-3xl mx-auto py-10">
    <h2 class="text-3xl font-bold text-blue-900 mb-6">宠物百科</h2>
    <Card customClass="p-6">
      <div v-if="error" class="text-red-500 text-sm mb-4">{{ error }}</div>
      <ul v-if="encyclopedias.length" class="divide-y divide-blue-50">
        <li v-for="e in encyclopedias" :key="e.id" class="py-4 flex items-center justify-between hover:bg-blue-50 px-2 rounded-lg transition">
          <router-link :to="`/encyclopedia/${e.id}`" class="font-bold text-blue-800 hover:underline">{{ e.title }}</router-link>
          <span class="text-xs text-blue-500 bg-blue-100 px-2 py-1 rounded">{{ e.type }}</span>
        </li>
      </ul>
      <div v-else class="text-gray-400">暂无数据</div>
    </Card>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from '../utils/axios'
import Card from './ui/Card.vue'

const encyclopedias = ref([])
const error = ref('')

const loadEncyclopedias = async () => {
  error.value = ''
  try {
    const res = await axios.get('/encyclopedias')
    encyclopedias.value = res || []
  } catch (e) {
    error.value = '加载失败'
  }
}
onMounted(loadEncyclopedias)
</script>
