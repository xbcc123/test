<template>
  <div class="announcement-list max-w-900 mx-auto">
    <a-card title="公告通知" :loading="loading">
      <div style="display:flex;gap:8px;flex-wrap:wrap;margin-bottom:12px;align-items:center;">
        <a-select v-model:value="query.type" allow-clear placeholder="类型" style="width:140px" @change="reload">
          <a-select-option value="SYSTEM">系统</a-select-option>
          <a-select-option value="GENERAL">通用</a-select-option>
          <a-select-option value="OTHER">其它</a-select-option>
        </a-select>
        <a-input-search v-model:value="query.keyword" placeholder="搜索标题/内容" style="width:240px" @search="reload" />
        <a-button type="default" @click="resetFilters" size="small">重置</a-button>
      </div>
      <a-list :data-source="announcements" :locale="{emptyText:'暂无公告'}">
        <template #renderItem="{ item }">
          <a-list-item class="ann-item" @click="openDetail(item)" style="cursor:pointer;">
            <a-list-item-meta>
              <template #title>
                <span>{{ item.title }} <a-tag :color="tagColor(item.type)">{{ mapType(item.type) }}</a-tag></span>
              </template>
              <template #description>
                <div style="font-size:12px;color:#888;">发布时间: {{ item.publishTime || '—' }}</div>
              </template>
            </a-list-item-meta>
          </a-list-item>
        </template>
      </a-list>
      <div style="margin-top:16px;text-align:right;">
        <a-pagination
          size="small"
          :current="page"
          :pageSize="size"
          :total="total"
          :show-total="t=>`共 ${t} 条`"
          @change="onPageChange" />
      </div>
    </a-card>
    <a-modal v-model:visible="detailVisible" :title="detail?.title" width="720px" :footer="null">
      <div class="meta" style="color:#666;font-size:12px;margin-bottom:8px;">
        发布时间：{{ detail?.publishTime || '-' }}
        <span v-if="detail?.type" style="margin-left:12px;">类型：{{ mapType(detail?.type) }}</span>
      </div>
      <div class="content" v-html="formatContent(detail?.content)"></div>
    </a-modal>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import axios from '@/utils/axios'
import { message } from 'ant-design-vue'

const announcements = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const loading = ref(false)
const detailVisible = ref(false)
const detail = ref(null)
const query = ref({ type:null, keyword:'' })

function mapType(t){ return t==='SYSTEM'?'系统': t==='GENERAL'?'通用': t==='OTHER'?'其它': (t||'-') }
function tagColor(t){ return t==='SYSTEM'?'red': t==='GENERAL'?'blue':'default' }
function formatContent(c){ if(!c) return '<i>无内容</i>'; return c.replace(/\n/g,'<br/>') }

async function load(){
  loading.value = true
  try {
    const res = await axios.get('/api/announcements', { params:{ page: page.value-1, size: size.value, type: query.value.type, keyword: query.value.keyword } })
    if(res && Array.isArray(res.content)){
      announcements.value = res.content
      total.value = res.totalElements || 0
    } else if (Array.isArray(res)) {
      announcements.value = res
      total.value = res.length
    } else {
      announcements.value = []
      total.value = 0
    }
  } catch(e){ message.error('加载公告失败') } finally { loading.value=false }
}
function onPageChange(p){ page.value = p; load() }
function reload(){ page.value = 1; load() }
function resetFilters(){ query.value={ type:null, keyword:'' }; reload() }
async function openDetail(item){
  try {
    const res = await axios.get(`/api/announcements/${item.id}`)
    detail.value = res
    detailVisible.value = true
  } catch { message.error('获取详情失败') }
}

onMounted(load)
</script>
<style scoped>
.ann-item:hover { background:#fafafa; }
.content { line-height:1.6; white-space:normal; word-break:break-word; }
</style>
