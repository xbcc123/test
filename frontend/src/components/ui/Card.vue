<template>
  <div :class="['card', customClass]">
    <slot />
  </div>
</template>

<script setup>
import { defineProps } from 'vue';
const props = defineProps({
  customClass: { type: String, default: '' }
});
</script>

<style scoped>
.card {
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-card);
  background: #fff;
  border: 1.5px solid var(--color-border);
  padding: 1.5rem 1.2rem;
  transition: box-shadow 0.18s, border 0.18s, background 0.18s;
}
.card:hover {
  box-shadow: var(--shadow-hover);
  border-color: var(--color-primary);
  background: #f6faff;
}
</style>
