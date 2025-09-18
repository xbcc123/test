<template>
  <section class="petsale-section">
    <h2 class="petsale-title">宠物出售信息</h2>
    <a-button type="primary" @click="goPublish" style="margin-bottom: 16px;">发布宠物</a-button>
    <a-table :columns="columns" :data-source="pets" rowKey="id" bordered :pagination="false">
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'imgUrl'">
          <img v-if="record.imgUrl" :src="record.imgUrl" alt="pet image" style="max-width:60px;max-height:60px;object-fit:cover;" />
        </template>
        <template v-else>
          {{ record[column.dataIndex] }}
        </template>
      </template>
    </a-table>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from '../utils/axios'
import { message } from 'ant-design-vue'

const pets = ref([])
const router = useRouter()

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
]

const loadPets = async () => {
  try {
    const res = await axios.get('/api/pet-sale/on-sale')
    pets.value = res || []
  } catch (e) {
    message.error('加载失败')
  }
}

const goPublish = () => {
  router.push('/pet-sale/publish')
}

onMounted(loadPets)
</script>

<style scoped>
.petsale-section { padding: 20px; }
.petsale-title { font-size: 1.5em; margin-bottom: 10px; }
</style>
