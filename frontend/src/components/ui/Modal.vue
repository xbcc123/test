<template>
  <transition name="modal-fade">
    <div v-if="visible" class="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50 transition-opacity duration-300" @click.self="$emit('close')">
      <div :class="['modal-card', customClass]">
        <button class="modal-close-btn" @click="$emit('close')" aria-label="关闭弹窗">
          <svg class="w-6 h-6" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12"/></svg>
        </button>
        <slot />
      </div>
    </div>
  </transition>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue';
const props = defineProps({
  visible: { type: Boolean, default: false },
  customClass: { type: String, default: '' }
});
defineEmits(['close']);
</script>

<style scoped>
.modal-card {
  background: #fff;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-hover);
  padding: 2.5rem 2rem 2rem 2rem;
  position: relative;
  width: 100%;
  max-width: 480px;
  border: 1.5px solid var(--color-border);
  transition: box-shadow 0.18s, border 0.18s, background 0.18s;
}
.modal-close-btn {
  position: absolute;
  top: 1.1rem;
  right: 1.1rem;
  background: transparent;
  border: none;
  color: #888;
  border-radius: 50%;
  padding: 0.3rem;
  transition: background 0.18s, color 0.18s;
  cursor: pointer;
}
.modal-close-btn:hover {
  background: #f3f4f6;
  color: var(--color-primary);
}
.modal-fade-enter-active, .modal-fade-leave-active {
  transition: opacity 0.25s, transform 0.25s;
}
.modal-fade-enter-from, .modal-fade-leave-to {
  opacity: 0;
  transform: scale(0.95);
}
</style>
