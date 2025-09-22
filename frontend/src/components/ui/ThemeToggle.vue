<template>
  <a-tooltip :title="isDark ? '切换为亮色模式' : '切换为暗色模式'">
    <a-button shape="circle" size="small" @click="toggleTheme" :class="['theme-toggle', isDark ? 'dark' : 'light']">
      <template #icon>
        <span v-if="isDark">🌙</span>
        <span v-else>☀️</span>
      </template>
    </a-button>
  </a-tooltip>
</template>
<script setup>
import { useTheme } from '@/composables/theme'
const { toggleTheme, isDark } = useTheme()
</script>
<style scoped>
.theme-toggle {
  display:flex;align-items:center;justify-content:center;
  background:linear-gradient(135deg,var(--color-primary),var(--color-accent));
  color:#fff;border:none;box-shadow:0 2px 8px rgba(0,0,0,.15);transition:.3s;
}
.theme-toggle.light { background:linear-gradient(135deg,#42a5f5,#ffb74d); }
.theme-toggle.dark { background:linear-gradient(135deg,#546e7a,#ffb74d); }
.theme-toggle:hover { filter:brightness(1.08); transform:translateY(-2px); }
.theme-toggle:active { transform:translateY(0); }
</style>

