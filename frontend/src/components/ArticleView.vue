<template>
  <section class="article-section">
    <a-typography-title level="2">资讯中心</a-typography-title>
    <a-alert v-if="error" type="error" :message="error" show-icon style="margin-bottom: 16px;" />
    <a-collapse accordion>
      <a-collapse-panel v-for="(list, category) in groupedArticles" :key="category" :header="`${category}（共${list.length}条）`">
        <a-list :data-source="list" bordered>
          <template #renderItem="{ item }">
            <a-list-item style="display:flex;align-items:center;justify-content:space-between;">
              <div>
                <a :href="`#/articles/${item.id}`"><b>{{ item.title }}</b></a>
                <span v-if="item.createTime" class="article-time" style="margin-left:8px;color:#888;">{{ formatDate(item.createTime) }}</span>
              </div>
              <LikeButton :postId="item.id" />
            </a-list-item>
          </template>
        </a-list>
      </a-collapse-panel>
    </a-collapse>
  </section>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from '../utils/axios'
import { Alert as AAlert, Collapse as ACollapse, CollapsePanel as ACollapsePanel, List as AList, Typography } from 'ant-design-vue'
import LikeButton from './ui/LikeButton.vue'

const { Title } = Typography

const articles = ref([])
const error = ref('')

function formatDate(dateStr) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString()
}

const loadArticles = async () => {
  error.value = ''
  try {
    const res = await axios.get('/articles')
    articles.value = res || []
  } catch (e) {
    error.value = '加载失败'
  }
}
onMounted(loadArticles)

const groupedArticles = computed(() => {
  const groups = {}
  for (const a of articles.value) {
    const cat = a.category || '未分类'
    if (!groups[cat]) groups[cat] = []
    groups[cat].push(a)
  }
  return groups
})
</script>

<style scoped>
.article-section { padding: 20px; }
.article-time { font-size: 13px; }
</style>
