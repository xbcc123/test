// 公共方法：拼接后端baseURL和相对路径，适用于视频等资源
import axios from './axios'

export function getFullUrl(path) {
  if (!path) return ''
  if (path.startsWith('http')) return path
  return axios.defaults.baseURL.replace(/\/$/, '') + path
}

