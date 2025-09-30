<template>
  <div class="flex h-[calc(100vh-20px)] p-4 gap-4">
    <!-- 会话列表 -->
    <div class="w-72 border rounded flex flex-col">
      <div class="p-2 font-semibold border-b flex justify-between items-center">
        <span>会话</span>
        <a-badge :count="totalUnread" />
      </div>
      <div class="flex-1 overflow-y-auto">
        <div v-for="c in conversations" :key="c.targetUserId" @click="selectConversation(c)"
             class="px-3 py-2 cursor-pointer hover:bg-gray-100 flex gap-3 items-center"
             :class="c.targetUserId===activeTarget?'bg-gray-200':''">
          <img v-if="c.targetUser?.avatar" :src="c.targetUser.avatar" class="w-10 h-10 rounded-full object-cover" />
          <div v-else class="w-10 h-10 rounded-full bg-blue-500 text-white flex items-center justify-center text-sm font-semibold">
            {{ (c.targetUser?.nickname||c.targetUser?.username||('U'+c.targetUserId)).slice(0,2) }}
          </div>
          <div class="truncate flex-1">
            <div class="text-sm font-medium">
              {{ c.targetUser?.nickname || c.targetUser?.username || ('用户 '+c.targetUserId) }}
            </div>
            <div class="text-xs text-gray-500 truncate">{{ c.lastContent }}</div>
          </div>
          <a-badge v-if="c.unreadCount>0" :count="c.unreadCount" />
        </div>
        <div v-if="conversations.length===0" class="text-center text-gray-400 py-6 text-sm">暂无会话</div>
      </div>
      <div class="p-2 border-t text-xs text-gray-500">在线聊天 Demo</div>
    </div>

    <!-- 聊天窗口 -->
    <div class="flex-1 border rounded flex flex-col">
      <div class="p-2 border-b font-semibold flex justify-between items-center">
        <span v-if="activeTarget">与 {{ activeTargetUserName }} 对话</span>
        <span v-else class="text-gray-400">选择一个会话或输入用户ID</span>
        <div class="flex items-center gap-2 text-xs text-gray-500">
          <span :class="{'text-green-600': wsConnected, 'text-red-500': !wsConnected}">
            {{ wsConnected? '已连接':'未连接' }}
          </span>
          <a-button size="small" @click="initWs" :disabled="wsConnected">重连</a-button>
        </div>
      </div>

      <div class="flex-1 overflow-y-auto p-3 space-y-3" ref="msgScroll">
        <div v-if="messages.length===0" class="text-center text-gray-400 text-sm py-8">暂无消息</div>
        <div v-for="m in orderedMessages" :key="m.id" class="flex items-end" :class="m.fromUserId===myId?'flex-row-reverse':''">
          <img v-if="(m.fromUserId!==myId && m.fromUser?.avatar) || (m.fromUserId===myId && m.fromUser?.avatar)"
               :src="(m.fromUserId===myId? m.fromUser?.avatar : m.fromUser?.avatar)"
               class="w-8 h-8 rounded-full object-cover mx-2" />
          <div v-else class="w-8 h-8 rounded-full bg-blue-400 text-white flex items-center justify-center text-xs mx-2">
            {{ (m.fromUser?.nickname||m.fromUser?.username||('U'+m.fromUserId)).slice(0,1) }}
          </div>
          <div class="max-w-[60%] rounded px-3 py-2 text-sm whitespace-pre-wrap relative"
               :class="m.fromUserId===myId?'bg-blue-500 text-white':'bg-gray-200'">
            <div class="text-[10px] opacity-70 mb-0.5 flex justify-between gap-2">
              <span class="truncate max-w-[130px]">{{ m.fromUser?.nickname || m.fromUser?.username || ('用户 '+m.fromUserId) }}</span>
              <span>{{ formatTime(m.createTime) }}</span>
            </div>
            <div>{{ m.content }}</div>
          </div>
        </div>
      </div>

      <!-- 发送区 -->
      <div class="border-t p-2 flex flex-col gap-2">
        <div class="flex gap-2 items-center flex-wrap">
          <a-input v-model:value="inputTargetId" placeholder="对方用户ID (数字)" style="width:160px" />
          <a-button size="small" @click="openConversation">打开会话</a-button>
          <a-button size="small" type="default" @click="refreshConversations">刷新会话</a-button>
        </div>
        <a-textarea v-model:value="inputContent" :rows="3" placeholder="输入消息，Ctrl+Enter 发送" @keydown="onTextareaKey" />
        <div class="flex justify-between items-center flex-wrap gap-2">
          <div class="text-xs text-gray-400">WebSocket URL: {{ wsUrl }}</div>
          <div class="flex gap-2">
            <a-button type="primary" @click="sendMessage" :disabled="!canSend">发送</a-button>
            <a-button @click="clearMessages" size="small">清屏</a-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount, nextTick, watch } from 'vue'
import { getToken, getUserId } from '../utils/auth'
import { message as antdMsg } from 'ant-design-vue'
import {
  apiChatConversations,
  apiChatMessages,
  apiChatUnreadTotal
} from '../utils/axios'
import { useRoute, useRouter } from 'vue-router'

const conversations = ref([])
const messages = ref([]) // 当前会话消息(倒序原始，展示时反转)
const activeTarget = ref(null)
const inputTargetId = ref('')
const inputContent = ref('')
const ws = ref(null)
const wsConnected = ref(false)
const reconnectTimer = ref(null)
const myId = Number(getUserId() || 0)
const totalUnread = ref(0)
const msgScroll = ref(null)

const route = useRoute()
const router = useRouter()

const wsUrl = `ws://localhost:9697/ws/chat?token=${encodeURIComponent(getToken())}`

const orderedMessages = computed(()=> {
  return [...messages.value].sort((a,b)=> new Date(a.createTime) - new Date(b.createTime))
})

const canSend = computed(()=> !!activeTarget.value && inputContent.value.trim().length>0 && wsConnected.value)
const activeTargetUserName = computed(()=>{
  if(!activeTarget.value) return ''
  const c = conversations.value.find(x=>x.targetUserId===activeTarget.value)
  return c?.targetUser?.nickname || c?.targetUser?.username || ('用户 '+activeTarget.value)
})

function formatTime(t){
  if(!t) return ''
  return t.replace('T',' ')
}

function scrollToBottom(){
  nextTick(()=>{
    const el = msgScroll.value
    if(el) el.scrollTop = el.scrollHeight
  })
}

async function refreshConversations(){
  try {
    conversations.value = await apiChatConversations()
    totalUnread.value = await apiChatUnreadTotal()
  } catch(e) {}
}

async function loadMessages(targetUserId){
  try {
    messages.value = await apiChatMessages(targetUserId, 0, 50, true) // markRead
    // 刷新列表未读
    await refreshConversations()
    scrollToBottom()
  } catch(e) {}
}

function selectConversation(c){
  activeTarget.value = c.targetUserId
  inputTargetId.value = String(c.targetUserId)
  loadMessages(c.targetUserId)
}

function openConversationById(id){
  if(!id || id<=0){ return }
  activeTarget.value = id
  inputTargetId.value = String(id)
  loadMessages(id)
}

function openConversation(){
  const id = Number(inputTargetId.value)
  if(!id || id<=0){
    antdMsg.warning('请输入有效用户ID')
    return
  }
  openConversationById(id)
}

function sendMessage(){
  if(!canSend.value) return
  const payload = { type: 'chat', toUserId: activeTarget.value, content: inputContent.value.trim() }
  ws.value?.send(JSON.stringify(payload))
  inputContent.value = ''
}

function onTextareaKey(e){
  if((e.metaKey || e.ctrlKey) && e.key==='Enter'){
    sendMessage()
  }
}

function clearMessages(){
  messages.value = []
}

function initWs(){
  if(ws.value){
    try { ws.value.close() } catch(_){ }
  }
  wsConnected.value = false
  ws.value = new WebSocket(wsUrl)
  ws.value.onopen = ()=>{ wsConnected.value = true; }
  ws.value.onclose = ()=>{ wsConnected.value = false; scheduleReconnect() }
  ws.value.onerror = ()=>{ wsConnected.value = false }
  ws.value.onmessage = (evt)=>{
    try {
      const msg = JSON.parse(evt.data)
      if(msg.type==='chat' && msg.data){
        const d = msg.data
        if(activeTarget.value && (d.fromUserId===activeTarget.value || d.toUserId===activeTarget.value)){
          messages.value.push(d)
          scrollToBottom()
        }
        refreshConversations()
      } else if(msg.type==='error' && msg.error==='BLOCKED') {
        antdMsg.error('发送失败：对方或您已拉黑')
      }
    } catch(e){}
  }
}

function scheduleReconnect(){
  if(reconnectTimer.value){ clearTimeout(reconnectTimer.value) }
  reconnectTimer.value = setTimeout(()=>{
    if(!wsConnected.value){ initWs() }
  }, 5000)
}

onMounted(async ()=>{
  await refreshConversations()
  initWs()
  const qid = Number(route.query.uid)
  if(qid>0){
    // 如果已经有该联系人会话，则直接打开，否则尝试加载并打开
    openConversationById(qid)
  }
})

watch(()=>route.query.uid,(nv)=>{
  const qid = Number(nv)
  if(qid>0){
    openConversationById(qid)
  }
})

onBeforeUnmount(()=>{
  if(ws.value){ ws.value.close() }
  if(reconnectTimer.value) clearTimeout(reconnectTimer.value)
})
</script>

<style scoped>
::-webkit-scrollbar { width:6px; }
::-webkit-scrollbar-thumb { background:#c1c1c1; border-radius:3px; }
</style>
