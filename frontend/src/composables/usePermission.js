// 权限判断 composable
import { ref } from 'vue';

// 假设用户权限列表存储在 localStorage 或全局 store
function getUserPermissions() {
  // 可替换为 pinia/vuex store
  const perms = localStorage.getItem('user_permissions');
  return perms ? JSON.parse(perms) : [];
}

export function usePermission() {
  const permissions = ref(getUserPermissions());

  function hasPermission(required) {
    if (!required) return true;
    if (Array.isArray(required)) {
      return required.some(p => permissions.value.includes(p));
    }
    return permissions.value.includes(required);
  }

  // 可用于刷新权限
  function setPermissions(perms) {
    permissions.value = perms;
    localStorage.setItem('user_permissions', JSON.stringify(perms));
  }

  return { permissions, hasPermission, setPermissions };
}

