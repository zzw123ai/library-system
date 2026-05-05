/**
 * 用户相关接口（登录模块使用）
 * 后端示例：POST /api/user/login，请求体 { username, password }
 */
import request from '../utils/request.js'

/**
 * 登录
 * @param {{ username: string; password: string }} payload
 * @returns {Promise<import('axios').AxiosResponse>}
 */
export function login(payload) {
  return request({
    url: '/api/user/login',
    method: 'post',
    data: payload,
  })
}
