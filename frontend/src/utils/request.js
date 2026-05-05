/**
 * Axios 实例封装（示例）
 * - baseURL：开发环境可在 .env.development 中配置 VITE_API_BASE_URL
 * - 统一超时、错误时可在此扩展请求头等逻辑
 */
import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  // 默认指向本地 Spring Boot；若使用 Vite 代理可改为 '/api' 并在 vite.config 里配置 proxy
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
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
