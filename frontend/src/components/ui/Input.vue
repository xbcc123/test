<template>
  <div>
    <input
      :type="type"
      :placeholder="placeholder"
      :value="modelValue"
      :disabled="disabled"
      :class="[
        'w-full',
        'rounded-lg border px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400 focus:border-blue-400 transition-all duration-150',
        error ? 'border-red-400 focus:ring-red-300 focus:border-red-400' : 'border-gray-300',
        disabled ? 'bg-gray-100 cursor-not-allowed' : 'bg-white',
        customClass
      ]"
      @input="$emit('update:modelValue', $event.target.value)"
      @blur="$emit('blur', $event)"
      @focus="$emit('focus', $event)"
    />
    <div v-if="error" class="text-red-500 text-xs mt-1">{{ error }}</div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue';
const props = defineProps({
  modelValue: [String, Number],
  type: { type: String, default: 'text' },
  placeholder: { type: String, default: '' },
  error: { type: String, default: '' },
  disabled: { type: Boolean, default: false },
  customClass: { type: String, default: '' }
});
defineEmits(['update:modelValue', 'blur', 'focus']);
</script>

<style scoped>
input {
  border-radius: var(--radius-md);
  font-size: 1rem;
  box-shadow: none;
  border: 1.5px solid var(--color-border);
  transition: border 0.18s, box-shadow 0.18s;
}
input:focus {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 2px rgba(33,150,243,0.10);
}
input:disabled {
  background: #f3f4f6;
  color: #aaa;
}
</style>
