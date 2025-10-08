<template>
  <section class="petsale-section">
    <h2 class="petsale-title">宠物出售信息</h2>
    <div style="display:flex;gap:12px;align-items:center;margin-bottom:12px;">
      <a-button type="primary" @click="goPublish">发布宠物</a-button>
      <a-button @click="goCart">查看购物车</a-button>
    </div>
    <div class="pet-card-list">
      <a-card
        v-for="pet in pets"
        :key="pet.id"
        class="pet-card"
        hoverable
        @click="openDetail(pet)"
        :cover="pet.imgUrl ? $createElement('img', { attrs: { src: pet.imgUrl, alt: 'pet', style: 'width:100%;height:180px;object-fit:cover;' } }) : null"
      >
        <a-card-meta
          :title="pet.name + '（' + pet.type + '）'"
          :description="'￥' + pet.price + ' | ' + (pet.status || '在售')"
        />
        <div class="pet-card-desc">{{ pet.description?.slice(0, 30) + (pet.description?.length > 30 ? '...' : '') }}</div>
        <div style="margin-top:12px;display:flex;justify-content:space-between;align-items:center;">
          <a-button type="primary" size="small" @click.stop="addToCart(pet)">加入购物车</a-button>
          <a-button size="small" @click.stop="openDetail(pet)">查看详情</a-button>
        </div>
      </a-card>
    </div>
    <a-modal v-model:open="showDetail" title="宠物详情" :footer="null" width="520px">
      <div v-if="currentPet">
        <img v-if="currentPet.imgUrl" :src="currentPet.imgUrl" alt="pet" style="width:100%;max-height:220px;object-fit:cover;margin-bottom:12px;" />
        <h3>{{ currentPet.name }}（{{ currentPet.type }}）</h3>
        <p><b>年龄：</b>{{ currentPet.age }}</p>
        <p><b>价格：</b>￥{{ currentPet.price }}</p>
        <p><b>状态：</b>{{ currentPet.status }}</p>
        <p><b>卖家ID：</b>{{ currentPet.sellerId }}</p>
        <p><b>描述：</b>{{ currentPet.description }}</p>
        <div style="margin-top:12px;">
          <a-button type="primary" @click="addToCart(currentPet)">加入购物车</a-button>
        </div>
      </div>
    </a-modal>
  </section>
</template>

<script setup>
import { ref, onMounted, h, getCurrentInstance } from 'vue'
import { useRouter } from 'vue-router'
import axios from '../utils/axios'
import { message } from 'ant-design-vue'

const pets = ref([])
const router = useRouter()
const showDetail = ref(false)
const currentPet = ref(null)
const { appContext } = getCurrentInstance()

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

const goCart = () => {
  router.push('/cart')
}

const openDetail = (pet) => {
  currentPet.value = pet
  showDetail.value = true
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
        price: Number(pet.price || pet.price || 0),
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

onMounted(loadPets)
</script>

<style scoped>
.petsale-section { padding: 20px; }
.petsale-title { margin-bottom: 10px; }
.pet-card-list {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  margin-bottom: 24px;
}
.pet-card {
  width: 260px;
  margin-bottom: 0;
  cursor: pointer;
  transition: box-shadow 0.2s;
}
.pet-card:hover {
  box-shadow: 0 4px 16px #b3e5fc;
}
.pet-card-desc {
  color: #888;
  font-size: 13px;
  margin-top: 8px;
}
</style>
