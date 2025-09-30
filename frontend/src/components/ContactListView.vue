<template>
  <div class="p-4 space-y-4">
    <div class="flex flex-wrap gap-4 items-end border-b pb-4">
      <a-input-number v-model:value="addUserId" :min="1" placeholder="用户ID" />
      <a-input v-model:value="addRemark" placeholder="备注(可选)" style="width:220px" />
      <a-button type="primary" @click="addContact" :loading="adding">添加联系人</a-button>
      <a-button @click="loadContacts" :loading="loading">刷新</a-button>
      <div class="text-xs text-gray-500">提示：输入对方用户ID添加，示例数据可直接用 2,3...</div>
    </div>

    <a-table :data-source="contacts" :loading="loading" row-key="contactUserId" size="small" :pagination="false">
      <a-table-column title="头像" key="avatar" width="70"
        :customRender="({ record }) => h('div',{class:'flex justify-center'},[
            record.user?.avatar ? h('img',{src:record.user.avatar,class:'w-10 h-10 rounded-full object-cover'}) :
              h('div',{class:'w-10 h-10 rounded-full bg-blue-400 text-white flex items-center justify-center text-sm'},(record.user?.nickname||record.user?.username||('U'+record.contactUserId)).slice(0,2))
        ])" />
      <a-table-column title="用户ID" data-index="contactUserId" width="90" />
      <a-table-column title="昵称" key="nickname" :customRender="({record})=> record.user?.nickname || record.user?.username || ('用户'+record.contactUserId)" />
      <a-table-column title="备注" key="remark" :customRender="renderRemark" />
      <a-table-column title="拉黑" key="blocked" width="90"
        :customRender="({record}) => h('a-switch', {checked: record.blocked, 'onUpdate:checked': v=> toggleBlock(record,v)})" />
      <a-table-column title="操作" key="ops" :customRender="renderOps" width="220" />
    </a-table>

    <div v-if="contacts.length===0 && !loading" class="text-center text-gray-400 py-10">暂无联系人</div>

    <a-modal v-model:open="remarkModal.open" title="修改备注" @ok="saveRemark" :confirm-loading="savingRemark">
      <a-input v-model:value="remarkModal.remark" placeholder="备注名" />
    </a-modal>
  </div>
</template>
<script setup>
import { ref, h, onMounted } from 'vue'
import { message, Modal } from 'ant-design-vue'
import {
  apiContactsList,
  apiContactsAdd,
  apiContactsUpdateRemark,
  apiContactsSetBlocked,
  apiContactsDelete
} from '../utils/axios'
import { useRouter } from 'vue-router'

const contacts = ref([])
const loading = ref(false)
const adding = ref(false)
const addUserId = ref(null)
const addRemark = ref('')
const remarkModal = ref({ open:false, id:null, remark:'', old:'' })
const savingRemark = ref(false)
const router = useRouter()

async function loadContacts(){
  loading.value = true
  try { contacts.value = await apiContactsList() || [] } catch(e) {} finally { loading.value=false }
}

async function addContact(){
  if(!addUserId.value || addUserId.value<=0){ message.warning('请输入有效用户ID'); return }
  adding.value = true
  try {
    await apiContactsAdd(addUserId.value, addRemark.value||undefined)
    message.success('添加成功')
    addUserId.value = null; addRemark.value=''
    loadContacts()
  } catch(e) {} finally { adding.value=false }
}

function renderOps({record}){
  return h('div',{class:'flex gap-2 flex-wrap'},[
    h('a-button',{size:'small',type:'primary',onClick:()=>openChat(record.contactUserId)}, {default:()=> '发消息'}),
    h('a-button',{size:'small',onClick:()=>openRemark(record)}, {default:()=> '备注'}),
    h('a-button',{size:'small',danger:true,onClick:()=>removeContact(record)}, {default:()=> '删除'})
  ])
}

function openChat(uid){
  router.push({ path:'/chat', query:{ uid } })
}

function openRemark(r){
  remarkModal.value = {open:true,id:r.contactUserId,remark:r.remark||'',old:r.remark||''}
}

async function saveRemark(){
  savingRemark.value = true
  try {
    await apiContactsUpdateRemark(remarkModal.value.id, remarkModal.value.remark)
    message.success('已更新')
    remarkModal.value.open = false
    loadContacts()
  } catch(e) {} finally { savingRemark.value=false }
}

async function toggleBlock(record, val){
  const prev = record.blocked
  record.blocked = val
  try {
    await apiContactsSetBlocked(record.contactUserId, val)
    message.success(val? '已拉黑':'已取消拉黑')
  } catch(e) { record.blocked = prev }
}

async function removeContact(record){
  Modal.confirm({
    title:'确认删除?',
    content: '将移除此联系人关系',
    okText:'删除', okType:'danger', cancelText:'取消',
    async onOk(){
      try { await apiContactsDelete(record.contactUserId); message.success('已删除'); loadContacts() } catch(e){}
    }
  })
}

function renderRemark({record}){
  return record.remark || '-'
}

onMounted(loadContacts)
</script>
<style scoped>
</style>

