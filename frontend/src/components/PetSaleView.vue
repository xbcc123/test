<template>
  <section class="petsale-section">
    <h2 class="petsale-title">宠物出售信息</h2>
    <a-button type="primary" @click="goPublish" style="margin-bottom: 16px;">发布宠物</a-button>
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

const openDetail = (pet) => {
  currentPet.value = pet
  showDetail.value = true
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
