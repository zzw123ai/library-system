<!-- 登录页：对接 POST /api/user/login，统一 Result { code, message, data } -->
<template>
  <div class="login-page">
    <div class="login-shell">
      <section class="brand-panel">
        <p class="brand-badge">Campus Library</p>
        <h1>图书馆管理系统</h1>
        <p class="brand-desc">
          统一管理用户、图书与借阅流程，提供更清晰的工作台体验。
        </p>
        <div class="brand-metrics">
          <div class="metric">
            <span>角色权限</span>
            <strong>管理员 / 读者</strong>
          </div>
          <div class="metric">
            <span>核心模块</span>
            <strong>用户 · 图书 · 借阅</strong>
          </div>
        </div>
      </section>

      <el-card class="login-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <span class="title">欢迎登录</span>
            <span class="sub">请输入账号密码继续使用</span>
          </div>
        </template>

        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-position="top"
          size="large"
          @keyup.enter="handleLogin"
        >
          <el-form-item label="用户名" prop="username">
            <el-input
              v-model="form.username"
              placeholder="管理员 admin / 读者 user1"
              clearable
              autocomplete="username"
            />
          </el-form-item>

          <el-form-item label="密码" prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              show-password
              clearable
              autocomplete="current-password"
            />
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              class="login-btn"
              :loading="loading"
              @click="handleLogin"
            >
              登录
            </el-button>
          </el-form-item>
        </el-form>

        <p class="hint">演示账号：admin / password（管理员）；user1 / user123（读者）</p>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login as loginApi } from '../api/user.js'
import { setSession, parseLoginData } from '../utils/auth.js'

const route = useRoute()
const router = useRouter()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

function safeRedirectPath(raw) {
  if (typeof raw !== 'string' || !raw.startsWith('/') || raw.startsWith('//')) {
    return null
  }
  if (raw === '/login' || raw.startsWith('/login?')) {
    return null
  }
  return raw
}

async function handleLogin() {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch {
    return
  }

  loading.value = true
  try {
    const res = await loginApi({
      username: form.username.trim(),
      password: form.password,
    })

    const { code, message, data } = res.data || {}

    if (code === 200 && data) {
      const { token, user } = parseLoginData(data)
      if (!user?.username || user.role === '') {
        ElMessage.error('登录响应数据不完整，请联系后端检查接口')
        return
      }
      if (!token) {
        ElMessage.error('登录响应缺少 token')
        return
      }

      setSession({ token, user })
      ElMessage.success(message || '登录成功')

      const redirect = safeRedirectPath(route.query.redirect)
      await router.replace(redirect || { path: '/' })
    } else {
      ElMessage.error(message || '用户名或密码错误')
    }
  } catch (err) {
    const msg = err.response?.data?.message
    if (msg) {
      ElMessage.error(msg)
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
}

.login-shell {
  width: min(980px, 100%);
  display: grid;
  grid-template-columns: 1.2fr 1fr;
  gap: 16px;
  align-items: stretch;
}

.brand-panel {
  background: linear-gradient(155deg, #eaf2ff, #f2f7ff 55%, #f6f9ff);
  border: 1px solid rgba(91, 141, 239, 0.2);
  border-radius: 12px;
  padding: 28px 24px;
  color: #1f2937;
  position: relative;
  overflow: hidden;
  box-shadow: 0 10px 28px rgba(15, 23, 42, 0.08);
}

.brand-panel::after {
  content: '';
  position: absolute;
  width: 180px;
  height: 180px;
  border-radius: 50%;
  right: -48px;
  top: -48px;
  background: rgba(122, 166, 255, 0.25);
}

.brand-badge {
  display: inline-block;
  font-size: 12px;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  padding: 6px 10px;
  border-radius: 8px;
  color: #355ea8;
  background: rgba(91, 141, 239, 0.12);
  margin-bottom: 12px;
}

.brand-panel h1 {
  font-size: 28px;
  line-height: 1.2;
  margin-bottom: 10px;
}

.brand-desc {
  color: #4b5563;
  line-height: 1.8;
  margin-bottom: 20px;
  font-size: 14px;
}

.brand-metrics {
  display: grid;
  gap: 10px;
}

.metric {
  background: rgba(255, 255, 255, 0.7);
  border: 1px solid rgba(91, 141, 239, 0.18);
  border-radius: 10px;
  padding: 10px 12px;
}

.metric span {
  display: block;
  font-size: 12px;
  opacity: 0.85;
}

.metric strong {
  font-size: 14px;
  margin-top: 4px;
  display: block;
}

.login-card {
  border-radius: 12px;
  padding-top: 2px;
}

.card-header {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.title {
  font-size: 22px;
  font-weight: 700;
  color: #111827;
}

.sub {
  font-size: 13px;
  color: #6b7280;
}

.login-btn {
  width: 100%;
  margin-top: 6px;
  height: 40px;
  border-radius: 8px;
  font-weight: 600;
  letter-spacing: 0.02em;
}

.hint {
  margin: 2px 0 0;
  font-size: 11px;
  color: #6b7280;
  line-height: 1.5;
  background: rgba(91, 141, 239, 0.08);
  border-radius: 8px;
  padding: 8px 10px;
}

@media (max-width: 900px) {
  .login-shell {
    grid-template-columns: 1fr;
  }

  .brand-panel {
    padding: 28px 22px;
  }

  .brand-panel h1 {
    font-size: 26px;
  }
}
</style>
