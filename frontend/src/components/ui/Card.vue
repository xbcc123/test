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

