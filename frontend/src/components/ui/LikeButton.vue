<template>
  <a-button :type="liked ? 'primary' : 'default'" @click="handleLike">
    <span v-if="liked">👍 已点赞（点击取消） {{ likeCount }}</span>
    <span v-else>👍 点赞 {{ likeCount }}</span>
  </a-button>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { message } from 'ant-design-vue'
import axios from '@/utils/axios'
import { getUserId } from '@/utils/auth'

const props = defineProps({ postId: Number })
const likeCount = ref(0)
const liked = ref(false)
const loading = ref(false)

const fetchLikeStatus = async () => {
  const userId = getUserId()
  if (!userId) return
  const res = await axios.get(`/api/post/${props.postId}/like/status`, { params: { userId } })
  liked.value = res.liked
}
const fetchLikeCount = async () => {
  const res = await axios.get(`/api/post/${props.postId}/like/count`)
  likeCount.value = res
}
const handleLike = async () => {
  if (loading.value) return
  const userId = getUserId()
  if (!userId) {
    message.warning('请先登录')
    return
  }
  loading.value = true
  try {
    if (!liked.value) {
      await axios.post(`/api/post/${props.postId}/like`, null, { params: { userId } })
      liked.value = true
      likeCount.value += 1
      message.success('点赞成功')
    } else {
      await axios.delete(`/api/post/${props.postId}/like`, { params: { userId } })
      liked.value = false
      likeCount.value = Math.max(0, likeCount.value - 1)
      message.success('已取消点赞')
    }
  } catch (e) {
    message.warning(e.response?.data || '操作失败')
  } finally {
    loading.value = false
  }
}
onMounted(() => {
  fetchLikeStatus()
  fetchLikeCount()
})
watch(() => props.postId, () => {
  fetchLikeStatus()
  fetchLikeCount()
})
</script>
