/**
 * 获取当前登录用户信息
 * @returns {Object|null} 用户信息对象
 */
export function getCurrentUser() {
  try {
    const userInfo = localStorage.getItem('userInfo')
    if (userInfo) {
      return JSON.parse(userInfo)
    }
  } catch (e) {
    console.error('Failed to get current user:', e)
  }
  return null
}

/**
 * 判断是否是管理员
 * @returns {boolean} true表示管理员，false表示普通用户
 */
export function isAdmin() {
  const user = getCurrentUser()
  return user?.role === '1' || user?.role === 1
}

/**
 * 获取当前用户ID
 * @returns {number|null} 用户ID
 */
export function getCurrentUserId() {
  const user = getCurrentUser()
  return user?.id || null
}