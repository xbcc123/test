import { ref, computed, watch } from 'vue'

// 简易主题管理（light / dark），基于 data-theme 属性 + CSS 变量
const currentTheme = ref('light')

function loadStoredTheme() {
  const saved = localStorage.getItem('app-theme')
  if (saved === 'light' || saved === 'dark') {
    currentTheme.value = saved
  } else if (window.matchMedia('(prefers-color-scheme: dark)').matches) {
    currentTheme.value = 'dark'
  }
  applyTheme()
}

function applyTheme() {
  const root = document.documentElement
  root.dataset.theme = currentTheme.value
}

function toggleTheme() {
  currentTheme.value = currentTheme.value === 'light' ? 'dark' : 'light'
}

watch(currentTheme, (val) => {
  localStorage.setItem('app-theme', val)
  applyTheme()
})

loadStoredTheme()

export function useTheme() {
  return { theme: currentTheme, toggleTheme, isDark: computed(() => currentTheme.value === 'dark') }
}

