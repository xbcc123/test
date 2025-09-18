<template>
  <section class="petsale-section">
    <h2 class="petsale-title">宠物出售管理</h2>
    <a-button type="primary" @click="showAdd = true" style="margin-bottom: 16px;">新增出售</a-button>
    <a-modal v-model:open="showAdd" title="新增宠物出售" @cancel="showAdd = false" @ok="handleAdd">
      <a-form :model="addForm" layout="vertical">
        <a-form-item label="名称" required>
          <a-input v-model:value="addForm.name" placeholder="请输入名称" />
        </a-form-item>
        <a-form-item label="类型" required>
          <a-input v-model:value="addForm.type" placeholder="请输入类型" />
        </a-form-item>
        <a-form-item label="年龄" required>
          <a-input v-model:value="addForm.age" placeholder="请输入年龄" />
        </a-form-item>
        <a-form-item label="价格" required>
          <a-input v-model:value="addForm.price" placeholder="请输入价格" />
        </a-form-item>
        <a-form-item label="描述">
          <a-input v-model:value="addForm.description" placeholder="请输入描述" />
        </a-form-item>
        <a-form-item label="图片URL">
          <a-input v-model:value="addForm.imgUrl" placeholder="图片链接（可选）" />
        </a-form-item>
        <a-form-item label="状态">
          <a-input v-model:value="addForm.status" placeholder="出售状态" />
        </a-form-item>
        <a-form-item label="卖家ID" required>
          <a-input v-model:value="addForm.sellerId" placeholder="请输入卖家ID" />
        </a-form-item>
      </a-form>
    </a-modal>
    <a-table :columns="columns" :data-source="pets" rowKey="id" bordered :pagination="false">
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'imgUrl'">
          <img v-if="record.imgUrl" :src="record.imgUrl" alt="pet image" style="max-width:60px;max-height:60px;object-fit:cover;" />
        </template>
        <template v-else>
          {{ record[column.dataIndex] }}
        </template>
      </template>
      <template #actions="{ record }">
        <a-button type="link" danger @click="deletePet(record.id)">删除</a-button>
      </template>
    </a-table>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from '@/utils/axios'
import { message } from 'ant-design-vue'

const pets = ref([])

const columns = [
  { title: 'ID', dataIndex: 'id' },
  { title: '名称', dataIndex: 'name' },
  { title: '类型', dataIndex: 'type' },
  { title: '年龄', dataIndex: 'age' },
  { title: '价格', dataIndex: 'price' },
  { title: '描述', dataIndex: 'description' },
  { title: '图片', dataIndex: 'imgUrl' },
  { title: '状态', dataIndex: 'status' },
  { title: '卖家ID', dataIndex: 'sellerId' },
  { title: '操作', key: 'actions', slots: { customRender: 'actions' } },
]

const loadPets = async () => {
  try {
    const res = await axios.get('/api/pet-sale/on-sale')
    pets.value = res || []
  } catch (e) {
    message.error('加载失败')
  }
}

const deletePet = async (id) => {
  try {
    await axios.delete(`/api/pet-sale/${id}`)
    message.success('删除成功')
    loadPets()
  } catch (e) {
    message.error('删除失败')
  }
}

const showAdd = ref(false)
const addForm = ref({ name: '', type: '', age: '', price: '', description: '', imgUrl: '', status: '', sellerId: '' })

const handleAdd = async () => {
  try {
    await axios.post('/api/pet-sale', addForm.value)
    message.success('新增成功')
    showAdd.value = false
    addForm.value = { name: '', type: '', age: '', price: '', description: '', imgUrl: '', status: '', sellerId: '' }
    loadPets()
  } catch (e) {
    message.error('新增失败')
  }
}

onMounted(loadPets)
</script>

<style scoped>
.petsale-section { padding: 20px; }
.petsale-title { font-size: 22px; font-weight: bold; margin-bottom: 18px; }
</style>
