<template>
  <section class="disease-section">
    <h2 class="disease-title">宠物疾病管理</h2>
    <a-button type="primary" @click="showAdd = true" class="add-btn">新增疾病</a-button>
    <div class="table-wrapper">
      <a-table :columns="columns" :data-source="diseases" rowKey="id" bordered :pagination="false">
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'action'">
            <a-button size="small" @click="editDisease(record)" class="mr-2">编辑</a-button>
            <a-button size="small" danger @click="deleteDisease(record.id)">删除</a-button>
          </template>
          <template v-else>
            <span class="table-cell">{{ record[column.dataIndex] }}</span>
          </template>
        </template>
      </a-table>
    </div>
    <a-modal v-model:open="modalOpen" :title="showAdd ? '新增疾病' : '编辑疾病'" @cancel="closeModal" @ok="submitDisease" class="disease-modal">
      <a-form :model="form" layout="vertical">
        <a-form-item label="名称" required>
          <a-input v-model:value="form.name" placeholder="请输入疾病名称" />
        </a-form-item>
        <a-form-item label="症状" required>
          <a-input v-model:value="form.symptom" placeholder="请输入症状" />
        </a-form-item>
        <a-form-item label="病因" required>
          <a-input v-model:value="form.cause" placeholder="请输入病因" />
        </a-form-item>
        <a-form-item label="治疗方法" required>
          <a-input v-model:value="form.treatment" placeholder="请输入治疗方法" />
        </a-form-item>
      </a-form>
    </a-modal>
  </section>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from '@/utils/axios'
import { message } from 'ant-design-vue'

const diseases = ref([])
const showAdd = ref(false)
const showEdit = ref(false)
const form = ref({ id: null, name: '', symptom: '', cause: '', treatment: '' })

const columns = [
  { title: 'ID', dataIndex: 'id' },
  { title: '名称', dataIndex: 'name' },
  { title: '症状', dataIndex: 'symptom' },
  { title: '病因', dataIndex: 'cause' },
  { title: '治疗方法', dataIndex: 'treatment' },
  { title: '操作', dataIndex: 'action' },
]

const loadDiseases = async () => {
  try {
    const res = await axios.get('/api/pet-disease')
    diseases.value = res || []
  } catch (e) {
    message.error('加载失败')
  }
}

const editDisease = (d) => {
  form.value = { ...d }
  showEdit.value = true
}

const deleteDisease = async (id) => {
  try {
    await axios.delete(`/api/pet-disease/${id}`)
    message.success('删除成功')
    loadDiseases()
  } catch (e) {
    message.error('删除失败')
  }
}

const submitDisease = async () => {
  try {
    if (showAdd.value) {
      await axios.post('/api/pet-disease', form.value)
    } else if (showEdit.value) {
      await axios.put(`/api/pet-disease/${form.value.id}`, form.value)
    }
    closeModal()
    loadDiseases()
    message.success('保存成功')
  } catch (e) {
    message.error('保存失败')
  }
}

const closeModal = () => {
  showAdd.value = false
  showEdit.value = false
  form.value = { id: null, name: '', symptom: '', cause: '', treatment: '' }
}

const modalOpen = computed({
  get: () => showAdd.value || showEdit.value,
  set: (val) => {
    if (!val) {
      showAdd.value = false;
      showEdit.value = false;
    }
  }
});

onMounted(loadDiseases)
</script>

<style scoped>
.disease-section {
  padding: 32px 18px 24px 18px;
  background: #f9fbfd;
  border-radius: 18px;
  box-shadow: 0 2px 12px 0 rgba(33,150,243,0.06);
  max-width: 900px;
  margin: 32px auto;
}
.disease-title {
  font-size: 1.6em;
  font-weight: bold;
  margin-bottom: 22px;
  color: var(--color-primary);
  letter-spacing: 0.02em;
}
.add-btn {
  margin-bottom: 18px;
  font-size: 1em;
  padding: 0 22px;
  border-radius: 8px;
  box-shadow: 0 2px 8px 0 rgba(33,150,243,0.08);
}
.table-wrapper {
  background: #fff;
  border-radius: 14px;
  box-shadow: 0 2px 8px 0 rgba(33,150,243,0.06);
  padding: 18px 12px 8px 12px;
  margin-bottom: 18px;
}
.table-cell {
  font-size: 1em;
  color: #2c3e50;
  padding: 2px 0;
}
.disease-modal {
  border-radius: 16px;
}
.disease-modal .ant-modal-content {
  border-radius: 16px;
  padding: 32px 24px 18px 24px;
}
.disease-modal .ant-modal-header {
  border-radius: 16px 16px 0 0;
}
.disease-modal .ant-modal-title {
  font-size: 1.2em;
  color: var(--color-primary);
  font-weight: bold;
}
.disease-modal .ant-form-item-label > label {
  font-weight: 500;
  color: #1976d2;
}
.disease-modal .ant-input {
  border-radius: 8px;
  font-size: 1em;
  padding: 8px 12px;
}
@media (max-width: 700px) {
  .disease-section {
    padding: 16px 4px 12px 4px;
    border-radius: 10px;
    margin: 12px 2px;
  }
  .table-wrapper {
    padding: 8px 2px 2px 2px;
    border-radius: 8px;
  }
  .disease-modal .ant-modal-content {
    padding: 18px 6px 10px 6px;
    border-radius: 8px;
  }
}
</style>
