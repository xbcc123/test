<template>
  <div>
    <!-- Banner 区块 -->
    <Card class="banner-card mb-8 flex items-center justify-between">
      <div class="banner-content">
        <h1 class="title-xl mb-2">企业数字化解决方案专家</h1>
        <p class="text-lg mb-4" style="color:#fff;opacity:0.92;max-width:420px;">
          一站式企业服务平台，提供智能化系统集成、云计算服务与专业技术支持，加速企业转型升级。
        </p>
        <Button variant="primary" size="large">立即咨询</Button>
      </div>
      <!-- 如需自定义 banner 图片，请将图片放入 public/ 目录并替换下方 src -->
      <img src="/favicon.ico" alt="banner" class="banner-img hidden md:block" />
    </Card>

    <!-- 推荐文章区块 -->
    <Card class="mb-8">
      <h2 class="title-lg mb-4">推荐文章</h2>
      <div class="article-list">
        <Card v-for="item in articles" :key="item.id" class="article-mini-card">
          <div class="flex flex-col gap-1">
            <a :href="`#/articles/${item.id}`" class="font-bold text-base hover:underline">{{ item.title }}</a>
            <span class="text-xs text-gray-500">{{ item.createTime ? new Date(item.createTime).toLocaleDateString() : '' }}</span>
          </div>
        </Card>
      </div>
    </Card>

    <!-- 热门宠物区块 -->
    <Card class="mb-8">
      <h2 class="title-lg mb-4">热门宠物</h2>
      <div class="cat-list">
        <Card v-for="item in cats" :key="item.id" class="cat-mini-card flex items-center gap-3">
          <img v-if="item.imgUrl" :src="item.imgUrl" alt="pet" class="cat-img" />
          <div>
            <span class="font-bold">{{ item.name }}</span>
            <span v-if="item.breed" class="text-xs text-gray-500 ml-2">({{ item.breed }})</span>
          </div>
        </Card>
      </div>
    </Card>

    <!-- 其它区块 -->
    <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
      <Card>
        <h3 class="title-md mb-2">服务优势（示例）</h3>
        <div class="text-gray-600">这里可以展示企业服务、云计算、安全等核心能力。</div>
      </Card>
      <Card>
        <h3 class="title-md mb-2">社区互动（示例）</h3>
        <div class="text-gray-600">这里可以展示社区热门话题、讨论等。</div>
      </Card>
      <Card>
        <h3 class="title-md mb-2">百科精选（示例）</h3>
        <div class="text-gray-600">这里可以展示百科词条、知识库等。</div>
      </Card>
      <Card>
        <h3 class="title-md mb-2">个人中心（示例）</h3>
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
    articles.value = (res || []).slice(0, 5)
  } catch {}
  try {
    const res = await axios.get('/cats')
    cats.value = (res || []).slice(0, 5)
  } catch {}
}

onMounted(() => {
  loadHomeArticles()
})
</script>

<style scoped>
.banner-card {
  min-height: 220px;
  background: linear-gradient(90deg, #1e3a8a 0%, #3b82f6 100%) !important;
  color: #fff;
  display: flex;
  align-items: center;
  padding-left: 48px;
  position: relative;
  overflow: hidden;
}
.banner-content {
  z-index: 2;
}
.banner-img {
  width: 180px;
  height: 180px;
  object-fit: contain;
  opacity: 0.95;
  margin-right: 32px;
}
.article-list, .cat-list {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}
.article-mini-card, .cat-mini-card {
  min-width: 140px;
  padding: 0.8rem 1rem;
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-card);
  background: #f9fbfd;
  transition: box-shadow 0.18s, background 0.18s;
}
.article-mini-card:hover, .cat-mini-card:hover {
  background: #f0f5ff;
  box-shadow: var(--shadow-hover);
}
.cat-img {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  object-fit: cover;
  border: 1.5px solid var(--color-border);
}
@media (max-width: 800px) {
  .banner-card { flex-direction: column; align-items: flex-start; padding-left: 18px; }
  .banner-img { margin: 0 0 12px 0; width: 120px; height: 120px; }
  .article-list, .cat-list { flex-direction: column; gap: 10px; }
}
</style>
