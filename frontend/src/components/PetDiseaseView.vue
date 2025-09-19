<template>
  <section class="disease-section">
    <a-typography-title level="2" class="disease-title">宠物疾病管理</a-typography-title>
    <a-alert v-if="error" type="error" :message="error" show-icon style="margin-bottom: 16px;" />
    <a-list
      :data-source="diseases"
      bordered
      class="disease-list"
      style="background:transparent;border:none;box-shadow:none;"
    >
      <template #renderItem="{ item }">
        <div class="disease-list-item">
          <div class="disease-list-item-main">
            <b class="disease-name">{{ item.name }}</b>
            <span class="disease-symptom">{{ item.symptom?.slice(0, 30) + (item.symptom?.length > 30 ? '...' : '') }}</span>
          </div>
          <a-button type="link" @click="openDetail(item)" class="detail-btn">详情</a-button>
        </div>
      </template>
    </a-list>
    <a-modal v-model:open="showDetail" title="疾病详情" :footer="null" width="500px" class="disease-modal">
      <div v-if="currentDisease">
        <h3 class="modal-title">{{ currentDisease.name }}</h3>
        <div class="modal-detail-row"><b>症状：</b><span>{{ currentDisease.symptom }}</span></div>
        <div class="modal-detail-row"><b>病因：</b><span>{{ currentDisease.cause }}</span></div>
        <div class="modal-detail-row"><b>治疗方法：</b><span>{{ currentDisease.treatment }}</span></div>
      </div>
    </a-modal>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from '../utils/axios'

const diseases = ref([])
const error = ref('')
const showDetail = ref(false)
const currentDisease = ref(null)

const loadDiseases = async () => {
  error.value = ''
  try {
    const res = await axios.get('/api/pet-disease')
    diseases.value = res || []
  } catch (e) {
    error.value = '加载失败'
  }
}

const openDetail = (item) => {
  currentDisease.value = item
  showDetail.value = true
}

onMounted(loadDiseases)
</script>

<style scoped>
.disease-section {
  padding: 32px 16px 24px 16px;
  background: #f9fbfd;
  border-radius: 18px;
  box-shadow: 0 2px 12px 0 rgba(33,150,243,0.06);
  max-width: 700px;
  margin: 32px auto;
}
.disease-title {
  margin-bottom: 18px;
  color: var(--color-primary);
  font-size: 1.5em;
  font-weight: bold;
  letter-spacing: 0.02em;
}
.disease-list {
  background: transparent;
  border: none;
  box-shadow: none;
  padding: 0;
}
.disease-list-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0 16px 0;
  border-bottom: 1px solid #e3e3e3;
  transition: background 0.18s;
  border-radius: 0;
}
.disease-list-item:last-child {
  border-bottom: none;
}
.disease-list-item:hover {
  background: #f5faff;
}
.disease-list-item-main {
  display: flex;
  align-items: center;
  gap: 16px;
}
.disease-name {
  font-size: 1.08em;
  color: #1565c0;
  font-weight: 600;
}
.disease-symptom {
  color: #888;
  font-size: 0.98em;
  margin-left: 8px;
}
.detail-btn {
  font-size: 1em;
  color: var(--color-primary);
  font-weight: 500;
  padding: 0 8px;
}
.disease-modal .ant-modal-content {
  border-radius: 14px;
  padding: 32px 24px 18px 24px;
}
.disease-modal .ant-modal-header {
  border-radius: 14px 14px 0 0;
}
.modal-title {
  font-size: 1.2em;
  color: var(--color-primary);
  font-weight: bold;
  margin-bottom: 18px;
}
.modal-detail-row {
  font-size: 1em;
  color: #333;
  margin-bottom: 10px;
  display: flex;
  gap: 8px;
}
@media (max-width: 700px) {
  .disease-section {
    padding: 12px 2px 8px 2px;
    border-radius: 10px;
    margin: 12px 2px;
  }
  .disease-modal .ant-modal-content {
    padding: 18px 6px 10px 6px;
    border-radius: 8px;
  }
}
</style>
