<template>
  <div>
    <a-card title="用户管理">
      <div style="margin-bottom:12px;display:flex;gap:8px;align-items:center;">
        <a-input-search v-model:value="searchKeyword" @search="fetchUsers" placeholder="搜索用户名或昵称" style="width:300px;" />
        <a-button @click="batchAssignRoles">批量分配角色</a-button>
        <a-button @click="batchToggleStatus">批量禁用/启用</a-button>
        <a-button @click="batchResetPassword">批量重置密码</a-button>
        <a-button @click="exportCSV">导出</a-button>
      </div>
      <a-table :columns="columns" :data-source="users" rowKey="id" :pagination="pagination" @change="onPageChange" :loading="loading" :row-selection="rowSelection">
        <template #action="{ record }">
          <a @click="openRoleModal(record)">分配角色</a>
          <a-divider type="vertical" />
          <a @click="resetPassword(record)">重置密码</a>
          <a-divider type="vertical" />
          <a @click="toggleStatus(record)">{{ record.status === '正常' ? '禁用' : '启用' }}</a>
        </template>
      </a-table>
      <a-modal v-model:visible="roleModalVisible" title="分配角色" @ok="handleAssignRoles">
        <a-select v-model:value="selectedRoles" mode="multiple" style="width:100%">
          <a-select-option v-for="role in allRoles" :key="role.id" :value="role.id">{{ role.name }}</a-select-option>
        </a-select>
      </a-modal>
    </a-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from '@/utils/axios'
import { message, Modal } from 'ant-design-vue'

const users = ref([])
const allRoles = ref([])
const pagination = ref({ current: 1, pageSize: 10, total: 0 })
const loading = ref(false)
const searchKeyword = ref('')
const columns = [
  { title: 'ID', dataIndex: 'id' },
  { title: '用户名', dataIndex: 'username' },
  { title: '昵称', dataIndex: 'nickname' },
  { title: '状态', dataIndex: 'status' },
  { title: '角色', dataIndex: 'roles', customRender: ({ record }) => record.roles?.map(r => r.name).join(', ') },
  { title: '操作', key: 'action', slots: { customRender: 'action' } }
]
const roleModalVisible = ref(false)
const selectedUser = ref(null)
const selectedRoles = ref([])
const selectedRowKeys = ref([])
const selectedRows = ref([])
const rowSelection = {
  selectedRowKeys: selectedRowKeys.value,
  onChange: (keys, rows) => {
    selectedRowKeys.value = keys
    selectedRows.value = rows
  }
}

const fetchUsers = async (page = 1, size = 10) => {
  loading.value = true
  try {
    const res = await axios.get('/users', { params: { page: page - 1, size, keyword: searchKeyword.value } })
    users.value = res.content
    pagination.value.total = res.totalElements
    pagination.value.current = page
  } catch (e) {
    message.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}
const fetchAllRoles = async () => {
  allRoles.value = await axios.get('/roles')
}
const onPageChange = (p) => {
  fetchUsers(p.current, p.pageSize)
}
const openRoleModal = (user) => {
  selectedUser.value = user
  selectedRoles.value = user.roles?.map(r => r.id) || []
  roleModalVisible.value = true
}
const handleAssignRoles = async () => {
  loading.value = true
  try {
    await axios.put(`/users/${selectedUser.value.id}/roles`, selectedRoles.value)
    message.success('分配角色成功')
    roleModalVisible.value = false
    fetchUsers(pagination.value.current, pagination.value.pageSize)
  } catch (e) {
    message.error('分配角色失败')
  } finally {
    loading.value = false
  }
}
const resetPassword = async (user) => {
  loading.value = true
  try {
    await axios.put(`/users/${user.username}/reset-password`, { newPassword: '123456' })
    message.success('密码已重置为123456')
    fetchUsers(pagination.value.current, pagination.value.pageSize)
  } catch (e) {
    message.error('重置密码失败')
  } finally {
    loading.value = false
  }
}
const toggleStatus = async (user) => {
  loading.value = true
  try {
    const newStatus = user.status === '正常' ? '禁用' : '正常'
    await axios.put(`/users/${user.username}/status`, { status: newStatus })
    message.success('操作成功')
    fetchUsers(pagination.value.current, pagination.value.pageSize)
  } catch (e) {
    message.error('操作失败')
  } finally {
    loading.value = false
  }
}
const batchAssignRoles = () => {
  if (!selectedRows.value.length) return message.warning('请先选择用户')
  openRoleModal({
    id: selectedRows.value.map(u => u.id),
    roles: []
  })
}
const batchToggleStatus = () => {
  if (!selectedRows.value.length) return message.warning('请先选择用户')
  Modal.confirm({
    title: '批量操作',
    content: '确定要批量切换这些用户的状态吗？',
    onOk: async () => {
      loading.value = true
      try {
        for (const user of selectedRows.value) {
          const newStatus = user.status === '正常' ? '禁用' : '正常'
          await axios.put(`/users/${user.username}/status`, { status: newStatus })
        }
        message.success('批量操作成功')
        fetchUsers(pagination.value.current, pagination.value.pageSize)
      } catch (e) {
        message.error('批量操作失败')
      } finally {
        loading.value = false
      }
    }
  })
}
const batchResetPassword = () => {
  if (!selectedRows.value.length) return message.warning('请先选择用户')
  Modal.confirm({
    title: '批量重置密码',
    content: '确定要将选中用户密码重置为123456？',
    onOk: async () => {
      loading.value = true
      try {
        for (const user of selectedRows.value) {
          await axios.put(`/users/${user.username}/reset-password`, { newPassword: '123456' })
        }
        message.success('批量重置成功')
        fetchUsers(pagination.value.current, pagination.value.pageSize)
      } catch (e) {
        message.error('批量重置失败')
      } finally {
        loading.value = false
      }
    }
  })
}
const exportCSV = () => {
  const header = ['ID', '用户名', '昵称', '状态', '角色']
  const data = (selectedRows.value.length ? selectedRows.value : users.value).map(u => [
    u.id, u.username, u.nickname, u.status, (u.roles||[]).map(r=>r.name).join(';')
  ])
  const csv = [header, ...data].map(row => row.map(v => `"${v ?? ''}"`).join(',')).join('\n')
  const blob = new Blob([csv], { type: 'text/csv' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = '用户列表.csv'
  a.click()
  URL.revokeObjectURL(url)
}
onMounted(() => {
  fetchUsers()
  fetchAllRoles()
})
</script>
