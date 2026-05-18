/**
 * Axios 封装：开发环境走 Vite 代理 /api -> localhost:8080
 */
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { getToken, clearSession } from './auth.js'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '',
  timeout: 15000,
})

request.interceptors.request.use((config) => {
  const token = getToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

function redirectToLogin() {
  clearSession()
  const path = window.location.pathname
  if (!path.startsWith('/login')) {
    window.location.href = '/login'
  }
}

request.interceptors.response.use(
  (response) => {
    const body = response.data
    const url = response.config?.url || ''
  // 业务层 401（如会话失效）；登录接口的 401 由登录页自行提示
    if (body && body.code === 401 && !url.includes('/user/login')) {
      ElMessage.warning(body.message || '登录已失效，请重新登录')
      redirectToLogin()
      return Promise.reject(new Error(body.message || '未授权'))
    }
    return response
  },
  (error) => {
    const status = error.response?.status
    const body = error.response?.data
    const url = error.config?.url || ''

    if (status === 401 && !url.includes('/user/login')) {
      ElMessage.warning(body?.message || '登录已失效，请重新登录')
      redirectToLogin()
      return Promise.reject(error)
    }

    const msg = body?.message || error.message || '网络异常，请稍后重试'
    if (!url.includes('/user/login')) {
      ElMessage.error(msg)
    }
    return Promise.reject(error)
  }
)

export default request
