<template>
  <div class="container">
    <h2>宠物百科</h2>
    <div class="error" v-if="error">{{ error }}</div>
    <ul>
      <li v-for="e in encyclopedias" :key="e.id">
        <b>{{ e.title }}</b> [{{ e.type }}]<br />
        {{ e.content }}
      </li>
    </ul>
  </div>
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
.container { max-width: 700px; margin: 40px auto; background: #fff; padding: 24px; border-radius: 8px; box-shadow: 0 2px 8px #0001; }
.error { color: #d00; margin-bottom: 12px; }
ul { padding-left: 20px; }
</style>

