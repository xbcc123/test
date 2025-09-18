<template>
  <section class="disease-section">
    <a-typography-title level="2" class="disease-title">宠物疾病管理</a-typography-title>
    <a-alert v-if="error" type="error" :message="error" show-icon style="margin-bottom: 16px;" />
    <a-table :columns="columns" :data-source="diseases" rowKey="id" bordered :pagination="false">
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex">
          {{ record[column.dataIndex] }}
        </template>
      </template>
    </a-table>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from '../utils/axios'
import { Typography, Alert, Table } from 'ant-design-vue'

const diseases = ref([])
const error = ref('')

const columns = [
  { title: 'ID', dataIndex: 'id' },
  { title: '名称', dataIndex: 'name' },
  { title: '症状', dataIndex: 'symptom' },
  { title: '病因', dataIndex: 'cause' },
  { title: '治疗方法', dataIndex: 'treatment' },
]

const loadDiseases = async () => {
  error.value = ''
  try {
    const res = await axios.get('/api/pet-disease')
    diseases.value = res || []
  } catch (e) {
    error.value = '加载失败'
  }
}

onMounted(loadDiseases)
</script>

<style scoped>
.disease-section { padding: 20px; }
.disease-title { margin-bottom: 10px; }
</style>
