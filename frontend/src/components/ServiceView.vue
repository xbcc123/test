<template>
  <section class="service-section">
    <h2 class="service-title">服务与资源</h2>
    <div class="error" v-if="error">{{ error }}</div>
    <b>宠物医院：</b>
    <ul class="service-list">
      <li v-for="h in hospitals" :key="h.id" class="service-list-item">
        <router-link :to="`/service/${h.id}`">
          <b>{{ h.name }}</b>
        </router-link>
        {{ h.address }} {{ h.phone }} 评分:{{ h.rating || '-' }}
      </li>
    </ul>
  </section>
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
    error.value = '�����载失败'
  }
}
onMounted(loadHospitals)
</script>

<style scoped>
.service-section {
  margin-bottom: 24px;
  padding: 0;
  border: none;
}
.service-title {
  color: #1565c0;
  font-size: 22px;
  font-weight: 700;
  margin-bottom: 18px;
  letter-spacing: 1px;
}
.service-list {
  list-style: none;
  padding: 0;
  margin: 0;
}
.service-list-item {
  padding: 12px 0 10px 0;
  border-bottom: 1px solid #e3f2fd;
  color: #1976d2;
  font-size: 16px;
  transition: background 0.2s;
}
.service-list-item:last-child {
  border-bottom: none;
}
.service-list-item:hover {
  background: #f5fafd;
}
.error { color: #1976d2; margin-bottom: 12px; }
</style>
