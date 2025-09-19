<template>
  <div>
    <a-alert v-if="error" type="error" :message="error" show-icon class="mb-4" />
    <a-form
      :model="form"
      layout="inline"
      @finish="savePost"
      class="mb-4 bg-white p-4 rounded shadow"
    >
      <a-form-item label="用户ID" name="userId" :rules="[{ required: true, message: '请输入用户ID' }]">
        <a-input v-model:value="form.userId" style="width:120px" />
      </a-form-item>
      <a-form-item label="类型" name="type" :rules="[{ required: true, message: '请输入类型' }]">
        <a-input v-model:value="form.type" style="width:100px" />
      </a-form-item>
      <a-form-item label="内容" name="content" :rules="[{ required: true, message: '请输入内容' }]">
        <a-input v-model:value="form.content" style="width:220px" />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit">{{form.id ? '保存' : '新增'}}</a-button>
        <a-button style="margin-left:8px" @click="resetForm">重置</a-button>
      </a-form-item>
    </a-form>
    <a-table :columns="columns" :data-source="posts" row-key="id" bordered :pagination="false">
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'action'">
          <a-button type="link" @click="editPost(record)">编辑</a-button>
          <a-popconfirm title="确定要删除吗？" @confirm="deletePost(record.id)">
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
import axios from '@/utils/axios'

const posts = ref([])
const error = ref('')
const form = ref({ id: '', userId: '', type: '', content: '' })

const columns = [
  { title: 'ID', dataIndex: 'id', key: 'id' },
  { title: '用户ID', dataIndex: 'userId', key: 'userId' },
  { title: '类型', dataIndex: 'type', key: 'type' },
  { title: '内容', dataIndex: 'content', key: 'content' },
  { title: '操作', key: 'action' },
]

function loadPosts() {
  error.value = ''
  axios.get('/posts').then(res => {
    posts.value = res || []
  }).catch(() => { error.value = '加载失败' })
}
function savePost() {
  error.value = ''
  const data = { ...form.value }
  const method = data.id ? 'put' : 'post'
  const url = data.id ? `/posts/${data.id}` : '/posts'
  axios[method](url, data).then(() => {
    resetForm()
    loadPosts()
  }).catch(() => { error.value = '保存失败' })
}
function editPost(p) {
  form.value = { ...p }
}
function resetForm() {
  form.value = { id: '', userId: '', type: '', content: '' }
}
function deletePost(id) {
  axios.delete(`/posts/${id}`).then(() => {
    loadPosts()
  }).catch(() => { error.value = '删除失败' })
}
onMounted(loadPosts)
</script>

<style scoped>
.mb-4 { margin-bottom: 16px; }
</style>
