<template>
  <button
    :class="[
      'btn',
      variant === 'primary' ? 'btn-primary' : 'bg-gray-200 text-gray-700 hover:bg-gray-300',
      loading ? 'opacity-60 cursor-not-allowed' : '',
      block ? 'w-full' : '',
      customClass
    ]"
    :disabled="disabled || loading"
    @click="$emit('click', $event)"
  >
    <span v-if="loading" class="animate-spin mr-2 inline-block"><svg class="w-4 h-4" fill="none" viewBox="0 0 24 24"><circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle><path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8z"></path></svg></span>
    <slot />
  </button>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue';
const props = defineProps({
  variant: { type: String, default: 'primary' },
  loading: { type: Boolean, default: false },
  disabled: { type: Boolean, default: false },
  block: { type: Boolean, default: false },
  customClass: { type: String, default: '' }
});
defineEmits(['click']);
</script>

