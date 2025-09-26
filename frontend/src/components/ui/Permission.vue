<template>
  <slot v-if="hasPermission" />
</template>
<script setup>
import { usePermission } from '../../composables/usePermission';
import { computed } from 'vue';
const props = defineProps({
  value: { type: [String, Array], required: true }
});
const { hasPermission: check } = usePermission();
const hasPermission = computed(() => check(props.value));
</script>

