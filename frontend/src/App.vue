<template>
  <router-view v-slot="{ Component, route }">
    <transition :name="route.path === '/login' ? 'login-route' : 'route-fade'" mode="out-in">
      <component :is="Component" :key="route.path" />
    </transition>
  </router-view>
</template>

<script setup>
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

:root {
  --bg-0: #f8fbff;
  --bg-1: #f2f7ff;
  --card: #ffffff;
  --text-0: #1f2937;
  --text-1: #4b5563;
  --text-2: #6b7280;
  --primary-0: #5b8def;
  --primary-1: #7aa6ff;
  --success-0: #34b8a5;
  --danger-0: #ee7f8f;
  --border-0: rgba(91, 141, 239, 0.18);
  --shadow-0: 0 10px 26px rgba(15, 23, 42, 0.08);
  --radius-2: 6px;
  --radius-3: 8px;
  --radius-4: 10px;
  --radius-5: 12px;
  --space-1: 4px;
  --space-2: 8px;
  --space-3: 12px;
  --space-4: 16px;
  --space-5: 20px;
  --space-6: 24px;
  --space-7: 28px;
  --font-display: 28px;
  --font-h1: 24px;
  --font-h2: 20px;
  --font-h3: 18px;
  --font-body: 14px;
  --font-small: 13px;
  --font-caption: 12px;
  --motion-fast: 0.18s;
  --motion-base: 0.24s;
  --motion-slow: 0.42s;
  --ease-smooth: cubic-bezier(0.22, 1, 0.36, 1);
  --ease-snappy: cubic-bezier(0.2, 0.8, 0.2, 1);
  --ease-bounce: cubic-bezier(0.34, 1.4, 0.64, 1);
}

body {
  font-family: 'Inter', 'PingFang SC', 'Microsoft YaHei', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
  color: var(--text-0);
  background:
    radial-gradient(circle at 8% 10%, rgba(91, 141, 239, 0.12), transparent 36%),
    radial-gradient(circle at 92% 0%, rgba(122, 166, 255, 0.12), transparent 30%),
    linear-gradient(180deg, var(--bg-1), var(--bg-0));
  min-height: 100vh;
  line-height: 1.5;
}

#app {
  min-height: 100vh;
}

button,
input,
select {
  font-family: inherit;
}

::-webkit-scrollbar {
  width: 10px;
  height: 10px;
}

::-webkit-scrollbar-thumb {
  background: rgba(91, 141, 239, 0.28);
  border-radius: 6px;
}

::-webkit-scrollbar-track {
  background: rgba(91, 141, 239, 0.06);
}

/* 登录页路由：仅淡入，避免与登录卡片动画叠加卡顿 */
.login-route-enter-active,
.login-route-leave-active {
  transition: opacity 0.22s ease;
}

.login-route-enter-from,
.login-route-leave-to {
  opacity: 0;
}

.el-card {
  border: 1px solid var(--border-0) !important;
  box-shadow: var(--shadow-0) !important;
  border-radius: 12px !important;
}

.el-button--primary {
  background: linear-gradient(120deg, var(--primary-0), var(--primary-1)) !important;
  border: none !important;
}

.el-button {
  border-radius: var(--radius-3) !important;
  font-size: var(--font-small) !important;
  font-weight: 600 !important;
  transition:
    transform var(--motion-fast) var(--ease-smooth),
    box-shadow var(--motion-fast) var(--ease-smooth),
    filter var(--motion-fast) var(--ease-smooth) !important;
}

.el-button.is-link,
.el-button.is-text,
.el-button--primary.is-link,
.el-button--primary.is-text {
  background: transparent !important;
  box-shadow: none !important;
  border: none !important;
}

.el-button:not(.is-disabled):hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(91, 141, 239, 0.22);
}

.el-button:not(.is-disabled):active {
  transform: translateY(0) scale(0.97);
  transition-duration: 0.1s;
}

:where(
  .add-btn,
  .remove-dup-btn,
  .edit-btn,
  .delete-btn,
  .return-btn,
  .submit-btn,
  .cancel-btn,
  .logout-btn
) {
  transition:
    transform var(--motion-fast) var(--ease-smooth),
    box-shadow var(--motion-fast) var(--ease-smooth),
    background-color var(--motion-fast) var(--ease-smooth),
    filter var(--motion-fast) var(--ease-smooth);
}

:where(
  .add-btn,
  .remove-dup-btn,
  .edit-btn,
  .delete-btn,
  .return-btn,
  .submit-btn,
  .logout-btn
):hover {
  transform: translateY(-2px);
  filter: saturate(1.05);
  box-shadow: 0 8px 18px rgba(91, 141, 239, 0.2);
}

:where(
  .add-btn,
  .remove-dup-btn,
  .edit-btn,
  .delete-btn,
  .return-btn,
  .submit-btn,
  .cancel-btn,
  .logout-btn
):active {
  transform: translateY(0) scale(0.98);
}

:where(
  .add-btn,
  .remove-dup-btn,
  .edit-btn,
  .delete-btn,
  .return-btn,
  .submit-btn,
  .cancel-btn,
  .logout-btn,
  .el-button
):focus-visible {
  outline: 2px solid rgba(91, 141, 239, 0.42);
  outline-offset: 2px;
}
</style>
