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
        <router-link to="/users" class="menu-item" :class="{ active: $route.path === '/users' }">
          <span class="icon">👥</span>
          <span>用户管理</span>
        </router-link>
        <router-link to="/books" class="menu-item" :class="{ active: $route.path === '/books' }">
          <span class="icon">📚</span>
          <span>图书管理</span>
        </router-link>
        <router-link to="/borrows" class="menu-item" :class="{ active: $route.path === '/borrows' }">
          <span class="icon">📖</span>
          <span>借阅管理</span>
        </router-link>
      </nav>
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
import { getUsers } from '../api/user'
import { getBooks } from '../api/book'
import { getBorrows } from '../api/borrow'

const router = useRouter()
const userCount = ref(0)
const bookCount = ref(0)
const borrowCount = ref(0)

const currentTitle = computed(() => {
  const titles = {
    '/': '首页',
    '/users': '用户管理',
    '/books': '图书管理',
    '/borrows': '借阅管理'
  }
  return titles[router.currentRoute.value.path] || '首页'
})

const handleLogout = () => {
  localStorage.removeItem('token')
  alert('退出成功')
  router.push('/login')
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
  width: 250px;
  background: linear-gradient(180deg, #1a202c 0%, #2d3748 100%);
  color: white;
  display: flex;
  flex-direction: column;
  box-shadow: 4px 0 15px rgba(0, 0, 0, 0.1);
}

.logo {
  padding: 28px 20px;
  background: linear-gradient(135deg, #4299e1 0%, #3182ce 100%);
  text-align: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  letter-spacing: 1px;
}

.menu {
  flex: 1;
  padding: 16px;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 14px 16px;
  margin-bottom: 6px;
  border-radius: 8px;
  color: #e2e8f0;
  text-decoration: none;
  transition: all 0.3s ease;
  font-size: 14px;
}

.menu-item:hover {
  background-color: rgba(66, 153, 225, 0.2);
  transform: translateX(4px);
}

.menu-item.active {
  background: linear-gradient(135deg, #4299e1 0%, #3182ce 100%);
  box-shadow: 0 4px 12px rgba(66, 153, 225, 0.3);
}

.icon {
  margin-right: 12px;
  font-size: 20px;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.header {
  background: linear-gradient(135deg, #4299e1 0%, #3182ce 100%);
  color: white;
  padding: 20px 28px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 4px 12px rgba(66, 153, 225, 0.3);
}

.header-title {
  font-size: 22px;
  font-weight: 600;
}

.logout-btn {
  padding: 10px 22px;
  background: linear-gradient(135deg, #fc8181 0%, #f56565 100%);
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(252, 129, 129, 0.3);
}

.logout-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(252, 129, 129, 0.4);
}

.content {
  flex: 1;
  padding: 28px;
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