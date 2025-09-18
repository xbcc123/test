<template>
  <section class="max-w-3xl mx-auto py-10">
    <h2 class="text-3xl font-bold text-blue-900 mb-6">服务与资源</h2>
    <div class="mb-6 flex justify-end">
      <router-link to="/service-order">
        <button class="order-btn">预约上门看病</button>
      </router-link>
    </div>
    <Card customClass="p-6">
      <div v-if="error" class="text-red-500 text-sm mb-4">{{ error }}</div>
      <b class="text-blue-700">宠物医院：</b>
      <ul v-if="hospitals.length" class="divide-y divide-blue-50 mt-2">
        <li v-for="h in hospitals" :key="h.id" class="py-4 flex flex-col md:flex-row md:items-center md:justify-between hover:bg-blue-50 px-2 rounded-lg transition">
          <div class="flex flex-col md:flex-row md:items-center gap-2">
            <router-link :to="`/service/${h.id}`" class="font-bold text-blue-800 hover:underline">{{ h.name }}</router-link>
            <span class="text-gray-600 text-sm">{{ h.address }}</span>
            <span class="text-gray-600 text-sm">{{ h.phone }}</span>
          </div>
          <span class="text-xs text-yellow-600 bg-yellow-100 px-2 py-1 rounded mt-2 md:mt-0">评分: {{ h.rating || '-' }}</span>
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

const hospitals = ref([])
const error = ref('')

const loadHospitals = async () => {
  error.value = ''
  try {
    const res = await axios.get('/hospitals')
    hospitals.value = res || []
  } catch (e) {
    error.value = '加载失败'
  }
}
onMounted(loadHospitals)
</script>

<style>
.order-btn {
  background-color: #3b82f6;
  color: white;
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 0.375rem;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s;
}

.order-btn:hover {
  background-color: #2563eb;
}
</style>
