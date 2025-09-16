<template>
  <div v-if="visible" class="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50 transition-opacity duration-300" @click.self="$emit('close')">
    <div :class="['bg-white rounded-2xl shadow-2xl p-8 relative w-full max-w-lg', customClass]">
      <button class="absolute top-4 right-4 text-gray-400 hover:text-gray-600" @click="$emit('close')">
        <span class="iconify text-2xl" data-icon="mdi:close"></span>
      </button>
      <slot />
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue';
const props = defineProps({
  visible: { type: Boolean, default: false },
  customClass: { type: String, default: '' }
});
defineEmits(['close']);
</script>

