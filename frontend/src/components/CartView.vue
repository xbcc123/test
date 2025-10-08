<template>
  <section class="cart-section">
    <h2 class="cart-title">购物车</h2>
    <div v-if="cart.length === 0">购物车为空</div>
    <div v-else>
      <table class="cart-table">
        <thead>
          <tr>
            <th>图片</th>
            <th>名称</th>
            <th>单价</th>
            <th>数量</th>
            <th>小计</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in cart" :key="item.productId">
            <td><img :src="item.imgUrl" alt="pet" style="width:60px;height:60px;object-fit:cover;"></td>
            <td>{{ item.productName }}</td>
            <td>￥{{ item.price }}</td>
            <td>
              <a-input-number v-model:value="item.quantity" :min="1" style="width:60px" />
            </td>
            <td>￥{{ (item.price * item.quantity).toFixed(2) }}</td>
            <td><a-button danger @click="removeItem(item.productId)">移除</a-button></td>
          </tr>
        </tbody>
      </table>
      <div class="cart-total">总价：￥{{ totalPrice.toFixed(2) }}</div>
      <a-button type="primary" @click="checkout" :disabled="cart.length === 0">结算</a-button>
    </div>
  </section>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import axios from '../utils/axios'
import { message } from 'ant-design-vue'

const router = useRouter()
const cart = ref(JSON.parse(localStorage.getItem('cart') || '[]'))

const totalPrice = computed(() => cart.value.reduce((sum, item) => sum + item.price * item.quantity, 0))

function removeItem(productId) {
  cart.value = cart.value.filter(item => item.productId !== productId)
  localStorage.setItem('cart', JSON.stringify(cart.value))
}

async function checkout() {
  // 假设已登录，userId 从本地获取
  const userId = localStorage.getItem('userId') || 1
  try {
    const res = await axios.post('/api/order/create', {
      userId,
      totalPrice: totalPrice.value,
      items: cart.value
    })
    message.success('下单成功！')
    cart.value = []
    localStorage.removeItem('cart')
    router.push({ name: 'OrderList' })
  } catch (e) {
    message.error('下单失败')
  }
}
</script>

<style scoped>
.cart-section { padding: 20px; }
.cart-title { font-size: 1.5em; margin-bottom: 10px; }
.cart-table { width: 100%; border-collapse: collapse; margin-bottom: 16px; }
.cart-table th, .cart-table td { border: 1px solid #eee; padding: 8px; text-align: center; }
.cart-total { font-weight: bold; margin-bottom: 16px; }
</style>

