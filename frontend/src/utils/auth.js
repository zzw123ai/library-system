/**
 * 登录态与角色（与后端 User.role 对齐：0 读者，1 管理员）
 * 无 JWT 时仍可用 userInfo 维持会话，便于开发与路由守卫联调。
 */
export const TOKEN_KEY = 'token'
export const USER_KEY = 'library_user'

/** 后端约定：普通读者 */
export const ROLE_READER = '0'
/** 后端约定：管理员 */
export const ROLE_ADMIN = '1'

/**
 * @param {{ token?: string; user: { id?: number; username?: string; role: string } }} payload
 */
export function setSession({ token, user }) {
  if (token) {
    localStorage.setItem(TOKEN_KEY, token)
  }
  if (user && typeof user === 'object') {
    localStorage.setItem(USER_KEY, JSON.stringify(user))
  }
}

export function clearSession() {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(USER_KEY)
}

export function getToken() {
  return localStorage.getItem(TOKEN_KEY) || ''
}

/** @returns {{ id?: number; username?: string; role: string } | null} */
export function getUserInfo() {
  const raw = localStorage.getItem(USER_KEY)
  if (!raw) return null
  try {
    return JSON.parse(raw)
  } catch {
    return null
  }
}

export function isLoggedIn() {
  return !!(getToken() || getUserInfo())
}

export function isAdminRole(role) {
  return String(role) === ROLE_ADMIN
}

/** 登录成功后默认进入的路由（工作台）；后续若需管理员直达某页可改此处 */
export function getDefaultRouteAfterLogin() {
  return { name: 'Dashboard' }
}

/**
 * 去掉密码等敏感字段，只存展示与鉴权所需字段
 * @param {Record<string, unknown>} data 登录接口 data
 */
export function sanitizeUser(data) {
  if (!data || typeof data !== 'object') return null
  return {
    id: data.id,
    username: data.username,
    role: String(data.role ?? ''),
  }
}
