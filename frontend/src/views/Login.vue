<!--
  图书馆管理系统 - 登录页（Vue3 组合式 API + Element Plus）
-->
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
            placeholder="请输入用户名"
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
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login as loginApi } from '../api/user.js'
import {
  setSession,
  sanitizeUser,
  getDefaultRouteAfterLogin,
} from '../utils/auth.js'

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

function pickToken(data) {
  if (!data || typeof data !== 'object') return ''
  return data.token || data.accessToken || ''
}

/** 防止 open redirect：仅允许站内相对路径 */
function safeRedirectPath(raw) {
  if (typeof raw !== 'string' || !raw.startsWith('/') || raw.startsWith('//')) {
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

    const body = res.data || {}
    const { code, message, data } = body

    if (code === 200) {
      const user = sanitizeUser(data)
      if (!user || user.role === '') {
        ElMessage.error('登录响应缺少用户信息或角色，无法完成鉴权')
        return
      }

      const token = pickToken(data)
      setSession({ token, user })

      if (!token) {
        ElMessage.warning(
          '未返回 token：已使用本地会话信息维持登录，正式环境请后端下发 JWT'
        )
      }

      ElMessage.success(message || '登录成功')

      const redirect = safeRedirectPath(route.query.redirect)
      if (redirect) {
        await router.replace(redirect)
      } else {
        await router.replace(getDefaultRouteAfterLogin())
      }
    } else {
      ElMessage.error(message || '登录失败')
    }
  } catch {
    // 通用错误由 request 拦截器提示
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
</style>
