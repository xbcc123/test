<template>
  <div>
    <a-card title="权限管理">
      <a-button type="primary" @click="openAddModal" style="margin-bottom:12px;">新增权限</a-button>
      <a-input-search
        v-model="searchKeyword"
        placeholder="搜索权限名"
        @search="fetchPermissions"
        style="margin-bottom: 12px;"
      />
      <a-table :columns="columns" :data-source="permissions" row-key="id" :loading="loading">
        <template #action="{ record }">
          <a @click="editPermission(record)">编辑</a>
          <a-divider type="vertical" />
          <a @click="deletePermission(record)">删除</a>
        </template>
      </a-table>
      <a-modal v-model:visible="editModalVisible" :title="editPermissionForm.id ? '编辑权限' : '新增权限'" @ok="handleEditPermission">
        <a-form :model="editPermissionForm">
          <a-form-item label="权限名"><a-input v-model:value="editPermissionForm.name" /></a-form-item>
          <a-form-item label="描述"><a-input v-model:value="editPermissionForm.description" /></a-form-item>
        </a-form>
      </a-modal>
    </a-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from '@/utils/axios'
import { message } from 'ant-design-vue'

const permissions = ref([])
const loading = ref(false)
const searchKeyword = ref('')
const columns = [
  { title: 'ID', dataIndex: 'id' },
  { title: '权限名', dataIndex: 'name' },
  { title: '描述', dataIndex: 'description' },
  { title: '操作', key: 'action', slots: { customRender: 'action' } }
]
const editModalVisible = ref(false)
const editPermissionForm = ref({ id: null, name: '', description: '' })

const fetchPermissions = async () => {
  loading.value = true
  try {
    const res = await axios.get('/permissions', { params: { keyword: searchKeyword.value } })
    permissions.value = res
  } catch (e) {
    message.error('获取权限列表失败')
  } finally {
    loading.value = false
  }
}
const openAddModal = () => {
  editPermissionForm.value = { id: null, name: '', description: '' }
  editModalVisible.value = true
}
const editPermission = (perm) => {
  editPermissionForm.value = { ...perm }
  editModalVisible.value = true
}
const handleEditPermission = async () => {
  loading.value = true
  try {
    if (editPermissionForm.value.id) {
      await axios.put(`/permissions/${editPermissionForm.value.id}`, editPermissionForm.value)
      message.success('编辑成功')
    } else {
      await axios.post('/permissions', editPermissionForm.value)
      message.success('新增成功')
    }
    editModalVisible.value = false
    fetchPermissions()
  } catch (e) {
    message.error('保存失败')
  } finally {
    loading.value = false
  }
}
const deletePermission = async (perm) => {
  loading.value = true
  try {
    await axios.delete(`/permissions/${perm.id}`)
    message.success('删除成功')
    fetchPermissions()
  } catch (e) {
    message.error('删除失败')
  } finally {
    loading.value = false
  }
}
onMounted(() => {
  fetchPermissions()
})
</script>
