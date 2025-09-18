<template>
  <section class="cat-section">
    <a-typography-title level="2">Cat 列表</a-typography-title>
    <a-alert v-if="error" type="error" :message="error" show-icon style="margin-bottom: 16px;" />
    <a-table :columns="columns" :data-source="cats" rowKey="id" bordered>
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'imageUrl'">
          <img v-if="record.imageUrl" :src="record.imageUrl" alt="cat image" style="max-width:60px;max-height:60px;object-fit:cover;" />
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

const cats = ref([])
const error = ref('')

const columns = [
  { title: 'ID', dataIndex: 'id' },
  { title: '名字', dataIndex: 'name' },
  { title: '年龄', dataIndex: 'age' },
  { title: '品种', dataIndex: 'breed' },
  { title: '性别', dataIndex: 'gender' },
  { title: '体重', dataIndex: 'weight' },
  { title: '颜色', dataIndex: 'color' },
  { title: '描述', dataIndex: 'description' },
  { title: '图片', dataIndex: 'imageUrl' }
]

const loadCats = async () => {
  error.value = ''
  try {
    const res = await axios.get('/cats')
    cats.value = res || []
  } catch (e) {
    error.value = '加载失败'
  }
}
onMounted(loadCats)
</script>

<style scoped>
.cat-section { padding: 20px; }
</style>
