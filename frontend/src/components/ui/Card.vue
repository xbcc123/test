<template>
  <div :class="['card', 'rounded-xl shadow-lg bg-white p-6 transition-shadow duration-150 hover:shadow-2xl', customClass]">
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
  transition: box-shadow 0.15s;
}
</style>
