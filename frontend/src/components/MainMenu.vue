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
    <a-menu-item key="pet-disease">
      <router-link to="/pet-disease">宠物疾病</router-link>
    </a-menu-item>
    <a-menu-item key="cat-shop">
      <router-link to="/cat-shop">宠物买卖</router-link>
    </a-menu-item>
    <a-menu-item key="system-monitor">
      <router-link to="/system-monitor">系统监控</router-link>
    </a-menu-item>
    <a-menu-item key="user-manage">
      <router-link to="/admin/user-manage">用户管理</router-link>
    </a-menu-item>
    <a-menu-item key="role-manage">
      <router-link to="/admin/role-manage">角色管理</router-link>
    </a-menu-item>
    <a-menu-item key="permission-manage">
      <router-link to="/admin/permission-manage">权限管理</router-link>
    </a-menu-item>
    <a-menu-item key="department-manage">
      <router-link to="/admin/department-manage">部门管理</router-link>
    </a-menu-item>
    <a-menu-item key="position-manage">
      <router-link to="/admin/position-manage">岗位管理</router-link>
    </a-menu-item>
    <a-menu-item key="announcements">
      <router-link to="/announcements">公告</router-link>
    </a-menu-item>
    <a-menu-item key="announcement-manage">
      <router-link to="/admin/announcement-manage">公告管理</router-link>
    </a-menu-item>
    <a-menu-item key="logout" style="float:right;margin-left:auto;">
      <a-button type="link" @click="logout" style="color:#d00;font-weight:bold;">退出登录</a-button>
    </a-menu-item>
  </a-menu>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { removeToken } from '../utils/auth'

const router = useRouter()
function logout() {
  // 清空所有 localStorage
  localStorage.clear()
  removeToken()
  router.push('/login')
}
</script>

<style scoped>
.main-menu {
  margin-bottom: 24px;
  border-radius: 14px 14px 0 0;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(33,150,243,0.08);
  font-size: 1.08em;
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
  border-radius: 10px 10px 0 0;
}
.main-menu :deep(.ant-menu-item) {
  font-weight: bold;
  font-size: 16px;
  color: #1976d2;
  transition: background 0.2s, color 0.2s, border-radius 0.2s;
  margin: 0 2px;
  border-radius: 10px 10px 0 0;
  padding: 0 18px;
}
.main-menu :deep(.ant-menu-item):hover {
  background: #e3f2fd !important;
  color: #1565c0 !important;
}
@media (max-width: 900px) {
  .main-menu {
    font-size: 0.98em;
    border-radius: 0;
    margin-bottom: 12px;
  }
  .main-menu :deep(.ant-menu-item) {
    padding: 0 10px;
    font-size: 15px;
  }
}
</style>
