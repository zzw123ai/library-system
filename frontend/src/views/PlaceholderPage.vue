<!-- 业务页占位：图书/读者/借阅等模块由组员接入后可替换为真实页面 -->
<template>
  <el-card shadow="never">
    <template #header>
      <span>{{ title }}</span>
    </template>
    <el-empty description="该模块页面开发中，敬请期待" />
  </el-card>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const title = computed(() => route.meta.title || '功能页')
</script>
