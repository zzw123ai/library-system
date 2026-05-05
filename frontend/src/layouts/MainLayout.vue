<!--
  主布局：顶部栏（用户信息 + 退出登录）+ 左侧菜单（按角色显示）
-->
<template>
  <el-container class="layout-root">
    <el-aside width="220px" class="aside">
      <div class="logo">图书馆系统</div>
      <el-menu
        :default-active="activeMenu"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
      >
        <el-menu-item
          v-for="item in visibleMenus"
          :key="item.path"
          :index="item.path"
        >
          <span>{{ item.title }}</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container direction="vertical">
      <el-header class="header">
        <span class="header-title">{{ currentTitle }}</span>
        <div class="header-right">
          <span class="user-line">
            {{ user?.username }}
            <el-tag :type="isAdmin ? 'danger' : 'info'" size="small" effect="plain">
              {{ isAdmin ? '管理员' : '读者' }}
            </el-tag>
          </span>
          <el-button type="primary" link @click="handleLogout">退出登录</el-button>
        </div>
      </el-header>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import {
  getUserInfo,
  clearSession,
  isAdminRole,
} from '../utils/auth.js'

const route = useRoute()
const router = useRouter()

const user = getUserInfo()
const isAdmin = computed(() => isAdminRole(user?.role ?? ''))

/** 与路由 path 一致（子路由全路径） */
const menuDefs = [
  { path: '/dashboard', title: '工作台', roles: ['0', '1'] },
  { path: '/books', title: '图书管理', roles: ['1'] },
  { path: '/readers', title: '读者管理', roles: ['1'] },
  { path: '/borrow-manage', title: '借阅管理', roles: ['1'] },
  { path: '/book-query', title: '图书检索', roles: ['0'] },
  { path: '/my-borrows', title: '我的借阅', roles: ['0'] },
  { path: '/profile', title: '个人中心', roles: ['0', '1'] },
]

const visibleMenus = computed(() => {
  const r = String(user?.role ?? '')
  return menuDefs.filter((m) => m.roles.includes(r))
})

const activeMenu = computed(() => route.path)

const currentTitle = computed(() => route.meta.title || '首页')

async function handleLogout() {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
  } catch {
    return
  }
  clearSession()
  router.replace({ name: 'Login' })
}
</script>

<style scoped>
.layout-root {
  min-height: 100vh;
}
.aside {
  background-color: #304156;
}
.logo {
  height: 56px;
  line-height: 56px;
  text-align: center;
  color: #fff;
  font-weight: 600;
  font-size: 15px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #ebeef5;
  background: #fff;
}
.header-title {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}
.user-line {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: #606266;
  font-size: 14px;
}
.main {
  background: #f0f2f5;
  padding: 20px;
}
</style>
