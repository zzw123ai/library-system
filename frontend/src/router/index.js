import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'
import Login from '../views/Login.vue'
import MainLayout from '../layouts/MainLayout.vue'
import Dashboard from '../views/Dashboard.vue'
import PlaceholderPage from '../views/PlaceholderPage.vue'
import { isLoggedIn, getUserInfo } from '../utils/auth.js'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { title: '登录', public: true },
  },
  {
    path: '/',
    component: MainLayout,
    meta: { requiresAuth: true },
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: Dashboard,
        meta: { title: '工作台', roles: ['0', '1'] },
      },
      {
        path: 'books',
        name: 'Books',
        component: PlaceholderPage,
        meta: { title: '图书管理', roles: ['1'] },
      },
      {
        path: 'readers',
        name: 'Readers',
        component: PlaceholderPage,
        meta: { title: '读者管理', roles: ['1'] },
      },
      {
        path: 'borrow-manage',
        name: 'BorrowManage',
        component: PlaceholderPage,
        meta: { title: '借阅管理', roles: ['1'] },
      },
      {
        path: 'book-query',
        name: 'BookQuery',
        component: PlaceholderPage,
        meta: { title: '图书检索', roles: ['0'] },
      },
      {
        path: 'my-borrows',
        name: 'MyBorrows',
        component: PlaceholderPage,
        meta: { title: '我的借阅', roles: ['0'] },
      },
      {
        path: 'profile',
        name: 'Profile',
        component: PlaceholderPage,
        meta: { title: '个人中心', roles: ['0', '1'] },
      },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

/**
 * 全局前置守卫：未登录拦截、已登录访问登录页则进工作台、按角色限制路由
 */
router.beforeEach((to, _from, next) => {
  const title = to.meta.title
  if (title) {
    document.title = `${title} - 图书馆管理系统`
  }

  const loggedIn = isLoggedIn()

  if (to.name === 'Login') {
    if (loggedIn) {
      next({ name: 'Dashboard' })
    } else {
      next()
    }
    return
  }

  if (to.matched.some((record) => record.meta.requiresAuth)) {
    if (!loggedIn) {
      next({
        name: 'Login',
        query: { redirect: to.fullPath },
      })
      return
    }

    const leaf = to.matched[to.matched.length - 1]
    const needRoles = leaf?.meta?.roles
    if (Array.isArray(needRoles) && needRoles.length > 0) {
      const user = getUserInfo()
      const role = user ? String(user.role) : ''
      if (!needRoles.includes(role)) {
        ElMessage.warning('当前角色无权访问该页面')
        next({ name: 'Dashboard' })
        return
      }
    }
  }

  next()
})

export default router
