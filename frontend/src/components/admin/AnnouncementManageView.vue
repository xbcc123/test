<template>
  <div class="announcement-manage">
    <a-card title="公告管理" :loading="loading">
      <div class="toolbar">
        <a-button type="primary" @click="openEdit(null)">新增公告</a-button>
        <a-select v-model:value="filters.status" allow-clear placeholder="状态" style="width:130px" @change="reload">
          <a-select-option value="DRAFT">草稿</a-select-option>
          <a-select-option value="PUBLISHED">已发布</a-select-option>
        </a-select>
        <a-select v-model:value="filters.type" allow-clear placeholder="类型" style="width:130px" @change="reload">
          <a-select-option value="SYSTEM">系统</a-select-option>
          <a-select-option value="GENERAL">通用</a-select-option>
          <a-select-option value="OTHER">其它</a-select-option>
        </a-select>
        <a-input-search v-model:value="filters.keyword" placeholder="搜索标题/内容" style="width:240px" @search="reload" />
        <a-button size="small" @click="resetFilters">重置</a-button>
      </div>
      <a-table :data-source="rows" :columns="columns" rowKey="id" size="middle" :pagination="false">
        <template #title="{ record }">
          <span class="title-cell">
            <a-tag :color="typeColor(record?.type)" style="margin-right:4px">{{ typeLabel(record?.type) }}</a-tag>
            <a @click="showDetail(record)">{{ record?.title }}</a>
          </span>
        </template>
        <template #type="{ record }">
          <span>{{ typeLabel(record?.type) }}</span>
        </template>
        <template #status="{ record }">
          <a-tag :color="record?.status==='PUBLISHED'?'green':'default'">{{ statusLabel(record?.status) }}</a-tag>
        </template>
        <template #action="{ record }">
          <a v-if="record" @click="openEdit(record)">编辑</a>
          <a-divider type="vertical" v-if="record" />
          <a v-if="record && record.status==='DRAFT'" @click="publish(record)">发布</a>
          <a v-else-if="record" @click="unpublish(record)">撤回</a>
          <a-divider type="vertical" v-if="record" />
          <a-popconfirm v-if="record" title="确定删除?" @confirm="removeRow(record)"><a style="color:#d00;">删除</a></a-popconfirm>
        </template>
      </a-table>
      <div class="pagination-bar">
        <a-pagination size="small" :current="page" :pageSize="size" :total="total" :show-total="t=>`共 ${t} 条`" @change="changePage" />
      </div>
    </a-card>

    <!-- 编辑弹窗 -->
    <a-modal v-model:visible="editVisible" :title="form.id ? '编辑公告' : '新增公告'" @ok="submit" @cancel="closeEdit" width="780px" :confirm-loading="submitting">
      <a-form :model="form" layout="vertical">
        <a-form-item label="标题" required>
          <a-input v-model:value="form.title" maxlength="150" show-count placeholder="请输入标题" />
        </a-form-item>
        <a-form-item label="类型" required>
          <a-select v-model:value="form.type" placeholder="选择类型">
            <a-select-option value="SYSTEM">系统</a-select-option>
            <a-select-option value="GENERAL">通用</a-select-option>
            <a-select-option value="OTHER">其它</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="发布时间 (可为空, 为空则立即或发布时刻)" >
          <a-date-picker v-model:value="publishPicker" show-time valueFormat="YYYY-MM-DD HH:mm:ss" style="width:100%" />
        </a-form-item>
        <a-form-item label="过期时间 (可为空)">
          <a-date-picker v-model:value="expirePicker" show-time valueFormat="YYYY-MM-DD HH:mm:ss" style="width:100%" />
        </a-form-item>
        <a-form-item label="状态" help="保存为草稿或直接选择已发布 (若 publishTime 晚于当前则仍视为计划中, 公共列表不会显示)">
          <a-select v-model:value="form.status">
            <a-select-option value="DRAFT">草稿</a-select-option>
            <a-select-option value="PUBLISHED">已发布</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="内容" required>
          <a-textarea v-model:value="form.content" rows="10" placeholder="支持换行, 可简易使用 Markdown 语法 (前端暂不渲染)" />
        </a-form-item>
        <a-alert type="info" show-icon :message="contentStatsMsg" />
      </a-form>
    </a-modal>

    <!-- 详情弹窗 -->
    <a-modal v-model:visible="detailVisible" :title="detail?.title" width="720px" :footer="null">
      <div style="font-size:12px;color:#666;margin-bottom:8px;">
        状态: {{ statusLabel(detail?.status) }} | 类型: {{ typeLabel(detail?.type) }} | 发布时间: {{ detail?.publishTime || '-' }}
      </div>
      <div class="detail-content" v-html="renderContent(detail?.content)"></div>
    </a-modal>
  </div>
</template>
<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import axios from '@/utils/axios'
import { message } from 'ant-design-vue'

// 列表数据
const rows = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const loading = ref(false)

// 过滤器
const filters = reactive({ status:null, type:null, keyword:'' })

// 编辑表单
const editVisible = ref(false)
const submitting = ref(false)
const form = reactive({ id:null, title:'', content:'', type:'GENERAL', status:'DRAFT', publishTime:null, expireTime:null })
const publishPicker = ref(null)
const expirePicker = ref(null)
const detailVisible = ref(false)
const detail = ref(null)

const columns = [
  { title:'标题', dataIndex:'title', key:'title', ellipsis:true, slots:{ customRender:'title' } },
  { title:'状态', dataIndex:'status', key:'status', width:110, slots:{ customRender:'status' } },
  { title:'类型', dataIndex:'type', key:'type', width:110, slots:{ customRender:'type' } },
  { title:'发布时间', dataIndex:'publishTime', width:160 },
  { title:'更新时间', dataIndex:'updateTime', width:160 },
  { title:'操作', key:'action', width:200, slots:{ customRender:'action' } }
]

function typeLabel(t){ return t==='SYSTEM'?'系统': t==='GENERAL'?'通用': t==='OTHER'?'其它': '-' }
function typeColor(t){ return t==='SYSTEM'?'red': t==='GENERAL'?'blue':'default' }
function statusLabel(s){ return s==='PUBLISHED'?'已发布':'草稿' }
function renderContent(c){ if(!c) return '<i>无内容</i>'; return c.replace(/\n/g,'<br/>') }
const contentStatsMsg = computed(()=>`当前字数: ${(form.content||'').length}`)

async function load(){
  loading.value = true
  try {
    const res = await axios.get('/admin/announcements', { params:{ page: page.value-1, size: size.value, status: filters.status, type: filters.type, keyword: filters.keyword } })
    if(res && Array.isArray(res.content)){
      rows.value = res.content
      total.value = res.totalElements || 0
    } else if (Array.isArray(res)) { // fallback
      rows.value = res
      total.value = res.length
    } else { rows.value=[]; total.value=0 }
  } catch(e){ message.error('加载失败') } finally { loading.value=false }
}
function changePage(p){ page.value = p; load() }
function reload(){ page.value = 1; load() }
function resetFilters(){ filters.status=null; filters.type=null; filters.keyword=''; reload() }

function openEdit(record){
  if(record){
    form.id = record.id
    form.title = record.title
    form.content = record.content
    form.type = record.type || 'GENERAL'
    form.status = record.status || 'DRAFT'
    form.publishTime = record.publishTime
    form.expireTime = record.expireTime
    publishPicker.value = record.publishTime
    expirePicker.value = record.expireTime
  } else {
    form.id = null
    form.title=''
    form.content=''
    form.type='GENERAL'
    form.status='DRAFT'
    form.publishTime=null
    form.expireTime=null
    publishPicker.value=null
    expirePicker.value=null
  }
  editVisible.value = true
}
function closeEdit(){ editVisible.value=false }

async function submit(){
  if(!form.title) return message.warning('请输入标题')
  if(!form.content) return message.warning('请输入内容')
  submitting.value = true
  form.publishTime = publishPicker.value
  form.expireTime = expirePicker.value
  try {
    if(form.id){
      await axios.put(`/admin/announcements/${form.id}`, form)
      message.success('更新成功')
    } else {
      await axios.post('/admin/announcements', form)
      message.success('创建成功')
    }
    editVisible.value=false
    load()
  } catch(e){ message.error('保存失败') } finally { submitting.value=false }
}

async function publish(record){
  try { await axios.put(`/admin/announcements/${record.id}/publish`); message.success('已发布'); load() } catch{ message.error('发布失败') }
}
async function unpublish(record){
  try { await axios.put(`/admin/announcements/${record.id}/unpublish`); message.success('已撤回'); load() } catch{ message.error('撤回失败') }
}
async function removeRow(record){
  try { await axios.delete(`/admin/announcements/${record.id}`); message.success('已删除'); load() } catch{ message.error('删除失败') }
}
async function showDetail(record){
  try { const d = await axios.get(`/admin/announcements/${record.id}`); detail.value = d; detailVisible.value=true } catch{ message.error('获取详情失败') }
}

onMounted(load)
</script>
<style scoped>
.announcement-manage .toolbar { display:flex; gap:8px; flex-wrap:wrap; margin-bottom:12px; }
.pagination-bar { margin-top:16px; text-align:right; }
.title-cell { display:inline-flex; align-items:center; gap:4px; }
.detail-content { line-height:1.6; word-break:break-word; }
</style>
