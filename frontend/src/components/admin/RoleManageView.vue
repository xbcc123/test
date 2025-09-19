<template>
  <div>
    <a-card title="角色管理">
      <a-input-search v-model:value="searchKeyword" @search="fetchRoles" placeholder="搜索角色名" style="width:300px; margin-bottom: 16px;" />
      <a-button type="primary" @click="openEditModal(null)" style="margin-bottom:12px;">新增角色</a-button>
      <a-table :columns="columns" :data-source="roles" rowKey="id" :loading="loading">
        <template #action="{ record }">
          <a @click="openPermissionModal(record)">分配权限</a>
          <a-divider type="vertical" />
          <a @click="openEditModal(record)">编辑</a>
          <a-divider type="vertical" />
          <a @click="deleteRole(record)">删除</a>
        </template>
      </a-table>
      <a-modal v-model:visible="permissionModalVisible" title="分配权限" @ok="handleAssignPermissions">
        <a-select v-model:value="selectedPermissions" mode="multiple" style="width:100%">
          <a-select-option v-for="perm in allPermissions" :key="perm.id" :value="perm.id">{{ perm.name }}</a-select-option>
        </a-select>
      </a-modal>
      <a-modal v-model:visible="editModalVisible" :title="editRoleForm.id ? '编辑角色' : '新增角色'" @ok="handleEditRole">
        <a-form :model="editRoleForm">
          <a-form-item label="角色名"><a-input v-model:value="editRoleForm.name" /></a-form-item>
          <a-form-item label="描述"><a-input v-model:value="editRoleForm.description" /></a-form-item>
        </a-form>
      </a-modal>
    </a-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from '@/utils/axios'
import { message } from 'ant-design-vue'

const roles = ref([])
const allPermissions = ref([])
const loading = ref(false)
const searchKeyword = ref('')
const columns = [
  { title: 'ID', dataIndex: 'id' },
  { title: '角色名', dataIndex: 'name' },
  { title: '描述', dataIndex: 'description' },
  { title: '权限', dataIndex: 'permissions', customRender: ({ record }) => record.permissions?.map(p => p.name).join(', ') },
  { title: '操作', key: 'action', slots: { customRender: 'action' } }
]
const permissionModalVisible = ref(false)
const selectedRole = ref(null)
const selectedPermissions = ref([])
const editModalVisible = ref(false)
const editRoleForm = ref({ id: null, name: '', description: '' })

const fetchRoles = async () => {
  loading.value = true
  try {
    const res = await axios.get('/roles', { params: { keyword: searchKeyword.value } })
    roles.value = res
  } catch (e) {
    message.error('获取角色列表失败')
  } finally {
    loading.value = false
  }
}
const fetchAllPermissions = async () => {
  allPermissions.value = await axios.get('/permissions')
}
const openPermissionModal = (role) => {
  selectedRole.value = role
  selectedPermissions.value = role.permissions?.map(p => p.id) || []
  permissionModalVisible.value = true
}
const handleAssignPermissions = async () => {
  loading.value = true
  try {
    await axios.put(`/roles/${selectedRole.value.id}/permissions`, selectedPermissions.value)
    message.success('分配权限成功')
    permissionModalVisible.value = false
    fetchRoles()
  } catch (e) {
    message.error('分配权限失败')
  } finally {
    loading.value = false
  }
}
const openEditModal = (role) => {
  if (role) {
    editRoleForm.value = { ...role }
  } else {
    editRoleForm.value = { id: null, name: '', description: '' }
  }
  editModalVisible.value = true
}
const handleEditRole = async () => {
  loading.value = true
  try {
    if (editRoleForm.value.id) {
      await axios.put(`/roles/${editRoleForm.value.id}`, editRoleForm.value)
      message.success('编辑成功')
    } else {
      await axios.post('/roles', editRoleForm.value)
      message.success('新增成功')
    }
    editModalVisible.value = false
    fetchRoles()
  } catch (e) {
    message.error('保存失败')
  } finally {
    loading.value = false
  }
}
const deleteRole = async (role) => {
  loading.value = true
  try {
    await axios.delete(`/roles/${role.id}`)
    message.success('删除成功')
    fetchRoles()
  } catch (e) {
    message.error('删除失败')
  } finally {
    loading.value = false
  }
}
onMounted(() => {
  fetchRoles()
  fetchAllPermissions()
})
</script>

