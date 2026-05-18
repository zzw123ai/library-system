import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'
import Login from '../views/Login.vue'
import Home from '../views/Home.vue'
import { isLoggedIn, isAdmin } from '../utils/auth.js'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { public: true, title: '登录' },
  },
  {
    path: '/',
    name: 'Home',
    component: Home,
    meta: { requiresAuth: true, title: '首页' },
    children: [
      {
        path: 'users',
        name: 'UserManage',
        component: () => import('../views/UserManage.vue'),
        meta: { requiresAuth: true, adminOnly: true, title: '用户管理' },
      },
      {
        path: 'books',
        name: 'BookManage',
        component: () => import('../views/BookManage.vue'),
        meta: { requiresAuth: true, title: '图书管理' },
      },
      {
        path: 'borrows',
        name: 'BorrowManage',
        component: () => import('../views/BorrowManage.vue'),
        meta: { requiresAuth: true, title: '借阅管理' },
      },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to, _from, next) => {
  if (to.meta.title) {
    document.title = `${to.meta.title} - 图书馆管理系统`
  }

  if (to.path === '/login') {
    if (isLoggedIn()) {
      next({ path: '/' })
    } else {
      next()
    }
    return
  }

  if (to.matched.some((r) => r.meta.requiresAuth) && !isLoggedIn()) {
    next({
      path: '/login',
      query: { redirect: to.fullPath },
    })
    return
  }

  if (to.meta.adminOnly && !isAdmin()) {
    ElMessage.warning('当前账号无权访问该页面')
    next({ path: '/' })
    return
  }

  next()
})

export default router
