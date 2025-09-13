<template>
  <div class="main-layout">
    <MainMenu />
    <div class="main-content blue-glass-bg">
      <slot />
    </div>
  </div>
</template>

<script setup>
import MainMenu from './MainMenu.vue'
</script>

<style scoped>
.main-layout {
  min-height: 100vh;
  background: linear-gradient(135deg, #eaf6fb 0%, #c9e7fa 100%);
}
.main-content {
  max-width: 900px;
  margin: 0 auto;
  padding: 32px 16px 16px 16px;
}
.blue-glass-bg {
  background: rgba(255,255,255,0.55);
  border-radius: 18px;
  box-shadow: 0 8px 32px 0 rgba(33,150,243,0.10), 0 1.5px 8px 0 rgba(33,150,243,0.08);
  backdrop-filter: blur(12px);
  border: 1.5px solid rgba(33,150,243,0.12);
}
</style>
