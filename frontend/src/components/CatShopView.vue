<template>
  <section class="catshop-section">
    <h2 class="catshop-title">宠物买卖</h2>
    <div style="display:flex;gap:12px;align-items:center;margin-bottom:12px;">
      <a-button @click="goCart">查看购物车</a-button>
    </div>
    <h3 style="margin:20px 0 8px 0;">宠物出售信息</h3>
    <div v-if="petSaleList.length === 0">暂无宠物出售信息</div>
    <div v-else class="pet-list">
      <div v-for="pet in petSaleList" :key="pet.id" class="pet-card">
        <div class="pet-img-wrapper">
          <img v-if="pet.imgUrl" :src="pet.imgUrl" alt="pet image" class="pet-img" />
        </div>
        <div class="pet-info">
          <div><strong>名称：</strong>{{ pet.name }}</div>
          <div><strong>类型：</strong>{{ pet.type }}</div>
          <div><strong>年龄：</strong>{{ pet.age }}</div>
          <div><strong>价格：</strong>{{ pet.price }}</div>
          <div><strong>描述：</strong>{{ pet.description }}</div>
          <div><strong>状态：</strong>{{ pet.status }}</div>
          <div><strong>卖家ID：</strong>{{ pet.sellerId }}</div>
          <div style="margin-top:8px;">
            <a-button type="primary" size="small" @click.stop="addToCart(pet)">加入购物车</a-button>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from '../utils/axios'
import { message } from 'ant-design-vue'

const petSaleList = ref([])
const router = useRouter()

const loadPetSaleList = async () => {
  try {
    const res = await axios.get('/api/pet-sale/on-sale')
    petSaleList.value = res || []
  } catch (e) {}
}

const goCart = () => {
  router.push('/cart')
}

function addToCart(pet) {
  try {
    const cartRaw = localStorage.getItem('cart') || '[]'
    const cart = JSON.parse(cartRaw)
    const productId = pet.id || pet.productId
    const existing = cart.find(i => i.productId === productId)
    if (existing) {
      existing.quantity = (existing.quantity || 1) + 1
    } else {
      cart.push({
        productId,
        productName: pet.name || pet.productName,
        price: Number(pet.price || 0),
        imgUrl: pet.imgUrl || pet.img || '',
        quantity: 1
      })
    }
    localStorage.setItem('cart', JSON.stringify(cart))
    message.success('已加入购物车')
  } catch (e) {
    console.error(e)
    message.error('加入购物车失败')
  }
}

onMounted(() => {
  loadPetSaleList()
})
</script>

<style scoped>
.catshop-section { padding: 20px; }
.catshop-title { font-size: 1.5em; margin-bottom: 10px; }
.pet-list { display: flex; flex-wrap: wrap; gap: 20px; }
.pet-card {
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 16px;
  width: 300px;
  background: #fafafa;
  display: flex;
  gap: 16px;
}
.pet-img-wrapper { flex-shrink: 0; }
.pet-img {
  max-width: 80px;
  max-height: 80px;
  object-fit: cover;
  border-radius: 6px;
  border: 1px solid #ddd;
}
.pet-info { flex: 1; display: flex; flex-direction: column; gap: 6px; }
</style>
