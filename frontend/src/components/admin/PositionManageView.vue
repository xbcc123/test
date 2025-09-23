<template>
  <div>
    <a-card title="岗位管理">
      <div style="margin-bottom:12px;display:flex;gap:8px;flex-wrap:wrap;align-items:center;">
        <a-button type="primary" @click="openEditModal(null)">新增岗位</a-button>
        <!-- 部门树筛选 -->
        <a-tree-select
          v-model:value="filterDept"
          allow-clear
            show-search
          style="width:240px"
          placeholder="按部门筛选"
          :tree-data="departmentTree"
          :field-names="{label:'name', value:'id', children:'children'}"
          @change="fetchPositions"
          tree-default-expand-all
        />
        <a-input-search v-model:value="keyword" @search="() => {}" placeholder="搜索名称/编码" style="width:240px" />
      </div>
      <a-table :data-source="displayPositions" :columns="columns" rowKey="id" size="middle" :loading="loading" :pagination="false">
        <template #department="{ record }">
          <span>{{ deptName(record.departmentId) }}</span>
        </template>
        <template #action="{ record }">
          <a @click="openEditModal(record)">编辑</a>
          <a-divider type="vertical" />
          <a-popconfirm title="确定删除该岗位?" @confirm="remove(record)"><a>删除</a></a-popconfirm>
        </template>
      </a-table>
      <a-modal v-model:visible="editVisible" :title="form.id ? '编辑岗位' : '新增岗位'" @ok="submit" @cancel="resetForm">
        <a-form :model="form" layout="vertical">
          <a-form-item label="名称" required>
            <a-input v-model:value="form.name" />
          </a-form-item>
          <a-form-item label="编码">
            <a-input v-model:value="form.code" />
          </a-form-item>
          <a-form-item label="部门">
            <a-tree-select
              v-model:value="form.departmentId"
              allow-clear
              show-search
              style="width:100%"
              placeholder="选择部门"
              :tree-data="departmentTree"
              :field-names="{label:'name', value:'id', children:'children'}"
              tree-default-expand-all
            />
          </a-form-item>
          <a-form-item label="描述">
            <a-textarea v-model:value="form.description" rows="3" />
          </a-form-item>
        </a-form>
      </a-modal>
    </a-card>
  </div>
</template>
<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from '@/utils/axios'
import { message } from 'ant-design-vue'

const positions = ref([])
const departments = ref([]) // 可能已为树或平铺
const loading = ref(false)
const editVisible = ref(false)
const filterDept = ref()
const keyword = ref('')
const form = ref({ id:null, name:'', code:'', description:'', departmentId:null })

const columns = [
  { title:'ID', dataIndex:'id', width:80 },
  { title:'名称', dataIndex:'name' },
  { title:'编码', dataIndex:'code', width:140 },
  { title:'部门', dataIndex:'departmentId', width:160, slots:{ customRender:'department' } },
  { title:'描述', dataIndex:'description' },
  { title:'操作', key:'action', width:160, slots:{ customRender:'action' } }
]

async function fetchDepartments(){
  try { departments.value = await axios.get('/departments') || [] } catch(e){ message.error('获取部门失败') }
}
async function fetchPositions(){
  loading.value=true
  try { positions.value = await axios.get('/positions', { params:{ departmentId: filterDept.value } }) || [] } catch(e){ message.error('获取岗位失败') } finally { loading.value=false }
}

// 构建树（兼容后端已返回树状 & 平铺）
function buildDeptTree(list){
  if(!list || !list.length) return []
  // 如果任何节点已经有 children 字段（并且非空或空数组），认为是树
  const looksTree = list.some(d => Object.prototype.hasOwnProperty.call(d,'children'))
  if(looksTree) return list
  const map = {}
  list.forEach(d=> map[d.id] = { ...d, children:[] })
  const roots = []
  list.forEach(d=> { if(d.parentId && map[d.parentId]) map[d.parentId].children.push(map[d.id]); else roots.push(map[d.id]) })
  return roots
}
const departmentTree = computed(()=> buildDeptTree(departments.value))

const displayPositions = computed(()=> {
  if(!keyword.value) return positions.value
  return positions.value.filter(p => (p.name||'').includes(keyword.value) || (p.code||'').includes(keyword.value))
})

// 递归寻找部门名称
function findDeptName(nodes, id){
  if(!id) return '-'
  for(const n of nodes){
    if(n.id === id) return n.name || '-'
    if(n.children && n.children.length){
      const r = findDeptName(n.children, id)
      if(r) return r
    }
  }
  return '-'
}
function deptName(id){ return findDeptName(departmentTree.value, id) }

function openEditModal(record){
  if(record){ form.value = { id:record.id, name:record.name, code:record.code, description:record.description, departmentId:record.departmentId||null } }
  else { form.value = { id:null, name:'', code:'', description:'', departmentId: filterDept.value||null } }
  editVisible.value = true
}
function resetForm(){ form.value={ id:null, name:'', code:'', description:'', departmentId:null } }
async function submit(){
  if(!form.value.name) return message.warning('请输入名称')
  try {
    if(form.value.id){ await axios.put(`/positions/${form.value.id}`, form.value); message.success('更新成功') }
    else { await axios.post('/positions', form.value); message.success('创建成功') }
    editVisible.value=false; fetchPositions()
  } catch(e){ message.error('保存失败') }
}
async function remove(record){ try{ await axios.delete(`/positions/${record.id}`); message.success('删除成功'); fetchPositions() }catch(e){ message.error('删除失败') } }

onMounted(()=>{ fetchDepartments(); fetchPositions() })
</script>
<style scoped>
</style>
