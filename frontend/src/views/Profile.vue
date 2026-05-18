<template>
  <div class="profile-page">
    <h2 class="page-title anim-fade-up">个人中心</h2>

    <div class="profile-grid">
      <section class="profile-card">
        <div class="avatar">{{ avatarLetter }}</div>
        <h3 class="profile-name">{{ profile.username || '—' }}</h3>
        <span class="role-tag">{{ roleLabel }}</span>
        <p class="profile-id">用户 ID：{{ profile.id ?? '—' }}</p>
        <div class="quick-links">
          <router-link to="/books" class="quick-link">图书检索</router-link>
          <router-link to="/borrows" class="quick-link">{{ isAdmin ? '借阅管理' : '我的借阅' }}</router-link>
        </div>
      </section>

      <div class="profile-main">
        <section class="panel stats-panel">
          <h4 class="panel-title">借阅概览</h4>
          <div class="stats-row">
            <div class="stat-chip">
              <span class="stat-num">{{ borrowStats.active }}</span>
              <span class="stat-text">借阅中</span>
            </div>
            <div class="stat-chip stat-warn">
              <span class="stat-num">{{ borrowStats.overdue }}</span>
              <span class="stat-text">已逾期</span>
            </div>
            <div class="stat-chip stat-ok">
              <span class="stat-num">{{ borrowStats.returned }}</span>
              <span class="stat-text">已归还</span>
            </div>
            <div class="stat-chip">
              <span class="stat-num">{{ borrowStats.total }}</span>
              <span class="stat-text">累计借阅</span>
            </div>
          </div>
          <el-button v-if="borrowStats.overdue > 0" type="primary" link @click="goBorrows">
            查看逾期并归还 →
          </el-button>
        </section>

        <section class="panel">
          <h4 class="panel-title">账号信息</h4>
          <div class="info-list">
            <div class="info-row">
              <span class="info-label">用户名</span>
              <span class="info-value">{{ profile.username }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">角色</span>
              <span class="info-value">{{ roleLabel }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">账号状态</span>
              <span class="info-value status-ok">正常</span>
            </div>
          </div>
        </section>

        <section class="panel">
          <h4 class="panel-title">修改密码</h4>
          <form class="password-form" @submit.prevent="submitPassword">
            <div class="form-group">
              <label>新密码</label>
              <input
                v-model="passwordForm.password"
                type="password"
                placeholder="至少 6 位"
                autocomplete="new-password"
              />
            </div>
            <div class="form-group">
              <label>确认新密码</label>
              <input
                v-model="passwordForm.confirm"
                type="password"
                placeholder="再次输入新密码"
                autocomplete="new-password"
              />
            </div>
            <p class="form-hint">留空则不修改；修改成功后请使用新密码登录。</p>
            <div class="form-actions">
              <button type="button" class="cancel-btn" @click="resetPasswordForm">重置</button>
              <button type="submit" class="submit-btn" :disabled="saving">
                {{ saving ? '保存中…' : '保存密码' }}
              </button>
            </div>
          </form>
        </section>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getMe, updateUser } from '../api/user'
import { getBorrows } from '../api/borrow'
import { getCurrentUser, isAdmin as checkAdmin, getCurrentUserId, setSession, getToken } from '../utils/auth'

const router = useRouter()
const isAdmin = computed(() => checkAdmin())
const profile = ref({ id: null, username: '', role: '0' })
const borrowStats = ref({ total: 0, active: 0, overdue: 0, returned: 0 })
const saving = ref(false)
const passwordForm = ref({ password: '', confirm: '' })

const roleLabel = computed(() => (profile.value.role === '1' ? '管理员' : '读者'))
const avatarLetter = computed(() => {
  const name = profile.value.username || '?'
  return name.charAt(0).toUpperCase()
})

function goBorrows() {
  router.push('/borrows')
}

function resetPasswordForm() {
  passwordForm.value = { password: '', confirm: '' }
}

async function loadProfile() {
  const local = getCurrentUser()
  if (local) {
    profile.value = { ...local, role: String(local.role ?? '0') }
  }
  try {
    const res = await getMe()
    if (res.data?.code === 200 && res.data.data) {
      profile.value = {
        id: res.data.data.id,
        username: res.data.data.username,
        role: String(res.data.data.role ?? '0'),
      }
      setSession({ token: getToken(), user: profile.value })
    }
  } catch {
    // 使用本地缓存
  }
}

async function loadBorrowStats() {
  const res = await getBorrows()
  if (res.data?.code !== 200 || !Array.isArray(res.data.data)) {
    return
  }
  let list = res.data.data
  if (!isAdmin.value) {
    const uid = getCurrentUserId()
    list = list.filter((b) => b.userId === uid)
  }
  borrowStats.value = {
    total: list.length,
    active: list.filter((b) => b.status === 'BORROWED').length,
    overdue: list.filter((b) => b.status === 'OVERDUE').length,
    returned: list.filter((b) => b.status === 'RETURNED').length,
  }
}

async function submitPassword() {
  const pwd = passwordForm.value.password.trim()
  const confirm = passwordForm.value.confirm.trim()
  if (!pwd && !confirm) {
    ElMessage.info('请输入新密码后再保存')
    return
  }
  if (pwd.length < 6) {
    ElMessage.warning('密码至少 6 位')
    return
  }
  if (pwd !== confirm) {
    ElMessage.warning('两次输入的密码不一致')
    return
  }
  saving.value = true
  try {
    const res = await updateUser({
      id: profile.value.id,
      username: profile.value.username,
      password: pwd,
      role: profile.value.role,
    })
    if (res.data?.code === 200) {
      ElMessage.success('密码已更新')
      resetPasswordForm()
    } else {
      ElMessage.error(res.data?.message || '更新失败')
    }
  } catch {
    ElMessage.error('更新失败，请稍后重试')
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  loadProfile()
  loadBorrowStats()
})
</script>

<style scoped>
.profile-page {
  padding: 2px;
}

.page-title {
  margin-bottom: 16px;
  color: #111827;
  font-size: 22px;
  font-weight: 600;
}

.profile-grid {
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: 16px;
  align-items: start;
}

.profile-card {
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.98), #ffffff);
  border: 1px solid rgba(99, 102, 241, 0.14);
  border-radius: 12px;
  padding: 24px 20px;
  text-align: center;
  box-shadow: 0 10px 22px rgba(17, 24, 39, 0.06);
}

.avatar {
  width: 72px;
  height: 72px;
  margin: 0 auto 14px;
  border-radius: 50%;
  background: linear-gradient(135deg, #5b8def, #7aa6ff);
  color: #fff;
  font-size: 28px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 10px 20px rgba(91, 141, 239, 0.28);
  transition: transform 0.2s ease;
}

.profile-card:hover .avatar {
  transform: scale(1.04);
}

.profile-name {
  margin: 0 0 8px;
  font-size: 20px;
  font-weight: 600;
  color: #111827;
}

.role-tag {
  display: inline-block;
  padding: 4px 12px;
  font-size: 12px;
  border-radius: 8px;
  background: rgba(91, 141, 239, 0.12);
  color: #355ea8;
  font-weight: 600;
}

.profile-id {
  margin: 14px 0 18px;
  font-size: 13px;
  color: #6b7280;
}

.quick-links {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.quick-link {
  display: block;
  padding: 8px 12px;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 500;
  color: #355ea8;
  background: rgba(91, 141, 239, 0.08);
  text-decoration: none;
  transition: background 0.2s ease, transform 0.2s ease;
}

.quick-link:hover {
  background: rgba(91, 141, 239, 0.16);
  transform: translateY(-1px);
}

.profile-main {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.panel {
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(99, 102, 241, 0.12);
  border-radius: 12px;
  padding: 18px 20px;
  box-shadow: 0 10px 22px rgba(17, 24, 39, 0.05);
}

.panel-title {
  margin: 0 0 14px;
  font-size: 16px;
  font-weight: 600;
  color: #111827;
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  margin-bottom: 8px;
}

.stat-chip {
  text-align: center;
  padding: 14px 8px;
  border-radius: 10px;
  background: linear-gradient(130deg, #f2f7ff, #f8fbff);
  border: 1px solid rgba(99, 102, 241, 0.14);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.stat-chip:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(91, 141, 239, 0.12);
}

.stat-num {
  display: block;
  font-size: 26px;
  font-weight: 700;
  background: linear-gradient(120deg, #5b8def, #7aa6ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.stat-warn .stat-num {
  background: linear-gradient(120deg, #e58f9b, #ee7f8f);
  -webkit-background-clip: text;
  background-clip: text;
}

.stat-ok .stat-num {
  background: linear-gradient(120deg, #34b8a5, #2ea896);
  -webkit-background-clip: text;
  background-clip: text;
}

.stat-text {
  display: block;
  margin-top: 4px;
  font-size: 12px;
  color: #6b7280;
  font-weight: 500;
}

.info-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 12px;
  border-radius: 8px;
  background: #f8faff;
  border: 1px solid rgba(99, 102, 241, 0.08);
  transition: background 0.2s ease;
}

.info-row:hover {
  background: #f0f5ff;
}

.info-label {
  font-size: 13px;
  color: #6b7280;
}

.info-value {
  font-size: 13px;
  font-weight: 600;
  color: #1f2937;
}

.status-ok {
  color: #2ea896;
}

.password-form .form-group {
  margin-bottom: 12px;
}

.password-form label {
  display: block;
  margin-bottom: 5px;
  font-size: 13px;
  font-weight: 500;
  color: #4b5563;
}

.password-form input {
  width: 100%;
  padding: 9px 11px;
  border: 1px solid rgba(99, 102, 241, 0.22);
  border-radius: 8px;
  font-size: 13px;
  background: #fff;
  box-sizing: border-box;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.password-form input:focus {
  outline: none;
  border-color: #5b8def;
  box-shadow: 0 0 0 3px rgba(91, 141, 239, 0.12);
}

.form-hint {
  margin: 0 0 14px;
  font-size: 12px;
  color: #9ca3af;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.cancel-btn {
  padding: 8px 12px;
  background: #e5e7eb;
  color: #374151;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 12px;
  font-weight: 600;
  transition: background 0.2s ease, transform 0.18s ease;
}

.cancel-btn:hover {
  background: #d1d5db;
}

.cancel-btn:active {
  transform: scale(0.98);
}

.submit-btn {
  padding: 8px 14px;
  background: linear-gradient(120deg, #5b8def, #7aa6ff);
  color: #fff;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 12px;
  font-weight: 600;
  box-shadow: 0 8px 16px rgba(91, 141, 239, 0.2);
  transition: transform 0.18s ease, box-shadow 0.2s ease;
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 10px 18px rgba(91, 141, 239, 0.26);
}

.submit-btn:active:not(:disabled) {
  transform: scale(0.98);
}

.submit-btn:disabled {
  opacity: 0.65;
  cursor: not-allowed;
}

@media (max-width: 900px) {
  .profile-grid {
    grid-template-columns: 1fr;
  }

  .stats-row {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
