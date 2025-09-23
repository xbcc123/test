<template>
  <div>
    <a-card title="部门管理">
      <div style="margin-bottom:12px;display:flex;gap:8px;align-items:center;flex-wrap:wrap;">
        <a-button type="primary" @click="openEditModal(null)">新增根部门</a-button>
        <a-input-search v-model:value="keyword" @search="noop" placeholder="搜索名称/编码" style="width:260px" />
        <a-button @click="expandAll" size="small">全部展开</a-button>
        <a-button @click="collapseAll" size="small">全部折叠</a-button>
      </div>
      <a-table
        :data-source="treeDisplayData"
        :columns="columns"
        rowKey="id"
        size="middle"
        :loading="loading"
        :pagination="false"
        :defaultExpandAllRows="autoExpanded"
        :expandedRowKeys="expandedKeys"
        @expand="onExpand"
      >
        <template #name="{ record }">
          <span :style="highlightStyle(record)">{{ record.name }}</span>
        </template>
        <template #action="{ record }">
          <a @click="openEditModal(record)">编辑</a>
          <a-divider type="vertical" />
          <a @click="addChild(record)">新增子部门</a>
          <a-divider type="vertical" />
          <a-popconfirm title="确定删除该部门?" @confirm="remove(record)">
            <a>删除</a>
          </a-popconfirm>
        </template>
      </a-table>

      <a-modal v-model:visible="editVisible" :title="form.id ? '编辑部门' : '新增部门'" @ok="submit" @cancel="resetForm">
        <a-form :model="form" layout="vertical">
          <a-form-item label="名称" required>
            <a-input v-model:value="form.name" />
          </a-form-item>
          <a-form-item label="编码">
            <a-input v-model:value="form.code" />
          </a-form-item>
          <a-form-item label="上级部门">
            <a-tree-select
              style="width:100%" allow-clear show-search tree-default-expand-all
              v-model:value="form.parentId"
              :tree-data="parentSelectTree"
              :field-names="{label:'name', value:'id', children:'children'}"
              :dropdown-style="{maxHeight:'360px',overflow:'auto'}"
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

const departments = ref([])
const loading = ref(false)
const keyword = ref('')
const editVisible = ref(false)
const form = ref({ id:null, name:'', code:'', description:'', parentId:null })

// 表格列（树状显示只需去掉“上级”列）
const columns = [
  { title:'名称', dataIndex:'name', key:'name', slots:{ customRender:'name' } },
  { title:'编码', dataIndex:'code', width:150 },
  { title:'描述', dataIndex:'description' },
  { title:'操作', key:'action', width:200, slots:{ customRender:'action' } }
]

// 构建树
function buildTree(list){
  const map = {}
  list.forEach(d=> map[d.id]={ ...d, children:[] })
  const roots=[]
  list.forEach(d=> { if(d.parentId && map[d.parentId]) map[d.parentId].children.push(map[d.id]); else roots.push(map[d.id]) })
  // 排序（可选：按 id 或 name）
  const sortFn = (a,b)=> (a.name||'').localeCompare(b.name||'')
  function sortRec(nodes){ nodes.sort(sortFn); nodes.forEach(n=> sortRec(n.children)) }
  sortRec(roots)
  return roots
}

const treeData = computed(()=> {
  if(!departments.value.length) return []
  const hasChildren = departments.value.some(d => Array.isArray(d.children) && d.children.length)
  return hasChildren ? departments.value : buildTree(departments.value)
})

// 关键词过滤保留匹配节点及其祖先路径
function filterTree(nodes, kw){
  if(!kw) return nodes
  const res=[]
  for(const n of nodes){
    const childFiltered = filterTree(n.children||[], kw)
    const selfMatch = (n.name||'').includes(kw) || (n.code||'').includes(kw)
    if(selfMatch || childFiltered.length){
      res.push({ ...n, children: childFiltered })
    }
  }
  return res
}
const treeDisplayData = computed(()=> filterTree(treeData.value, keyword.value.trim()))

// 展开/折叠控制
const expandedKeys = ref([])
const autoExpanded = true
function collectAllKeys(nodes, arr){ nodes.forEach(n=> { arr.push(n.id); if(n.children?.length) collectAllKeys(n.children, arr) }) }
function expandAll(){
  const all=[]; collectAllKeys(treeDisplayData.value, all); expandedKeys.value = all
}
function collapseAll(){ expandedKeys.value = [] }
function onExpand(expanded, record){
  const id = record.id
  if(expanded) { if(!expandedKeys.value.includes(id)) expandedKeys.value.push(id) }
  else { expandedKeys.value = expandedKeys.value.filter(k=> k!==id) }
}

function highlightStyle(record){
  if(!keyword.value) return {}
  const kw = keyword.value.trim()
  if(!kw) return {}
  if((record.name||'').includes(kw) || (record.code||'').includes(kw)){
    return { background:'#fff9e6' }
  }
  return {}
}

// CRUD
const fetchDepartments = async () => {
  loading.value = true
  try {
    departments.value = await axios.get('/departments') || []
    // 默认展开根节点
    expandAll()
  } catch(e) { message.error('获取部门失败') } finally { loading.value = false }
}

function openEditModal(record){
  if(record){ form.value = { id:record.id, name:record.name, code:record.code, description:record.description, parentId:record.parentId || null } }
  else { form.value = { id:null, name:'', code:'', description:'', parentId:null } }
  editVisible.value = true
}
function addChild(record){
  form.value = { id:null, name:'', code:'', description:'', parentId: record.id }
  editVisible.value = true
}
function resetForm(){ form.value = { id:null, name:'', code:'', description:'', parentId:null } }
async function submit(){
  if(!form.value.name) return message.warning('请输入名称')
  try {
    if(form.value.id){ await axios.put(`/departments/${form.value.id}`, form.value); message.success('更新成功') }
    else { await axios.post('/departments', form.value); message.success('创建成功') }
    editVisible.value=false
    await fetchDepartments()
  } catch(e){ message.error('保存失败') }
}
async function remove(record){
  try { await axios.delete(`/departments/${record.id}`); message.success('删除成功'); fetchDepartments() } catch(e){ message.error('删除失败') }
}

// Add filtered parent tree excluding current node
const parentSelectTree = computed(() => {
  if (!treeData.value.length) return []
  const currentId = form.value.id
  if (!currentId) return treeData.value // creating new root, show all
  const deepFilter = (nodes) => {
    const result = []
    for (const n of nodes) {
      if (n.id === currentId) continue // skip self (and implicitly its children)
      const children = n.children && n.children.length ? deepFilter(n.children) : []
      result.push({ ...n, children })
    }
    return result
  }
  return deepFilter(treeData.value)
})

function noop(){ /* 搜索触发由 computed 处理 */ }

onMounted(()=>{ fetchDepartments() })
</script>
<style scoped>
</style>
