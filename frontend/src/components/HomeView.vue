<template>
  <div>
    <!-- Banner 区块 -->
    <a-card class="mb-8 banner-card" :bordered="false" style="background: linear-gradient(90deg, #1e3a8a 0%, #3b82f6 100%); color: #fff;">
      <div style="max-width: 600px;">
        <a-typography-title level="1" style="color:#fff;">企业数字化解决方案专家</a-typography-title>
        <a-typography-paragraph style="color:#fff;opacity:0.9;">
          一站式企业服务平台，提供智能化系统集成、云计算服务与专业技术支持，加速企业转型升级。
        </a-typography-paragraph>
        <a-button type="primary" size="large">立即咨询</a-button>
      </div>
    </a-card>

    <!-- 推荐文章区块 -->
    <a-card class="mb-8">
      <a-typography-title level="3" style="color:#1e40af;">推荐文章</a-typography-title>
      <a-list :data-source="articles" bordered>
        <template #renderItem="{ item }">
          <a-list-item>
            <a>{{ item.title }}</a>
          </a-list-item>
        </template>
      </a-list>
    </a-card>

    <!-- 热门宠物区块 -->
    <a-card class="mb-8">
      <a-typography-title level="3" style="color:#047857;">热门宠物</a-typography-title>
      <a-list :data-source="cats" bordered>
        <template #renderItem="{ item }">
          <a-list-item>
            <a>{{ item.name }}<span v-if="item.breed">（{{ item.breed }}）</span></a>
          </a-list-item>
        </template>
      </a-list>
    </a-card>

    <!-- 其他区块 -->
    <a-row gutter="16">
      <a-col :span="12">
        <a-card>
          <a-typography-title level="3" style="color:#ea580c;">服务优势（示例）</a-typography-title>
          <div style="color:#666;">这里可以展示企业服务、云计算、安全等核心能力。</div>
        </a-card>
      </a-col>
      <a-col :span="12">
        <a-card>
          <a-typography-title level="3" style="color:#7c3aed;">社区互动（示例）</a-typography-title>
          <div style="color:#666;">这里可以展示社区热门话题、讨论等。</div>
        </a-card>
      </a-col>
    </a-row>
    <a-row gutter="16" class="mt-4">
      <a-col :span="12">
        <a-card>
          <a-typography-title level="3" style="color:#1e40af;">百科精选（示例）</a-typography-title>
          <div style="color:#666;">这里可以展示百科词条、知识库等。</div>
        </a-card>
      </a-col>
      <a-col :span="12">
        <a-card>
          <a-typography-title level="3" style="color:#047857;">个人中心（示例）</a-typography-title>
          <div style="color:#666;">这里可以展示个人信息、统计、兴趣等。</div>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from '../utils/axios'

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
  display: flex;
  align-items: center;
  padding-left: 48px;
  background: linear-gradient(90deg, #1e3a8a 0%, #3b82f6 100%) !important;
}
.mt-4 { margin-top: 16px; }
</style>
