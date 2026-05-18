/** 侧栏菜单配置：adminOnly 项仅管理员可见；读者使用 readerLabel */
export const MENU_ITEMS = [
  { path: '/', icon: '🏠', label: '首页' },
  { path: '/users', icon: '👥', label: '用户管理', adminOnly: true },
  {
    path: '/books',
    icon: '📚',
    label: '图书管理',
    readerLabel: '图书检索',
  },
  {
    path: '/borrows',
    icon: '📖',
    label: '借阅管理',
    readerLabel: '我的借阅',
  },
]

/**
 * @param {boolean} admin
 */
export function getVisibleMenus(admin) {
  return MENU_ITEMS.filter((item) => !item.adminOnly || admin).map((item) => ({
    path: item.path,
    icon: item.icon,
    label: admin ? item.label : item.readerLabel || item.label,
  }))
}

/**
 * @param {string} path
 * @param {boolean} admin
 */
export function getPageTitle(path, admin) {
  const item = MENU_ITEMS.find((m) => m.path === path)
  if (!item) return '首页'
  return admin ? item.label : item.readerLabel || item.label
}
