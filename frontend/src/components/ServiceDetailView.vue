<template>
  <section class="service-detail-section" v-if="hospital">
    <h2 class="service-detail-title">{{ hospital.name }}</h2>
    <div class="service-detail-meta">
      <span>地址：{{ hospital.address }}</span>
      <span v-if="hospital.phone"> | 电话：{{ hospital.phone }}</span>
      <span v-if="hospital.rating"> | 评分：{{ hospital.rating }}</span>
      <span v-if="hospital.createTime"> | 收录时间：{{ formatDate(hospital.createTime) }}</span>
    </div>
    <div class="service-detail-content">{{ hospital.description }}</div>
  </section>
  <div v-else class="error">{{ error || '未找到医院信息' }}</div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import axios from '../utils/axios'

const route = useRoute()
const hospital = ref(null)
const error = ref('')

function formatDate(dateStr) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString()
}

const loadHospital = async () => {
  error.value = ''
  try {
    const res = await axios.get(`/hospitals/${route.params.id}`)
    hospital.value = res
    if (!hospital.value) error.value = '未找到医院信息'
  } catch (e) {
    error.value = '加载失败'
  }
}
onMounted(loadHospital)
</script>

<style scoped>
.service-detail-section {
  margin: 24px auto;
  max-width: 700px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px #e3e3e3;
  padding: 32px 24px;
}
.service-detail-title {
  font-size: 26px;
  font-weight: bold;
  margin-bottom: 16px;
  color: #1565c0;
}
.service-detail-meta {
  color: #888;
  font-size: 14px;
  margin-bottom: 18px;
}
.service-detail-content {
  font-size: 18px;
  line-height: 1.8;
  color: #222;
  white-space: pre-line;
}
.error {
  color: #d00;
  margin: 32px auto;
  text-align: center;
}
</style>
