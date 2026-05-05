<!-- 工作台：登录后默认页，按角色展示提示 -->
<template>
  <div class="dashboard">
    <el-card shadow="never">
      <template #header>
        <span>工作台</span>
      </template>
      <p class="greet">
        你好，<strong>{{ displayName }}</strong>
        <el-tag :type="isAdmin ? 'danger' : 'info'" size="small" class="role-tag">
          {{ isAdmin ? '管理员' : '读者' }}
        </el-tag>
      </p>
      <p class="hint">请通过左侧菜单进入各功能模块（部分页面由组员后续接入业务）。</p>
    </el-card>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { getUserInfo, isAdminRole } from '../utils/auth.js'

const user = getUserInfo()
const displayName = computed(() => user?.username || '用户')
const isAdmin = computed(() => isAdminRole(user?.role ?? ''))
</script>

<style scoped>
.dashboard {
  max-width: 720px;
}
.greet {
  margin: 0 0 12px;
  font-size: 16px;
}
.role-tag {
  margin-left: 10px;
  vertical-align: middle;
}
.hint {
  margin: 0;
  color: #909399;
  font-size: 14px;
}
</style>
