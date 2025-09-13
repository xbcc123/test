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

export default instance

