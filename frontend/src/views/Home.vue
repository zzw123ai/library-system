<template>
  <div class="home-page">
    <div class="sidebar anim-fade-scale">
      <div class="logo">
        <h2>图书馆管理系统</h2>
      </div>
      <nav class="menu">
        <router-link
          v-for="item in visibleMenus"
          :key="item.path"
          :to="item.path"
          class="menu-item"
          :class="{ active: isMenuActive(item.path) }"
        >
          <span class="icon">{{ item.icon }}</span>
          <span>{{ item.label }}</span>
        </router-link>
      </nav>
      <!-- 当前用户信息 -->
      <router-link to="/profile" class="user-info user-info-link">
        <span class="username">{{ currentUser?.username }}</span>
        <span class="role">{{ isAdmin ? '管理员' : '读者' }}</span>
        <span class="profile-hint">个人中心 →</span>
      </router-link>
    </div>
    <div class="main-content">
      <header class="header">
        <div class="header-title">
          {{ currentTitle }}
        </div>
        <button class="logout-btn" @click="handleLogout">退出登录</button>
      </header>
      <main class="content">
        <div v-if="$route.path === '/'" class="welcome-card anim-fade-up">
          <div v-if="shouldShowOverdueAlert" class="overdue-alert-wrap">
            <el-alert
              type="error"
              :closable="false"
              show-icon
              class="overdue-alert"
              :title="overdueAlertTitle"
            >
              <template #icon>
                <el-icon
                  class="overdue-alert-icon-btn"
                  title="关闭提醒"
                  @click.stop="dismissOverdueAlert"
                >
                  <CircleCloseFilled />
                </el-icon>
              </template>
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
          </div>

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
        
        <router-view v-else v-slot="{ Component }">
          <transition name="route-fade" mode="out-in">
            <component :is="Component" :key="$route.path" class="page-view" />
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { CircleCloseFilled } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUsers } from '../api/user'
import { getBooks } from '../api/book'
import { getBorrows, getOverdueReminder } from '../api/borrow'
import { getCurrentUser, clearSession, isLoggedIn, isAdmin as checkAdmin, getCurrentUserId } from '../utils/auth.js'
import { logout as apiLogout } from '../api/user.js'
import { getVisibleMenus, getPageTitle } from '../config/menu.js'

const router = useRouter()
const route = useRoute()

function isMenuActive(path) {
  if (path === '/') return route.path === '/'
  return route.path === path
}
const userCount = ref(0)
const bookCount = ref(0)
const borrowCount = ref(0)
const overdueReminder = ref({ overdueCount: 0, records: [] })
const dismissedReminderSignature = ref('')
const overdueAlertDismissed = ref(false)

// 当前用户信息
const currentUser = ref(null)

const isAdmin = computed(() => checkAdmin())

const visibleMenus = computed(() => getVisibleMenus(isAdmin.value))
const shouldShowOverdueAlert = computed(() => {
  return overdueReminder.value.overdueCount > 0 && !overdueAlertDismissed.value
})

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

function buildReminderSignature(reminder) {
  const records = Array.isArray(reminder?.records) ? reminder.records : []
  const ids = records.map((r) => `${r.id}-${r.status}-${r.dueDate}`).join('|')
  return `${reminder?.overdueCount || 0}:${ids}`
}

function dismissOverdueAlert() {
  overdueAlertDismissed.value = true
  dismissedReminderSignature.value = buildReminderSignature(overdueReminder.value)
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
  try {
    await apiLogout()
  } catch {
    // 网络异常时仍清除本地会话
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
    const latestSignature = buildReminderSignature(res.data.data)
    if (!overdueAlertDismissed.value) {
      return
    }
    if (latestSignature !== dismissedReminderSignature.value) {
      overdueAlertDismissed.value = false
      dismissedReminderSignature.value = ''
    }
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
  background: transparent;
}

.sidebar {
  width: 248px;
  margin: 16px;
  background: linear-gradient(180deg, #f5f9ff, #eef5ff 45%, #e8f1ff);
  color: #1f2937;
  border: 1px solid rgba(91, 141, 239, 0.16);
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  padding: 20px 0 16px;
  box-shadow: 0 10px 26px rgba(15, 23, 42, 0.08);
}

.logo {
  padding: 6px 20px 14px;
  border-bottom: 1px solid rgba(91, 141, 239, 0.18);
  margin-bottom: 12px;
}

.logo h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  letter-spacing: 0.02em;
}

.menu {
  flex: 1;
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  color: #4b5563;
  text-decoration: none;
  border-radius: 8px;
  transition: 0.2s ease;
  font-size: 14px;
  font-weight: 500;
}

.menu-item:hover,
.menu-item.active {
  background: rgba(91, 141, 239, 0.12);
  color: #1f2937;
  transform: translateX(2px);
}

.icon {
  font-size: 16px;
}

.user-info {
  margin: 0 12px;
  padding: 12px;
  border-top: 1px solid rgba(91, 141, 239, 0.2);
  background: rgba(255, 255, 255, 0.62);
  border-radius: 10px;
  text-align: center;
}

.user-info .username {
  display: block;
  font-size: 13px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 6px;
}

.user-info .role {
  display: inline-block;
  padding: 4px 10px;
  font-size: 11px;
  border-radius: 8px;
  background: rgba(91, 141, 239, 0.12);
  color: #355ea8;
}

.user-info-link {
  display: block;
  text-decoration: none;
  color: inherit;
  transition: background 0.2s ease, transform 0.2s ease;
}

.user-info-link:hover {
  background: rgba(91, 141, 239, 0.1);
  transform: translateY(-1px);
}

.profile-hint {
  display: block;
  margin-top: 8px;
  font-size: 11px;
  color: #5b8def;
  font-weight: 500;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  margin: 16px 16px 16px 0;
  gap: 12px;
}

.header {
  background: rgba(255, 255, 255, 0.88);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(99, 102, 241, 0.12);
  border-radius: 12px;
  padding: 12px 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 10px 24px rgba(17, 24, 39, 0.06);
}

.header-title {
  font-size: 18px;
  font-weight: 600;
  color: #111827;
}

.logout-btn {
  padding: 8px 12px;
  background: linear-gradient(120deg, #8aa9dd, #7f9ac8);
  color: #fff;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 12px;
  font-weight: 600;
  transition: 0.2s ease;
}

.logout-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 8px 14px rgba(127, 154, 200, 0.28);
}

.content {
  flex: 1;
  background: rgba(255, 255, 255, 0.76);
  border-radius: 12px;
  border: 1px solid rgba(99, 102, 241, 0.12);
  box-shadow: 0 12px 26px rgba(17, 24, 39, 0.05);
  padding: 22px;
  overflow-x: hidden;
  overflow-y: auto;
}

.welcome-card {
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.95), #ffffff);
  padding: 24px;
  border-radius: 12px;
  border: 1px solid rgba(99, 102, 241, 0.12);
}

.welcome-card h2 {
  margin-bottom: 10px;
  color: #111827;
  font-size: 24px;
  font-weight: 600;
}

.welcome-card p {
  color: #4b5563;
  margin-bottom: 24px;
  font-size: 14px;
  line-height: 1.7;
}

.overdue-alert-wrap {
  position: relative;
  margin-bottom: 24px;
}

.overdue-alert {
  margin-bottom: 0;
  border-radius: 8px;
}

.overdue-alert-icon-btn {
  cursor: pointer;
  pointer-events: auto;
}

.overdue-alert :deep(.el-alert__icon) {
  pointer-events: auto;
}

.overdue-list {
  margin: 8px 0 12px;
  padding-left: 20px;
  color: #374151;
  font-size: 14px;
  line-height: 1.8;
}

.stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.stat-item {
  text-align: left;
  padding: 20px 16px;
  background: linear-gradient(130deg, #f2f7ff, #f8fbff);
  border-radius: 10px;
  border: 1px solid rgba(99, 102, 241, 0.18);
}

.page-view {
  min-height: 200px;
}

.stat-value {
  display: block;
  font-size: 34px;
  font-weight: 700;
  background: linear-gradient(120deg, #5b8def, #7aa6ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.stat-label {
  display: block;
  color: #4b5563;
  margin-top: 8px;
  font-size: 13px;
  font-weight: 500;
}

@media (max-width: 980px) {
  .sidebar {
    width: 210px;
    margin-right: 10px;
  }

  .main-content {
    margin-left: 0;
  }
}

@media (max-width: 820px) {
  .home-page {
    flex-direction: column;
  }

  .sidebar,
  .main-content {
    width: calc(100% - 24px);
    margin: 12px;
  }
}
</style>