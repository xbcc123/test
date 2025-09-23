<template>
  <div>
    <a-card title="用户管理">
      <div style="margin-bottom:12px;display:flex;gap:8px;align-items:center;flex-wrap:wrap;">
        <a-input-search v-model:value="searchKeyword" @search="fetchUsers" placeholder="搜索用户名或昵称" style="width:260px;" />
        <a-button @click="batchAssignRoles">批量分配角色</a-button>
        <a-button @click="batchToggleStatus">批量禁用/启用</a-button>
        <a-button @click="batchResetPassword">批量重置密码</a-button>
        <a-button @click="exportCSV">导出</a-button>
        <a-divider type="vertical" />
        <a-button @click="openDeptModal(null)" type="dashed">分配部门</a-button>
        <a-button @click="openPositionsModal(null)" type="dashed">分配岗位</a-button>
      </div>
      <a-table :columns="columns" :data-source="users" rowKey="id" :pagination="pagination" @change="onPageChange" :loading="loading" :row-selection="rowSelection">
        <template #action="{ record }">
          <a @click="openRoleModal(record)">角色</a>
          <a-divider type="vertical" />
          <a @click="openDeptModal(record)">部门</a>
          <a-divider type="vertical" />
          <a @click="openPositionsModal(record)">岗位</a>
          <a-divider type="vertical" />
          <a @click="resetPassword(record)">重置密码</a>
          <a-divider type="vertical" />
          <a @click="toggleStatus(record)">{{ record.status === '正常' ? '禁用' : '启用' }}</a>
        </template>
        <template #roles="{ record }">
          <span>{{ (record.roles||[]).map(r=>r.name).join(', ') || '-' }}</span>
        </template>
        <template #department="{ record }">
          <span>{{ record.department?.name || '-' }}</span>
        </template>
        <template #positions="{ record }">
          <span>{{ (record.positions||[]).map(p=>p.name).join(', ') || '-' }}</span>
        </template>
      </a-table>

      <!-- 分配角色 Modal -->
      <a-modal v-model:visible="roleModalVisible" title="分配角色" @ok="handleAssignRoles">
        <a-select v-model:value="selectedRoles" mode="multiple" style="width:100%" placeholder="选择角色">
          <a-select-option v-for="role in allRoles" :key="role.id" :value="role.id">{{ role.name }}</a-select-option>
        </a-select>
      </a-modal>

      <!-- 分配部门 Modal -->
      <a-modal v-model:visible="deptModalVisible" title="分配部门" @ok="handleAssignDepartment" @cancel="closeDeptModal">
        <a-tree-select v-model:value="selectedDepartmentId" style="width:100%" allow-clear show-search
          :tree-data="departmentTree" :field-names="{label:'name', value:'id', children:'children'}" tree-default-expand-all
          placeholder="选择部门(可清空)" />
      </a-modal>

      <!-- 分配岗位 Modal -->
      <a-modal v-model:visible="positionsModalVisible" title="分配岗位" @ok="handleAssignPositions" @cancel="closePositionsModal">
        <a-select v-model:value="selectedPositionIds" mode="multiple" style="width:100%" placeholder="选择岗位">
          <a-select-option v-for="pos in allPositions" :key="pos.id" :value="pos.id">{{ pos.name }}</a-select-option>
        </a-select>
      </a-modal>
    </a-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from '@/utils/axios'
import { message, Modal } from 'ant-design-vue'

// 数据源
const users = ref([])
const allRoles = ref([])
const allDepartments = ref([])
const allPositions = ref([])

// 状态
const pagination = ref({ current: 1, pageSize: 10, total: 0 })
const loading = ref(false)
const searchKeyword = ref('')

// 选择
const selectedRowKeys = ref([])
const selectedRows = ref([])
const rowSelection = {
  selectedRowKeys: selectedRowKeys.value,
  onChange: (keys, rows) => {
    selectedRowKeys.value = keys
    selectedRows.value = rows
  }
}

// 列定义
const columns = [
  { title: 'ID', dataIndex: 'id', width: 70 },
  { title: '用户名', dataIndex: 'username', width: 120 },
  { title: '昵称', dataIndex: 'nickname', width: 120 },
  { title: '状态', dataIndex: 'status', width: 90 },
  { title: '角色', dataIndex: 'roles', key: 'roles', slots: { customRender: 'roles' }, width: 180 },
  { title: '部门', dataIndex: 'department', key: 'department', slots: { customRender: 'department' }, width: 140 },
  { title: '岗位', dataIndex: 'positions', key: 'positions', slots: { customRender: 'positions' }, width: 200 },
  { title: '操作', key: 'action', slots: { customRender: 'action' }, width: 320 }
]

// 角色 Modal
const roleModalVisible = ref(false)
const selectedUser = ref(null) // 可能单个或批量 (批量时 id 为数组)
const selectedRoles = ref([])

// 部门 Modal
const deptModalVisible = ref(false)
const selectedDepartmentId = ref(null)

// 岗位 Modal
const positionsModalVisible = ref(false)
const selectedPositionIds = ref([])

// 树形部门
const departmentTree = computed(() => buildDeptTree(allDepartments.value))
function buildDeptTree(list) {
  const map = {}
  list.forEach(d => (map[d.id] = { ...d, children: [] }))
  const roots = []
  list.forEach(d => {
    if (d.parentId && map[d.parentId]) map[d.parentId].children.push(map[d.id])
    else roots.push(map[d.id])
  })
  return roots
}

// 获取数据
const fetchUsers = async (page = 1, size = 10) => {
  loading.value = true
  try {
    const res = await axios.get('/users', { params: { page: page - 1, size, keyword: searchKeyword.value } })
    // 后端当前返回 List<UserDTO> 而非分页对象, 做兼容
    if (Array.isArray(res)) {
      users.value = res
      pagination.value.total = res.length // 简单估算; 若后端改为真实分页再调整
    } else if (res?.content) { // 兼容 Page 结构
      users.value = res.content
      pagination.value.total = res.totalElements || res.content.length
    } else {
      users.value = []
      pagination.value.total = 0
    }
    pagination.value.current = page
  } catch (e) {
    message.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}
const fetchAllRoles = async () => { try { allRoles.value = await axios.get('/roles') } catch { message.error('获取角色失败') } }
const fetchAllDepartments = async () => { try { allDepartments.value = await axios.get('/departments') } catch { message.error('获取部门失败') } }
const fetchAllPositions = async () => { try { allPositions.value = await axios.get('/positions') } catch { message.error('获取岗位失败') } }

// 角色分配
const openRoleModal = (user) => {
  selectedUser.value = user
  selectedRoles.value = Array.isArray(user?.id) ? [] : (user?.roles || []).map(r => r.id)
  roleModalVisible.value = true
}
const handleAssignRoles = async () => {
  loading.value = true
  try {
    // 批量或单个
    if (Array.isArray(selectedUser.value?.id)) {
      for (const uid of selectedUser.value.id) {
        await axios.put(`/users/${uid}/roles`, selectedRoles.value)
      }
    } else {
      await axios.put(`/users/${selectedUser.value.id}/roles`, selectedRoles.value)
    }
    message.success('分配角色成功')
    roleModalVisible.value = false
    fetchUsers(pagination.value.current, pagination.value.pageSize)
  } catch (e) {
    message.error('分配角色失败')
  } finally {
    loading.value = false
  }
}

// 部门分配
function openDeptModal(user) {
  selectedUser.value = user
  selectedDepartmentId.value = user?.department?.id || null
  deptModalVisible.value = true
}
function closeDeptModal() { deptModalVisible.value = false }
async function handleAssignDepartment() {
  if (!selectedUser.value) return
  loading.value = true
  try {
    // 单/批量
    const ids = Array.isArray(selectedUser.value.id) ? selectedUser.value.id : [selectedUser.value.id]
    for (const uid of ids) {
      await axios.put(`/users/${uid}/department`, { departmentId: selectedDepartmentId.value })
    }
    message.success('部门更新成功')
    deptModalVisible.value = false
    fetchUsers(pagination.value.current, pagination.value.pageSize)
  } catch { message.error('部门更新失败') } finally { loading.value = false }
}

// 岗位分配
function openPositionsModal(user) {
  selectedUser.value = user
  selectedPositionIds.value = (user?.positions || []).map(p => p.id)
  positionsModalVisible.value = true
}
function closePositionsModal() { positionsModalVisible.value = false }
async function handleAssignPositions() {
  if (!selectedUser.value) return
  loading.value = true
  try {
    const ids = Array.isArray(selectedUser.value.id) ? selectedUser.value.id : [selectedUser.value.id]
    for (const uid of ids) {
      await axios.put(`/users/${uid}/positions`, { positionIds: selectedPositionIds.value })
    }
    message.success('岗位更新成功')
    positionsModalVisible.value = false
    fetchUsers(pagination.value.current, pagination.value.pageSize)
  } catch { message.error('岗位更新失败') } finally { loading.value = false }
}

// 密码 & 状态
const resetPassword = async (user) => {
  loading.value = true
  try {
    await axios.put(`/users/${user.username}/reset-password`, { newPassword: '123456' })
    message.success('密码已重置为123456')
  } catch (e) { message.error('重置密码失败') } finally { loading.value = false }
}
const toggleStatus = async (user) => {
  loading.value = true
  try {
    const newStatus = user.status === '正常' ? '禁用' : '正常'
    await axios.put(`/users/${user.username}/status`, { status: newStatus })
    message.success('操作成功')
    fetchUsers(pagination.value.current, pagination.value.pageSize)
  } catch (e) { message.error('操作失败') } finally { loading.value = false }
}

// 批量操作
const batchAssignRoles = () => {
  if (!selectedRows.value.length) return message.warning('请先选择用户')
  selectedUser.value = { id: selectedRows.value.map(u => u.id) }
  selectedRoles.value = []
  roleModalVisible.value = true
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
      } catch (e) { message.error('批量操作失败') } finally { loading.value = false }
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
      } catch (e) { message.error('批量重置失败') } finally { loading.value = false }
    }
  })
}

// 导出
const exportCSV = () => {
  const header = ['ID', '用户名', '昵称', '状态', '角色', '部门', '岗位']
  const data = (selectedRows.value.length ? selectedRows.value : users.value).map(u => [
    u.id,
    u.username,
    u.nickname,
    u.status,
    (u.roles||[]).map(r=>r.name).join(';'),
    u.department?.name || '',
    (u.positions||[]).map(p=>p.name).join(';')
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

// 分页
const onPageChange = (p) => { fetchUsers(p.current, p.pageSize) }

onMounted(() => {
  fetchUsers()
  fetchAllRoles()
  fetchAllDepartments()
  fetchAllPositions()
})
</script>
<style scoped>
</style>
