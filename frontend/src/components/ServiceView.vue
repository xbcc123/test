<template>
  <div class="container">
    <h2>服务与资源</h2>
    <div class="error" v-if="error">{{ error }}</div>
    <b>宠物医院：</b>
    <ul>
      <li v-for="h in hospitals" :key="h.id">
        <b>{{ h.name }}</b> {{ h.address }} {{ h.phone }} 评分:{{ h.rating || '-' }}<br />
        {{ h.description }}
      </li>
    </ul>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from '../utils/axios'

const hospitals = ref([])
const error = ref('')

const loadHospitals = async () => {
  error.value = ''
  try {
    const res = await axios.get('/hospitals')
    hospitals.value = res.data || []
  } catch (e) {
    error.value = '加载失败'
  }
}
onMounted(loadHospitals)
</script>

<style scoped>
.container { max-width: 700px; margin: 40px auto; background: #fff; padding: 24px; border-radius: 8px; box-shadow: 0 2px 8px #0001; }
.error { color: #d00; margin-bottom: 12px; }
ul { padding-left: 20px; }
</style>

