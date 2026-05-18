<template>
  <div class="home-page">
    <div class="sidebar">
      <div class="logo">
        <h2>图书馆管理系统</h2>
      </div>
      <nav class="menu">
        <router-link to="/" class="menu-item" :class="{ active: $route.path === '/' }">
          <span class="icon">🏠</span>
          <span>首页</span>
        </router-link>
        <!-- 管理员可见：用户管理 -->
        <router-link v-if="isAdmin" to="/users" class="menu-item" :class="{ active: $route.path === '/users' }">
          <span class="icon">👥</span>
          <span>用户管理</span>
        </router-link>
        <!-- 所有用户可见：图书管理 -->
        <router-link to="/books" class="menu-item" :class="{ active: $route.path === '/books' }">
          <span class="icon">📚</span>
          <span>图书管理</span>
        </router-link>
        <!-- 所有用户可见：借阅管理 -->
        <router-link to="/borrows" class="menu-item" :class="{ active: $route.path === '/borrows' }">
          <span class="icon">📖</span>
          <span>借阅管理</span>
        </router-link>
      </nav>
      <!-- 当前用户信息 -->
      <div class="user-info">
        <span class="username">{{ currentUser?.username }}</span>
        <span class="role">{{ isAdmin ? '管理员' : '普通用户' }}</span>
      </div>
    </div>
    <div class="main-content">
      <header class="header">
        <div class="header-title">
          {{ currentTitle }}
        </div>
        <button class="logout-btn" @click="handleLogout">退出登录</button>
      </header>
      <main class="content">
        <div v-if="$route.path === '/'" class="welcome-card">
          <h2>欢迎使用图书馆管理系统！</h2>
          <p>这是一个功能完整的图书馆管理系统，支持用户管理、图书管理和借阅管理。</p>
          <div class="stats">
            <div class="stat-item">
              <span class="stat-value">{{ userCount }}</span>
              <span class="stat-label">用户数量</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ bookCount }}</span>
              <span class="stat-label">图书数量</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ borrowCount }}</span>
              <span class="stat-label">借阅数量</span>
            </div>
          </div>
        </div>
        
        <router-view v-else />
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUsers } from '../api/user'
import { getBooks } from '../api/book'
import { getBorrows } from '../api/borrow'
import { getCurrentUser, clearSession, isLoggedIn, isAdmin as checkAdmin } from '../utils/auth.js'

const router = useRouter()
const userCount = ref(0)
const bookCount = ref(0)
const borrowCount = ref(0)

// 当前用户信息
const currentUser = ref(null)

const isAdmin = computed(() => checkAdmin())

const loadCurrentUser = () => {
  currentUser.value = getCurrentUser()
}

const currentTitle = computed(() => {
  const titles = {
    '/': '首页',
    '/users': '用户管理',
    '/books': '图书管理',
    '/borrows': '借阅管理'
  }
  return titles[router.currentRoute.value.path] || '首页'
})

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
  currentUser.value = null
  ElMessage.success('已退出登录')
  router.replace('/login')
}

const loadStats = async () => {
  const [usersRes, booksRes, borrowsRes] = await Promise.all([
    getUsers(),
    getBooks(),
    getBorrows()
  ])
  if (usersRes.data && usersRes.data.code === 200) {
    userCount.value = usersRes.data.data.length
  }
  if (booksRes.data && booksRes.data.code === 200) {
    bookCount.value = booksRes.data.data.length
  }
  if (borrowsRes.data && borrowsRes.data.code === 200) {
    borrowCount.value = borrowsRes.data.data.length
  }
}

onMounted(() => {
  if (!isLoggedIn()) {
    router.replace('/login')
    return
  }
  loadCurrentUser()
  loadStats()
})
</script>

<style scoped>
.home-page {
  display: flex;
  min-height: 100vh;
  background-color: #f7fafc;
}

.sidebar {
  width: 220px;
  background-color: #2c3e50;
  color: white;
  display: flex;
  flex-direction: column;
  padding: 20px 0;
  transition: all 0.3s ease;
}

.logo {
  text-align: center;
  padding: 20px 0;
  border-bottom: 1px solid #34495e;
  margin-bottom: 20px;
}

.logo h2 {
  margin: 0;
  font-size: 22px;
  font-weight: 600;
}

.menu {
  flex: 1;
  padding: 16px;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 12px 25px;
  color: #bdc3c7;
  text-decoration: none;
  transition: all 0.3s ease;
  font-size: 16px;
}

.menu-item:hover,
.menu-item.active {
  background-color: #34495e;
  color: white;
  border-left: 4px solid #3498db;
}

.icon {
  margin-right: 12px;
  font-size: 18px;
}

.user-info {
  padding: 16px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  text-align: center;
}

.user-info .username {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #e2e8f0;
  margin-bottom: 4px;
}

.user-info .role {
  display: inline-block;
  padding: 4px 12px;
  font-size: 12px;
  border-radius: 20px;
  background: rgba(66, 153, 225, 0.2);
  color: #4299e1;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.header {
  background-color: white;
  padding: 15px 30px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 5px rgba(0,0,0,0.1);
}

.header-title {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
}

.logout-btn {
  padding: 8px 16px;
  background-color: #e74c3c;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.logout-btn:hover {
  background-color: #c0392b;
}

.content {
  flex: 1;
  padding: 30px;
}

.welcome-card {
  background-color: white;
  padding: 36px;
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.06);
}

.welcome-card h2 {
  margin-bottom: 12px;
  color: #1a202c;
  font-size: 24px;
  font-weight: 600;
}

.welcome-card p {
  color: #718096;
  margin-bottom: 32px;
  font-size: 15px;
  line-height: 1.6;
}

.stats {
  display: flex;
  gap: 24px;
}

.stat-item {
  flex: 1;
  text-align: center;
  padding: 28px 20px;
  background: linear-gradient(135deg, #f7fafc 0%, #edf2f7 100%);
  border-radius: 12px;
  transition: all 0.3s ease;
  border: 1px solid #e2e8f0;
}

.stat-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.08);
}

.stat-value {
  display: block;
  font-size: 42px;
  font-weight: 700;
  background: linear-gradient(135deg, #4299e1 0%, #3182ce 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.stat-label {
  display: block;
  color: #718096;
  margin-top: 8px;
  font-size: 14px;
  font-weight: 500;
}

</style>