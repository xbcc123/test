import axios from 'axios'
import { message } from 'ant-design-vue'

const instance = axios.create({
  baseURL: 'http://localhost:9697', // 可根据实际API地址调整
  timeout: 10000
})

let isRefreshing = false;
let refreshSubscribers = [];

function onRefreshed(token) {
  refreshSubscribers.forEach(cb => cb(token));
  refreshSubscribers = [];
}

function addRefreshSubscriber(cb) {
  refreshSubscribers.push(cb);
}

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
  async error => {
    const originalRequest = error.config;
    if (error.response) {
      // 401 未授权
      if (error.response.status === 401) {
        // 检查是否 token 过期且有 refreshToken
        const refreshToken = localStorage.getItem('refresh_token');
        if (refreshToken && !originalRequest._retry) {
          if (!isRefreshing) {
            isRefreshing = true;
            // 假设有刷新token的API
            try {
              const resp = await axios.post('http://localhost:9697/api/auth/refresh', { refreshToken });
              const { token: newToken, refreshToken: newRefreshToken } = resp.data.data || {};
              localStorage.setItem('token', newToken);
              localStorage.setItem('refresh_token', newRefreshToken);
              onRefreshed(newToken);
              isRefreshing = false;
              originalRequest._retry = true;
              originalRequest.headers['Authorization'] = 'Bearer ' + newToken;
              return instance(originalRequest);
            } catch (e) {
              isRefreshing = false;
              localStorage.removeItem('token');
              localStorage.removeItem('refresh_token');
              window.location.hash = '#/login';
              return Promise.reject(e);
            }
          } else {
            // 队列等待刷新完成
            return new Promise((resolve, reject) => {
              addRefreshSubscriber(token => {
                originalRequest._retry = true;
                originalRequest.headers['Authorization'] = 'Bearer ' + token;
                resolve(instance(originalRequest));
              });
            });
          }
        } else {
          localStorage.removeItem('token');
          localStorage.removeItem('refresh_token');
          window.location.hash = '#/login';
        }
      } else {
        // 其它错误
        message.error(error.response.data?.msg || '请求异常');
      }
    } else {
      message.error('网络异常');
    }
    return Promise.reject(error);
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

// 即时通讯 IM 相关 API
export function apiChatConversations() {
  return instance.get('/api/chat/conversations')
}
export function apiChatMessages(targetUserId, page=0, size=50, markRead=true) {
  return instance.get(`/api/chat/messages`, { params: { targetUserId, page, size, markRead } })
}
export function apiChatSendHttp(toUserId, content) {
  return instance.post('/api/chat/send', { toUserId, content })
}
export function apiChatMarkRead(targetUserId) {
  return instance.post('/api/chat/read', { targetUserId })
}
export function apiChatUnreadTotal() {
  return instance.get('/api/chat/unread/total')
}

// 即时通讯 IM 相关 API
export function apiChatConversations() {
  return instance.get('/api/chat/conversations')
}
export function apiChatMessages(targetUserId, page=0, size=50, markRead=true) {
  return instance.get(`/api/chat/messages`, { params: { targetUserId, page, size, markRead } })
}
export function apiChatSendHttp(toUserId, content) {
  return instance.post('/api/chat/send', { toUserId, content })
}
export function apiChatMarkRead(targetUserId) {
  return instance.post('/api/chat/read', { targetUserId })
}
export function apiChatUnreadTotal() {
  return instance.get('/api/chat/unread/total')
}

// 通讯录相关 API
export function apiContactsList(){
  return instance.get('/api/contacts')
}
export function apiContactsAdd(contactUserId, remark){
  return instance.post('/api/contacts',{ contactUserId, remark })
}
export function apiContactsUpdateRemark(contactUserId, remark){
  return instance.put(`/api/contacts/${contactUserId}/remark`, { remark })
}
export function apiContactsSetBlocked(contactUserId, blocked){
  return instance.put(`/api/contacts/${contactUserId}/block`, null, { params:{ blocked } })
}
export function apiContactsDelete(contactUserId){
  return instance.delete(`/api/contacts/${contactUserId}`)
}

export default instance
