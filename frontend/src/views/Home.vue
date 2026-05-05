<!-- 首页占位：后续业务页面可替换为本组件或拆分子路由 -->
<template>
  <div class="home-wrap">
    <el-result icon="success" title="首页" sub-title="登录模块已就绪，可在此扩展图书/借阅等业务页面。">
      <template #extra>
        <el-button type="primary" @click="goLogin">返回登录页</el-button>
      </template>
    </el-result>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

function goLogin() {
  router.push({ name: 'Login' })
}
</script>

<style scoped>
.home-wrap {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  box-sizing: border-box;
  background: #f5f7fa;
}
</style>
