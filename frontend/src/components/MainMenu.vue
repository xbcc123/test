<template>
  <a-menu mode="horizontal" class="main-menu blue-menu">
    <a-menu-item key="home">
      <router-link to="/">首页</router-link>
    </a-menu-item>
    <a-menu-item key="articles">
      <router-link to="/articles">资讯中心</router-link>
    </a-menu-item>
    <a-menu-item key="encyclopedia">
      <router-link to="/encyclopedia">宠物百科</router-link>
    </a-menu-item>
    <a-menu-item key="community">
      <router-link to="/community">社区互动</router-link>
    </a-menu-item>
    <a-menu-item key="service">
      <router-link to="/service">服务与资源</router-link>
    </a-menu-item>
    <a-menu-item key="member">
      <router-link to="/member">会员中心</router-link>
    </a-menu-item>
    <a-menu-item key="admin">
      <router-link to="/admin">后台管理</router-link>
    </a-menu-item>
  </a-menu>
</template>

<script setup>
// 菜单无业务逻辑，仅展示
</script>

<style scoped>
.main-menu {
  margin-bottom: 24px;
  border-radius: 14px 14px 0 0;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(33,150,243,0.08);
}
.blue-menu {
  background: linear-gradient(90deg, #b6d0f7 0%, #eaf6fb 100%) !important;
  border: none !important;
}
.main-menu :deep(.ant-menu-item-selected),
.main-menu :deep(.ant-menu-item-active) {
  background: linear-gradient(90deg, #2196f3 0%, #90caf9 100%) !important;
  color: #fff !important;
  box-shadow: 0 2px 8px 0 rgba(33,150,243,0.10);
}
.main-menu :deep(.ant-menu-item) {
  font-weight: bold;
  font-size: 16px;
  color: #1976d2;
  transition: background 0.2s, color 0.2s;
}
.main-menu :deep(.ant-menu-item):hover {
  background: #e3f2fd !important;
  color: #1565c0 !important;
}
</style>
