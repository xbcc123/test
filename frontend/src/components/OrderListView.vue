<template>
  <section class="order-list-section">
    <h2 class="order-list-title">我的订单</h2>
    <div v-if="orders.length === 0">暂无订单</div>
    <div v-else>
      <a-table :dataSource="orders" :columns="columns" rowKey="id" @rowClick="goDetail" />
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from '../utils/axios'

const router = useRouter()
const orders = ref([])
const userId = localStorage.getItem('userId') || 1

const columns = [
  { title: '订单号', dataIndex: 'id' },
  { title: '下单时间', dataIndex: 'createdAt' },
  { title: '总价', dataIndex: 'totalPrice', customRender: ({ text }) => `￥${text}` },
  { title: '状态', dataIndex: 'status' }
]

async function loadOrders() {
  const res = await axios.get(`/api/order/user/${userId}`)
  orders.value = res || []
}

function goDetail(record) {
  router.push({ name: 'OrderDetail', params: { orderId: record.id } })
}

onMounted(loadOrders)
</script>

<style scoped>
.order-list-section { padding: 20px; }
.order-list-title { font-size: 1.5em; margin-bottom: 10px; }
</style>

