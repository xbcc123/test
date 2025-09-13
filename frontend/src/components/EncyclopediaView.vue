<template>
  <section class="encyclopedia-section">
    <h2 class="encyclopedia-title">宠物百科</h2>
    <div class="error" v-if="error">{{ error }}</div>
    <ul class="encyclopedia-list">
      <li v-for="e in encyclopedias" :key="e.id" class="encyclopedia-list-item">
        <b>{{ e.title }}</b> <span class="encyclopedia-type">[{{ e.type }}]</span><br />
        <span class="encyclopedia-content">{{ e.content }}</span>
      </li>
    </ul>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from '../utils/axios'

const encyclopedias = ref([])
const error = ref('')

const loadEncyclopedias = async () => {
  error.value = ''
  try {
    const res = await axios.get('/encyclopedias')
    encyclopedias.value = res.data || []
  } catch (e) {
    error.value = '加载失败'
  }
}
onMounted(loadEncyclopedias)
</script>

<style scoped>
.encyclopedia-section {
  margin-bottom: 24px;
  padding: 0;
  border: none;
}
.encyclopedia-title {
  color: #1565c0;
  font-size: 22px;
  font-weight: 700;
  margin-bottom: 18px;
  letter-spacing: 1px;
}
.encyclopedia-list {
  list-style: none;
  padding: 0;
  margin: 0;
}
.encyclopedia-list-item {
  padding: 12px 0 10px 0;
  border-bottom: 1px solid #e3f2fd;
  color: #1976d2;
  font-size: 16px;
  transition: background 0.2s;
}
.encyclopedia-list-item:last-child {
  border-bottom: none;
}
.encyclopedia-list-item:hover {
  background: #f5fafd;
}
.encyclopedia-type {
  color: #64b5f6;
  font-size: 13px;
  font-weight: 500;
}
.encyclopedia-content {
  color: #333;
  font-size: 15px;
  display: inline-block;
  margin-top: 2px;
}
.error { color: #1976d2; margin-bottom: 12px; }
</style>
