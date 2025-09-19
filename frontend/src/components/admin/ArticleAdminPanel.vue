<template>
  <div>
    <a-alert v-if="error" type="error" :message="error" show-icon class="mb-4" />
    <a-form
      :model="form"
      layout="inline"
      @finish="saveArticle"
      class="mb-4 bg-white p-4 rounded shadow"
    >
      <a-form-item label="标题" name="title" :rules="[{ required: true, message: '请输入标题' }]">
        <a-input v-model:value="form.title" style="width:160px" />
      </a-form-item>
      <a-form-item label="分类" name="category" :rules="[{ required: true, message: '请输入分类' }]">
        <a-input v-model:value="form.category" style="width:120px" />
      </a-form-item>
      <a-form-item label="内容" name="content" :rules="[{ required: true, message: '请输入内容' }]">
        <a-input v-model:value="form.content" style="width:220px" />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit">{{form.id ? '保存' : '新增'}}</a-button>
        <a-button style="margin-left:8px" @click="resetForm">重置</a-button>
      </a-form-item>
    </a-form>
    <a-table :columns="columns" :data-source="articles" row-key="id" bordered :pagination="false">
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'action'">
          <a-button type="link" @click="editArticle(record)">编辑</a-button>
          <a-popconfirm title="确定要删除吗？" @confirm="deleteArticle(record.id)">
            <a-button type="link" danger>删除</a-button>
          </a-popconfirm>
        </template>
        <template v-else>
          {{ record[column.dataIndex] }}
        </template>
      </template>
    </a-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from '../../utils/axios'

const articles = ref([])
const error = ref('')
const form = ref({ id: '', title: '', category: '', content: '' })

const columns = [
  { title: 'ID', dataIndex: 'id', key: 'id' },
  { title: '标题', dataIndex: 'title', key: 'title' },
  { title: '分类', dataIndex: 'category', key: 'category' },
  { title: '内容', dataIndex: 'content', key: 'content' },
  { title: '操作', key: 'action' },
]

function loadArticles() {
  error.value = ''
  axios.get('/articles').then(res => {
    articles.value = res || []
  }).catch(() => { error.value = '加载失败' })
}
function saveArticle() {
  error.value = ''
  const data = { ...form.value }
  const method = data.id ? 'put' : 'post'
  const url = data.id ? `/articles/${data.id}` : '/articles'
  axios[method](url, data).then(() => {
    resetForm()
    loadArticles()
  }).catch(() => { error.value = '保存失败' })
}
function editArticle(a) {
  form.value = { ...a }
}
function resetForm() {
  form.value = { id: '', title: '', category: '', content: '' }
}
function deleteArticle(id) {
  axios.delete(`/articles/${id}`).then(() => {
    loadArticles()
  }).catch(() => { error.value = '删除失败' })
}
onMounted(loadArticles)
</script>

<style scoped>
.mb-4 { margin-bottom: 16px; }
</style>
