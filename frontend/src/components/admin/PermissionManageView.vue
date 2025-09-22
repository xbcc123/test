<template>
  <div>
    <a-card title="权限管理">
      <a-button type="primary" @click="openAddModal" style="margin-bottom:12px;">新增权限</a-button>
      <a-input-search
        v-model:value="searchKeyword"
        placeholder="搜索权限名"
        @search="fetchPermissions"
        style="margin-bottom: 12px;"
      />
      <div style="display: flex; gap: 24px;">
        <a-tree
          :tree-data="permissionsTree"
          :field-names="{ title: 'name', key: 'id', children: 'children' }"
          @select="onTreeSelect"
          style="width: 300px; min-width: 200px;"
        />
        <a-table
          :columns="columns"
          :data-source="permissionsTree"
          row-key="id"
          :loading="loading"
          :childrenColumnName="'children'"
          style="flex:1;">
          <template #action="{ record }">
            <a @click="editPermission(record)">编辑</a>
            <a-divider type="vertical" />
            <a @click="deletePermission(record)">删除</a>
          </template>
        </a-table>
      </div>
      <a-modal v-model:visible="editModalVisible" :title="editPermissionForm.id ? '编辑权限' : '新增权限'" @ok="handleEditPermission">
        <a-form :model="editPermissionForm">
          <a-form-item label="权限名"><a-input v-model:value="editPermissionForm.name" /></a-form-item>
          <a-form-item label="描述"><a-input v-model:value="editPermissionForm.description" /></a-form-item>
          <a-form-item label="父权限">
            <a-tree-select
              v-model:value="editPermissionForm.parentId"
              :tree-data="permissionsTree"
              :field-names="{ label: 'name', value: 'id', children: 'children' }"
              allow-clear
              placeholder="请选择父权限（可为空）"
              style="width: 100%"/>
          </a-form-item>
        </a-form>
      </a-modal>
    </a-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from '@/utils/axios'
import { message } from 'ant-design-vue'

const permissionsTree = ref([]) // 树状结构
const loading = ref(false)
const searchKeyword = ref('')
const columns = [
  { title: 'ID', dataIndex: 'id' },
  { title: '权限名', dataIndex: 'name' },
  { title: '描述', dataIndex: 'description' },
  { title: '操作', key: 'action', slots: { customRender: 'action' } }
]
const editModalVisible = ref(false)
const editPermissionForm = ref({ id: null, name: '', description: '', parentId: null })

// 工具函数：树转扁平
function treeToList(tree) {
  let res = []
  tree.forEach(node => {
    res.push(node)
    if (node.children) res = res.concat(treeToList(node.children))
  })
  return res
}

const fetchPermissions = async () => {
  loading.value = true
  try {
    const res = await axios.get('/permissions', { params: { keyword: searchKeyword.value } })
    // 直接使用后端返回的树状结构
    permissionsTree.value = res
  } catch (e) {
    message.error('获取权限列表失败')
  } finally {
    loading.value = false
  }
}
const openAddModal = () => {
  editPermissionForm.value = { id: null, name: '', description: '', parentId: null }
  editModalVisible.value = true
}
const onTreeSelect = (selectedKeys, { selectedNodes }) => {
  // 选中树节点时，支持直接在该节点下添加子权限
  if (selectedKeys.length > 0) {
    editPermissionForm.value.parentId = selectedKeys[0]
  } else {
    editPermissionForm.value.parentId = null
  }
}
const editPermission = (perm) => {
  // 显式设置 parentId 字段，确保为 null 或具体 id
  editPermissionForm.value = {
    id: perm.id,
    name: perm.name,
    description: perm.description,
    parentId: perm.parentId === undefined ? null : perm.parentId
  }
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
