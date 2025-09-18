<template>
  <section class="community-detail-section" v-if="post">
    <h2 class="community-detail-title">社区互动详情</h2>
    <div class="community-detail-meta">
      <span v-if="post.username">用户名：{{ post.username }}</span>
      <span v-else>用户ID：{{ post.userId }}</span>
      <span v-if="post.type"> | 类型：{{ post.type }}</span>
      <span v-if="post.createTime"> | 发布时间：{{ formatDate(post.createTime) }}</span>
    </div>
    <div class="community-detail-content">{{ post.content }}</div>
    <!-- 点赞区 -->
    <div class="like-section">
      <LikeButton :postId="post.id" />
    </div>
    <!-- 评论区 -->
    <div class="comment-section">
      <h3>评论</h3>
      <form @submit.prevent="submitComment" class="comment-form">
        <textarea v-model="newComment" placeholder="写下你的评论..." required></textarea>
        <button type="submit">发表评论</button>
      </form>
      <div v-if="commentError" class="error">{{ commentError }}</div>
      <div v-if="commentLoading">评论加载中...</div>
      <ul v-else-if="comments.length" class="comment-list">
        <li v-for="c in comments" :key="c.id" class="comment-item">
          <img :src="userMap[c.username]?.avatar || '/favicon.ico'" class="comment-avatar" />
          <span class="comment-user">{{ userMap[c.username]?.nickname || c.username || ('用户' + c.userId) }}：</span>
          <span class="comment-content">{{ c.content }}</span>
          <span class="comment-time">{{ formatDate(c.createTime) }}</span>
          <button v-if="c.username === username || role === 'admin'" @click="deleteComment(c.id)" class="delete-btn">删除</button>
        </li>
      </ul>
      <div v-else>暂无评论</div>
    </div>
  </section>
  <div v-else class="error">{{ error || '未找到帖子' }}</div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import axios from '../utils/axios'
import LikeButton from './ui/LikeButton.vue'

const route = useRoute()
const post = ref(null)
const error = ref('')

// 评论相关
const comments = ref([])
const commentLoading = ref(false)
const commentError = ref('')
const newComment = ref('')
const userMap = ref({})
let role = localStorage.getItem('role') || ''

// 假设已登录用户ID
let userId = localStorage.getItem('userId') || 1
let username = localStorage.getItem('username') || ''

function formatDate(dateStr) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString()
}

const loadPost = async () => {
  error.value = ''
  try {
    const res = await axios.get(`/posts/${route.params.id}`)
    post.value = res
    if (!post.value) error.value = '未找到帖子'
  } catch (e) {
    error.value = '加载失败'
  }
}

const loadComments = async () => {
  commentLoading.value = true
  commentError.value = ''
  try {
    const res = await axios.get('/comments', { params: { postId: route.params.id } })
    comments.value = res || []
    // 批量获取所有评论用户信息
    const usernames = Array.from(new Set(comments.value.map(c => c.username).filter(Boolean)))
    const map = {}
    await Promise.all(usernames.map(async uname => {
      try {
        const ures = await axios.get(`/users/${uname}`)
        map[uname] = ures
      } catch {}
    }))
    userMap.value = map
  } catch (e) {
    commentError.value = '评论加载失败'
  } finally {
    commentLoading.value = false
  }
}

const submitComment = async () => {
  if (!newComment.value) return
  try {
    await axios.post('/comments', { postId: route.params.id, userId, username, content: newComment.value })
    newComment.value = ''
    await loadComments()
  } catch (e) {
    commentError.value = '评论失败'
  }
}

const deleteComment = async (id) => {
  if (!confirm('确定要删除这条评论吗？')) return
  try {
    await axios.delete(`/comments/${id}`)
    await loadComments()
  } catch (e) {
    commentError.value = '删除失败'
  }
}

onMounted(() => {
  loadPost()
  loadComments()
})
</script>

<style scoped>
.community-detail-section {
  margin: 24px auto;
  max-width: 700px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px #e3e3e3;
  padding: 32px 24px;
}
.community-detail-title {
  font-size: 26px;
  font-weight: bold;
  margin-bottom: 16px;
  color: #1565c0;
}
.community-detail-meta {
  color: #888;
  font-size: 14px;
  margin-bottom: 18px;
}
.community-detail-content {
  font-size: 18px;
  line-height: 1.8;
  color: #222;
  white-space: pre-line;
  margin-bottom: 18px;
}
.like-section {
  margin-bottom: 18px;
}
.comment-section {
  margin-top: 24px;
}
.comment-form {
  margin-bottom: 12px;
}
.comment-form textarea {
  width: 60%;
  min-height: 50px;
  margin-right: 8px;
}
.comment-list {
  list-style: none;
  padding: 0;
}
.comment-item {
  margin-bottom: 8px;
  border-bottom: 1px solid #eee;
  padding-bottom: 6px;
  display: flex;
  align-items: flex-start;
}
.comment-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  margin-right: 10px;
}
.comment-user {
  color: #1565c0;
  margin-right: 6px;
}
.comment-content {
  color: #222;
}
.comment-time {
  color: #888;
  margin-left: 8px;
  font-size: 12px;
}
.delete-btn {
  margin-left: 10px;
  color: #d00;
  background: none;
  border: none;
  cursor: pointer;
}
.error {
  color: #d00;
  margin: 12px 0;
}
</style>
