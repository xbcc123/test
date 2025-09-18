import axios from 'axios'
import { message } from 'ant-design-vue'

const instance = axios.create({
  baseURL: 'http://localhost:9697', // 可根据实际API地址调整
  timeout: 10000
})

instance.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers['Authorization'] = 'Bearer ' + token
  }
  return config
})

instance.interceptors.response.use(
  response => {
    const res = response.data
    if (res && typeof res.code !== 'undefined') {
      if (res.code === 0) {
        return res.data
      } else {
        message.error(res.msg || '请求失败')
        return Promise.reject(res)
      }
    }
    // 兼容非统一返回结构
    return res
  },
  error => {
    if (error.response) {
      // 处理 401 未授权
      if (error.response.status === 401) {
        // 清除本地token
        localStorage.removeItem('token')
        // 可根据实际路由跳转到登录页
        // window.location.href = '/login'
        window.location.hash = '#/login'
        // 或者使用弹窗提示
        // alert('登录已过期，请重新登录')
      } else if (
        error.response.data &&
        error.response.data.error &&
        error.response.data.error.includes('Token已过期')
      ) {
        localStorage.removeItem('token')
        // window.location.href = '/login'
        window.location.hash = '#/login'
      }
    }
    return Promise.reject(error)
  }
)

// 文章点赞相关API
export function likeArticle(articleId) {
  return instance.post(`/api/article/${articleId}/like`)
}

export function unlikeArticle(articleId) {
  return instance.delete(`/api/article/${articleId}/like`)
}

export function getArticleLikeCount(articleId) {
  return instance.get(`/api/article/${articleId}/like/count`)
}

export function getArticleLikeStatus(articleId) {
  return instance.get(`/api/article/${articleId}/like/status`)
}

// 服务单预约相关API
export function createServiceOrder(data) {
  return instance.post('/api/service-orders', data)
}
export function getMyServiceOrders() {
  return instance.get('/api/service-orders')
}
export function getServiceOrder(id) {
  return instance.get(`/api/service-orders/${id}`)
}
export function cancelServiceOrder(id) {
  return instance.put(`/api/service-orders/${id}/cancel`)
}
export function completeServiceOrder(id) {
  return instance.put(`/api/service-orders/${id}/complete`)
}

// 猫咪商城相关API
export function getCatShopOnSale() {
  return instance.get('/api/catshop/on-sale')
}
export function getCatShopAll() {
  return instance.get('/api/catshop')
}
export function getCatShopById(id) {
  return instance.get(`/api/catshop/${id}`)
}
export function addCatShop(data) {
  return instance.post('/api/catshop', data)
}
export function updateCatShop(id, data) {
  return instance.put(`/api/catshop/${id}`, data)
}
export function deleteCatShop(id) {
  return instance.delete(`/api/catshop/${id}`)
}
export function setCatShopStatus(id, status) {
  return instance.put(`/api/catshop/${id}/status?status=${status}`)
}

export default instance
