<!-- 登录页：对接 POST /api/user/login，统一 Result { code, message, data } -->
<template>
  <div class="login-page">
    <el-card class="login-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span class="title">图书馆管理系统</span>
          <span class="sub">请登录您的账号</span>
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
  box-sizing: border-box;
  background: linear-gradient(135deg, #e8f0fe 0%, #f5f7fa 45%, #fdfbfb 100%);
}

.login-card {
  width: 100%;
  max-width: 420px;
  border-radius: 12px;
}

.card-header {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.title {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.sub {
  font-size: 13px;
  color: #909399;
}

.login-btn {
  width: 100%;
  margin-top: 4px;
}

.hint {
  margin: 0;
  font-size: 12px;
  color: #909399;
  line-height: 1.5;
}
</style>
