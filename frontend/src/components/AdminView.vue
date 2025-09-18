<template>
  <div class="admin-container">
    <h2 class="admin-title">后台管理</h2>
    <div class="tab-btns">
      <button v-for="tab in tabs" :key="tab.key" :class="{active: currentTab===tab.key}" @click="currentTab=tab.key">{{tab.label}}</button>
    </div>
    <div v-show="currentTab==='cat'">
      <CatAdminPanel />
    </div>
    <div v-show="currentTab==='article'">
      <ArticleAdminPanel />
    </div>
    <div v-show="currentTab==='encyclopedia'">
      <EncyclopediaAdminPanel />
    </div>
    <div v-show="currentTab==='community'">
      <CommunityAdminPanel />
    </div>
    <div v-show="currentTab==='service'">
      <ServiceAdminPanel />
    </div>
    <div v-show="currentTab==='member'">
      <MemberAdminPanel />
    </div>
    <div v-show="currentTab==='petsale'">
      <PetSaleAdminPanel />
    </div>
    <div v-show="currentTab==='petdisease'">
      <PetDiseaseAdminPanel />
    </div>
    <!-- 其他管理模块可扩展 -->
  </div>
</template>

<script setup>
import { ref } from 'vue'
import CatAdminPanel from './admin/CatAdminPanel.vue'
import ArticleAdminPanel from './admin/ArticleAdminPanel.vue'
import EncyclopediaAdminPanel from './admin/EncyclopediaAdminPanel.vue'
import CommunityAdminPanel from './admin/CommunityAdminPanel.vue'
import ServiceAdminPanel from './admin/ServiceAdminPanel.vue'
import MemberAdminPanel from './admin/MemberAdminPanel.vue'
import PetSaleAdminPanel from './admin/PetSaleAdminPanel.vue'
import PetDiseaseAdminPanel from './admin/PetDiseaseAdminPanel.vue'

const tabs = [
  { key: 'cat', label: '猫管理' },
  { key: 'article', label: '文章管理' },
  { key: 'encyclopedia', label: '宠物百科管理' },
  { key: 'community', label: '社区互动管理' },
  { key: 'service', label: '服务与资源管理' },
  { key: 'member', label: '会员中心管理' },
  { key: 'petsale', label: '宠物出售管理' },
  { key: 'petdisease', label: '宠物疾病管理' },
]
const currentTab = ref('cat')
</script>

<style scoped>
.admin-container {
  max-width: 900px;
  margin: 40px auto;
  background: #fff;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 8px #0001;
}
.admin-title {
  text-align: center;
  font-size: 26px;
  font-weight: 700;
  margin-bottom: 18px;
}
.tab-btns {
  margin-bottom: 20px;
}
.tab-btns button {
  margin-right: 8px;
  padding: 6px 18px;
  border: none;
  border-radius: 4px;
  background: #e3f2fd;
  color: #1976d2;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s, color 0.2s;
}
.tab-btns button.active, .tab-btns button:hover {
  background: #1976d2;
  color: #fff;
}
</style>
