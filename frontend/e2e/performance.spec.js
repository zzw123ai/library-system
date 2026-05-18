import { test, expect } from '@playwright/test'
import { API_BASE, loginAsAdmin, openMenu } from './helpers.js'

/** 计划书：页面加载 < 2s */
const PAGE_LOAD_MAX_MS = 2000
/** 计划书：查询 < 1s */
const QUERY_MAX_MS = 1000

test.describe('性能指标验收', () => {
  test('登录页首屏加载 < 2s', async ({ page }) => {
    const start = Date.now()
    await page.goto('/login')
    await page.waitForSelector('.login-card')
    const elapsed = Date.now() - start
    expect(elapsed, `登录页加载 ${elapsed}ms`).toBeLessThan(PAGE_LOAD_MAX_MS)
  })

  test('登录后进入首页 < 2s', async ({ page }) => {
    const response = await page.request.post(`${API_BASE}/api/user/login`, {
      data: { username: 'admin', password: 'password' },
    })
    const body = await response.json()
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
    const start = Date.now()
    await page.goto('/')
    await page.waitForSelector('.home-page', { timeout: 10_000 })
    const elapsed = Date.now() - start
    expect(elapsed, `工作台首屏 ${elapsed}ms`).toBeLessThan(PAGE_LOAD_MAX_MS)
  })

  test('图书管理页路由切换 < 2s', async ({ page }) => {
    await loginAsAdmin(page)
    const start = Date.now()
    await openMenu(page, '图书管理')
    await page.waitForSelector('.page-title')
    await page.waitForSelector('.data-table tbody tr')
    const elapsed = Date.now() - start
    expect(elapsed, `图书页切换 ${elapsed}ms`).toBeLessThan(PAGE_LOAD_MAX_MS)
  })

  test('图书列表接口响应 < 1s', async ({ page }) => {
    await loginAsAdmin(page)
    const elapsed = await page.evaluate(async (maxMs) => {
      const token = localStorage.getItem('token') || ''
      const t0 = performance.now()
      const res = await fetch('/api/book/list', {
        headers: token ? { Authorization: `Bearer ${token}` } : {},
      })
      await res.json()
      return performance.now() - t0
    }, QUERY_MAX_MS)
    expect(elapsed, `图书列表 API ${elapsed.toFixed(1)}ms`).toBeLessThan(QUERY_MAX_MS)
  })

  test('图书搜索接口响应 < 1s', async ({ page }) => {
    await loginAsAdmin(page)
    const elapsed = await page.evaluate(async () => {
      const token = localStorage.getItem('token') || ''
      const t0 = performance.now()
      const res = await fetch('/api/book/search?keyword=红', {
        headers: token ? { Authorization: `Bearer ${token}` } : {},
      })
      await res.json()
      return performance.now() - t0
    })
    expect(elapsed, `图书搜索 API ${elapsed.toFixed(1)}ms`).toBeLessThan(QUERY_MAX_MS)
  })
})
