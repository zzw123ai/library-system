import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Home from '../views/Home.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { title: '登录' },
  },
  {
    path: '/',
    name: 'Home',
    component: Home,
    meta: { title: '首页' },
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
