<template>
  <section class="catshop-section">
    <h2 class="catshop-title">宠物买卖</h2>
    <h3 style="margin:20px 0 8px 0;">宠物出售信息</h3>
    <a-table :columns="petSaleColumns" :data-source="petSaleList" rowKey="id" bordered :pagination="false">
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
import axios from '../utils/axios'

const petSaleList = ref([])
const petSaleColumns = [
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

const loadPetSaleList = async () => {
  try {
    const res = await axios.get('/api/pet-sale/on-sale')
    petSaleList.value = res || []
  } catch (e) {}
}

onMounted(() => {
  loadPetSaleList()
})
</script>

<style scoped>
.catshop-section { padding: 20px; }
.catshop-title { font-size: 1.5em; margin-bottom: 10px; }
</style>
