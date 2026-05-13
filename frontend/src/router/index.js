import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Home from '../views/Home.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/',
    name: 'Home',
    component: Home,
    children: [
      {
        path: 'users',
        name: 'UserManage',
        component: () => import('../views/UserManage.vue')
      },
      {
        path: 'books',
        name: 'BookManage',
        component: () => import('../views/BookManage.vue')
      },
      {
        path: 'borrows',
        name: 'BorrowManage',
        component: () => import('../views/BorrowManage.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
