<template>
  <div class="home-page">
    <div class="sidebar">
      <div class="logo">
        <h2>图书馆管理系统</h2>
      </div>
      <nav class="menu">
        <router-link
          v-for="item in visibleMenus"
          :key="item.path"
          :to="item.path"
          class="menu-item"
          :class="{ active: $route.path === item.path }"
        >
          <span class="icon">{{ item.icon }}</span>
          <span>{{ item.label }}</span>
        </router-link>
      </nav>
      <!-- 当前用户信息 -->
      <div class="user-info">
        <span class="username">{{ currentUser?.username }}</span>
        <span class="role">{{ isAdmin ? '管理员' : '读者' }}</span>
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
          <el-alert
            v-if="overdueReminder.overdueCount > 0"
            type="error"
            :closable="false"
            show-icon
            class="overdue-alert"
            :title="overdueAlertTitle"
          >
            <template #default>
              <ul class="overdue-list">
                <li v-for="item in overdueReminder.records" :key="item.id">
                  《{{ item.bookTitle }}》— 应还 {{ item.dueDate }}，已逾期 {{ item.overdueDays }} 天
                  <span v-if="isAdmin">（借阅人：{{ item.username }}）</span>
                </li>
              </ul>
              <el-button type="primary" link @click="goBorrows">
                {{ isAdmin ? '前往借阅管理归还' : '前往我的借阅归还' }}
              </el-button>
            </template>
          </el-alert>

          <h2>欢迎使用图书馆管理系统！</h2>
          <p v-if="isAdmin">
            管理员工作台：可进行用户管理、图书管理与全馆借阅管理。
          </p>
          <p v-else>
            读者工作台：可检索馆藏图书、办理借阅与归还，并查看个人借阅记录。
          </p>
          <div class="stats">
            <div v-if="isAdmin" class="stat-item">
              <span class="stat-value">{{ userCount }}</span>
              <span class="stat-label">用户数量</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ bookCount }}</span>
              <span class="stat-label">馆藏图书</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ borrowCount }}</span>
              <span class="stat-label">{{ isAdmin ? '借阅记录' : '我的借阅' }}</span>
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
import { getBorrows, getOverdueReminder } from '../api/borrow'
import { getCurrentUser, clearSession, isLoggedIn, isAdmin as checkAdmin, getCurrentUserId } from '../utils/auth.js'
import { getVisibleMenus, getPageTitle } from '../config/menu.js'

const router = useRouter()
const userCount = ref(0)
const bookCount = ref(0)
const borrowCount = ref(0)
const overdueReminder = ref({ overdueCount: 0, records: [] })

// 当前用户信息
const currentUser = ref(null)

const isAdmin = computed(() => checkAdmin())

const visibleMenus = computed(() => getVisibleMenus(isAdmin.value))

const overdueAlertTitle = computed(() => {
  const n = overdueReminder.value.overdueCount
  if (isAdmin.value) {
    return `逾期提醒：全馆共有 ${n} 条逾期未还记录`
  }
  return `逾期提醒：您有 ${n} 本书已逾期，请尽快归还`
})

function goBorrows() {
  router.push('/borrows')
}

const loadCurrentUser = () => {
  currentUser.value = getCurrentUser()
}

const currentTitle = computed(() =>
  getPageTitle(router.currentRoute.value.path, isAdmin.value)
)

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

const loadOverdueReminder = async () => {
  const userId = isAdmin.value ? undefined : getCurrentUserId()
  const res = await getOverdueReminder(userId)
  if (res.data?.code === 200 && res.data.data) {
    overdueReminder.value = res.data.data
  }
}

const loadStats = async () => {
  const booksRes = await getBooks()
  if (booksRes.data?.code === 200) {
    bookCount.value = booksRes.data.data.length
  }

  const borrowsRes = await getBorrows()
  if (borrowsRes.data?.code === 200) {
    const list = borrowsRes.data.data
    if (isAdmin.value) {
      borrowCount.value = list.length
    } else {
      const uid = getCurrentUserId()
      borrowCount.value = list.filter((b) => b.userId === uid).length
    }
  }

  if (isAdmin.value) {
    const usersRes = await getUsers()
    if (usersRes.data?.code === 200) {
      userCount.value = usersRes.data.data.length
    }
  }
}

onMounted(() => {
  if (!isLoggedIn()) {
    router.replace('/login')
    return
  }
  loadCurrentUser()
  loadStats()
  loadOverdueReminder()
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

.overdue-alert {
  margin-bottom: 24px;
}

.overdue-list {
  margin: 8px 0 12px;
  padding-left: 20px;
  color: #606266;
  font-size: 14px;
  line-height: 1.8;
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