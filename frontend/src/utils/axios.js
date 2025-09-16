import axios from 'axios'

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
    // 正常响应直接返回
    return response
  },
  error => {
    if (error.response) {
      // 处理 401 未授权
      if (error.response.status === 401) {
        // 清除本地token
        localStorage.removeItem('token')
        // 可根据实际路由跳转到登录页
        window.location.href = '/login'
        // 或者使用弹窗提示
        // alert('登录已过期，请重新登录')
      } else if (
        error.response.data &&
        error.response.data.error &&
        error.response.data.error.includes('Token已过期')
      ) {
        localStorage.removeItem('token')
        window.location.href = '/login'
      }
    }
    return Promise.reject(error)
  }
)

export default instance
