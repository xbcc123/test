<template>
  <div class="space-y-10">
    <!-- Banner 区块 -->
    <div class="w-full h-64 bg-gradient-to-r from-blue-900 to-blue-500 rounded-2xl flex items-center px-16 mb-8">
      <div class="text-white max-w-xl">
        <h1 class="text-4xl font-bold mb-4">企业数字化解决方案专家</h1>
        <p class="text-lg mb-6 opacity-90">一站式企业服务平台，提供智能化系统集成、云计算服务与专业技术支持，加速企业转型升级。</p>
        <Button class="btn btn-primary px-8 py-3 font-bold">立即咨询</Button>
      </div>
    </div>

    <!-- 推荐文章区块 -->
    <Card customClass="mb-8">
      <h3 class="text-xl font-bold text-blue-800 mb-4">推荐文章</h3>
      <ul>
        <li v-for="a in articles" :key="a.id" class="py-2 border-b last:border-b-0 border-blue-50 text-blue-700 hover:bg-blue-50 px-2 cursor-pointer">
          {{ a.title }}
        </li>
      </ul>
    </Card>

    <!-- 热门宠物区块 -->
    <Card customClass="mb-8">
      <h3 class="text-xl font-bold text-green-800 mb-4">热门宠物</h3>
      <ul>
        <li v-for="c in cats" :key="c.id" class="py-2 border-b last:border-b-0 border-green-50 text-green-700 hover:bg-green-50 px-2 cursor-pointer">
          {{ c.name }}<span v-if="c.breed">（{{ c.breed }}）</span>
        </li>
      </ul>
    </Card>

    <!-- 预留：服务、社区、百科、个人中心等区块 -->
    <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
      <Card>
        <h3 class="text-xl font-bold text-orange-800 mb-4">服务优势（示例）</h3>
        <div class="text-gray-600">这里可以展示企业服务、云计算、安全等核心能力。</div>
      </Card>
      <Card>
        <h3 class="text-xl font-bold text-purple-800 mb-4">社区互动（示例）</h3>
        <div class="text-gray-600">这里可以展示社区热门话题、讨论等。</div>
      </Card>
    </div>
    <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
      <Card>
        <h3 class="text-xl font-bold text-blue-800 mb-4">百科精选（示例）</h3>
        <div class="text-gray-600">这里可以展示百科词条、知识库等。</div>
      </Card>
      <Card>
        <h3 class="text-xl font-bold text-green-800 mb-4">个人中心（示例）</h3>
        <div class="text-gray-600">这里可以展示个人信息、统计、兴趣等。</div>
      </Card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from '../utils/axios'
import Card from './ui/Card.vue'
import Button from './ui/Button.vue'

const articles = ref([])
const cats = ref([])

const loadHomeArticles = async () => {
  try {
    const res = await axios.get('/articles')
    articles.value = (res.data || []).slice(0, 5)
  } catch {}
  try {
    const res = await axios.get('/cats')
    cats.value = (res.data || []).slice(0, 5)
  } catch {}
}

onMounted(() => {
  loadHomeArticles()
})
</script>
