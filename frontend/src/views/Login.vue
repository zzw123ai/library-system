<!--
  图书馆管理系统 - 登录页（Vue3 组合式 API + Element Plus）
  使用方式：在路由中注册本组件，例如 path: '/login', name: 'Login'
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
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login as loginApi } from '../api/user.js'

/** localStorage 中存放 Token 的键名（可按团队规范修改） */
const TOKEN_KEY = 'token'

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

/**
 * 从登录接口 data 中提取 Token（兼容常见字段名）
 * 若后端暂未返回 token，请与后端约定在 data 中增加 token / accessToken
 */
function pickToken(data) {
  if (!data || typeof data !== 'object') return ''
  return data.token || data.accessToken || ''
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
      const token = pickToken(data)
      if (token) {
        localStorage.setItem(TOKEN_KEY, token)
      } else {
        // 当前仓库后端示例仅返回用户对象，无 JWT：不写入假 Token，仅提示约定字段
        ElMessage.warning(
          '登录成功，但响应中未找到 token/accessToken 字段，请后端在登录成功 data 中返回 Token 后再写入 localStorage'
        )
      }

      ElMessage.success(message || '登录成功')

      // 优先跳转到名为 Home 的路由；若未配置则回退到根路径
      try {
        await router.push({ name: 'Home' })
      } catch {
        await router.push({ path: '/' })
      }
    } else {
      ElMessage.error(message || '登录失败')
    }
  } catch {
    // 网络或 4xx/5xx：request 拦截器已提示时可不再重复；此处保留以便业务自定义
    // ElMessage.error 已在 utils/request.js 中处理通用错误
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
