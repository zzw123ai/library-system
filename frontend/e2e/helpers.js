/** 与 playwright.config.js 中 BACKEND_PORT 保持一致 */
export const API_BASE = 'http://127.0.0.1:18080'

/** @param {import('@playwright/test').Page} page */
export async function loginAsAdmin(page) {
  const response = await page.request.post(`${API_BASE}/api/user/login`, {
    data: { username: 'admin', password: 'password' },
  })
  if (!response.ok()) {
    throw new Error(`登录接口 HTTP ${response.status()}`)
  }
  const body = await response.json()
  if (body.code !== 200 || !body.data?.token) {
    throw new Error(`登录失败: ${body.message || 'unknown'}`)
  }
  const { token, id, username, role } = body.data
  await page.addInitScript((session) => {
    localStorage.setItem('token', session.token)
    localStorage.setItem(
      'userInfo',
      JSON.stringify({
        id: session.id,
        username: session.username,
        role: String(session.role ?? ''),
      })
    )
  }, { token, id, username, role })
  await page.goto('/')
  await page.waitForSelector('.home-page', { timeout: 15_000 })
}

/** @param {import('@playwright/test').Page} page @param {string} menuLabel */
export async function openMenu(page, menuLabel) {
  await page.locator('.menu-item', { hasText: menuLabel }).click()
}
