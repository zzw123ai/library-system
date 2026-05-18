/** 登录态本地存储键名（与 Login、request、路由守卫共用） */
export const TOKEN_KEY = 'token'
export const USER_INFO_KEY = 'userInfo'

/**
 * 写入登录会话（不保存密码）
 * @param {{ token?: string; user: { id?: number; username?: string; role: string } }} payload
 */
export function setSession({ token, user }) {
  if (token) {
    localStorage.setItem(TOKEN_KEY, token)
  }
  if (user && typeof user === 'object') {
    localStorage.setItem(
      USER_INFO_KEY,
      JSON.stringify({
        id: user.id,
        username: user.username,
        role: String(user.role ?? ''),
      })
    )
  }
}

export function clearSession() {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(USER_INFO_KEY)
}

export function getToken() {
  return localStorage.getItem(TOKEN_KEY) || ''
}

/** @returns {{ id?: number; username?: string; role: string } | null} */
export function getCurrentUser() {
  try {
    const raw = localStorage.getItem(USER_INFO_KEY)
    if (raw) {
      return JSON.parse(raw)
    }
  } catch (e) {
    console.error('Failed to get current user:', e)
  }
  return null
}

export function isLoggedIn() {
  const user = getCurrentUser()
  return !!(getToken() && user?.username)
}

export function isAdmin() {
  const user = getCurrentUser()
  return user?.role === '1' || user?.role === 1
}

export function getCurrentUserId() {
  const user = getCurrentUser()
  return user?.id ?? null
}

/** 从登录接口 data 解析用户与 token */
export function parseLoginData(data) {
  if (!data || typeof data !== 'object') {
    return { token: '', user: null }
  }
  return {
    token: data.token || data.accessToken || '',
    user: {
      id: data.id,
      username: data.username,
      role: String(data.role ?? ''),
    },
  }
}
