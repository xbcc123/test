<template>
  <div v-html="safeHtml" />
</template>
<script setup>
import { computed, toRefs } from 'vue';
import MarkdownIt from 'markdown-it';
import DOMPurify from 'dompurify';

const props = defineProps({
  content: { type: String, required: true },
  options: { type: Object, default: () => ({}) }
});

const md = new MarkdownIt(props.options);
const safeHtml = computed(() => {
  const rawHtml = md.render(props.content || '');
  return DOMPurify.sanitize(rawHtml);
});
</script>

