/**
 * Axios 实例封装（示例）
 * - baseURL：开发环境可在 .env.development 中配置 VITE_API_BASE_URL
 * - 统一超时、错误时可在此扩展请求头等逻辑
 */
import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '',
  timeout: 15000,
})

request.interceptors.response.use(
  (response) => response,
  (error) => {
    const msg =
      error.response?.data?.message ||
      error.message ||
      '网络异常，请稍后重试'
    ElMessage.error(msg)
    return Promise.reject(error)
  }
)

export default request
