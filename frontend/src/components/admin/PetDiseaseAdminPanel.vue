<template>
  <section class="disease-section">
    <h2 class="disease-title">宠物疾病管理</h2>
    <a-button type="primary" @click="showAdd = true" style="margin-bottom: 16px;">新增疾病</a-button>
    <a-table :columns="columns" :data-source="diseases" rowKey="id" bordered :pagination="false">
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'action'">
          <a-button size="small" @click="editDisease(record)">编辑</a-button>
          <a-button size="small" danger @click="deleteDisease(record.id)">删除</a-button>
        </template>
        <template v-else>
          {{ record[column.dataIndex] }}
        </template>
      </template>
    </a-table>
    <a-modal v-model:open="modalOpen" :title="showAdd ? '新增疾病' : '编辑疾病'" @cancel="closeModal" @ok="submitDisease">
      <a-form :model="form" layout="vertical">
        <a-form-item label="名称" required>
          <a-input v-model:value="form.name" />
        </a-form-item>
        <a-form-item label="症状" required>
          <a-input v-model:value="form.symptom" />
        </a-form-item>
        <a-form-item label="病因" required>
          <a-input v-model:value="form.cause" />
        </a-form-item>
        <a-form-item label="治疗方法" required>
          <a-input v-model:value="form.treatment" />
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
.disease-section { padding: 20px; }
.disease-title { font-size: 22px; font-weight: bold; margin-bottom: 18px; }
</style>
