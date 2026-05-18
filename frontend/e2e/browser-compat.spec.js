import { test, expect } from '@playwright/test'
import { loginAsAdmin, openMenu } from './helpers.js'

test.describe('Chrome / Edge 兼容性冒烟', () => {
  test('登录与三大模块页面可访问', async ({ page, browserName }) => {
    test.info().annotations.push({ type: 'browser', description: browserName })

    await page.goto('/login')
    await expect(page.getByRole('heading', { name: '图书馆管理系统' })).toBeVisible()
    await expect(page.getByRole('button', { name: '登录' })).toBeVisible()

    await loginAsAdmin(page)

    await openMenu(page, '图书管理')
    await expect(page.getByRole('heading', { name: '图书管理' })).toBeVisible()
    await expect(page.locator('.data-table tbody tr').first()).toBeVisible()

    await openMenu(page, '借阅管理')
    await expect(page.getByRole('heading', { name: '借阅管理' })).toBeVisible()

    await openMenu(page, '用户管理')
    await expect(page.getByRole('heading', { name: '用户管理' })).toBeVisible()
  })

  test('图书搜索框与表格交互正常', async ({ page }) => {
    await loginAsAdmin(page)
    await openMenu(page, '图书管理')
    const search = page.getByPlaceholder(/搜索书名/)
    await search.fill('红楼梦')
    await page.waitForTimeout(400)
    await expect(page.locator('.data-table tbody')).toContainText('红楼梦')
  })
})
