<template>
  <section class="encyclopedia-detail-section" v-if="encyclopedia">
    <h2 class="encyclopedia-detail-title">{{ encyclopedia.title }}</h2>
    <div class="encyclopedia-detail-meta">
      <span>类型：{{ encyclopedia.type }}</span>
      <span v-if="encyclopedia.createTime"> | 发布时间：{{ formatDate(encyclopedia.createTime) }}</span>
    </div>
    <div class="encyclopedia-detail-content">{{ encyclopedia.content }}</div>
  </section>
  <div v-else class="error">{{ error || '未找到百科内容' }}</div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import axios from '../utils/axios'

const route = useRoute()
const encyclopedia = ref(null)
const error = ref('')
function formatDate(dateStr) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString()
}

const loadEncyclopedia = async () => {
  error.value = ''
  try {
    const res = await axios.get(`/encyclopedias/${route.params.id}`)
    encyclopedia.value = res.data
    if (!encyclopedia.value) error.value = '未找到百科内容'
  } catch (e) {
    error.value = '加载失败'
  }
}
onMounted(loadEncyclopedia)
</script>

<style scoped>
.encyclopedia-detail-section {
  margin: 24px auto;
  max-width: 700px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px #e3e3e3;
  padding: 32px 24px;
}
.encyclopedia-detail-title {
  font-size: 26px;
  font-weight: bold;
  margin-bottom: 16px;
  color: #1565c0;
}
.encyclopedia-detail-meta {
  color: #888;
  font-size: 14px;
  margin-bottom: 18px;
}
.encyclopedia-detail-content {
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
