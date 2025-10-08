<template>
  <section class="order-detail-section">
    <h2 class="order-detail-title">订单详情</h2>
    <div v-if="!order">加载中...</div>
    <div v-else>
      <div>订单号：{{ order.id }}</div>
      <div>下单时间：{{ order.createdAt }}</div>
      <div>总价：￥{{ order.totalPrice }}</div>
      <div>状态：{{ order.status }}</div>
      <h3>商品明细</h3>
      <table class="order-detail-table">
        <thead>
          <tr>
            <th>图片</th>
            <th>名称</th>
            <th>单价</th>
            <th>数量</th>
            <th>小计</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in order.items" :key="item.id">
            <td><img :src="item.imgUrl" alt="pet" style="width:60px;height:60px;object-fit:cover;"></td>
            <td>{{ item.productName }}</td>
            <td>￥{{ item.price }}</td>
            <td>{{ item.quantity }}</td>
            <td>￥{{ (item.price * item.quantity).toFixed(2) }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import axios from '../utils/axios'

const route = useRoute()
const order = ref(null)

async function loadOrder() {
  const orderId = route.params.orderId
  const res = await axios.get(`/api/order/${orderId}`)
  order.value = res
}

onMounted(loadOrder)
</script>

<style scoped>
.order-detail-section { padding: 20px; }
.order-detail-title { font-size: 1.5em; margin-bottom: 10px; }
.order-detail-table { width: 100%; border-collapse: collapse; margin-top: 16px; }
.order-detail-table th, .order-detail-table td { border: 1px solid #eee; padding: 8px; text-align: center; }
</style>

