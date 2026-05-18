<template>
  <router-view />
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
  background: linear-gradient(180deg, #bdd4ff, #9fc0ff);
  border-radius: 10px;
}

::-webkit-scrollbar-track {
  background: rgba(91, 141, 239, 0.1);
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
}

.el-button.is-link,
.el-button.is-text,
.el-button--primary.is-link,
.el-button--primary.is-text {
  background: transparent !important;
  box-shadow: none !important;
  border: none !important;
}
</style>
