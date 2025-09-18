<template>
  <div style="max-width: 800px; margin: 0 auto;">
    <h2 style="margin-bottom: 24px;">预约上门看病</h2>
    <ServiceOrderForm />
    <a-divider />
    <h3>我的预约</h3>
    <ServiceOrderList />
  </div>
</template>

<script setup>
import ServiceOrderForm from './ServiceOrderForm.vue';
import ServiceOrderList from './ServiceOrderList.vue';
</script>

<style scoped>
</style>

