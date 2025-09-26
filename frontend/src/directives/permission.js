// v-permission 指令
import { usePermission } from '../composables/usePermission';

export default {
  mounted(el, binding) {
    const { hasPermission } = usePermission();
    if (!hasPermission(binding.value)) {
      el.parentNode && el.parentNode.removeChild(el);
    }
  },
  updated(el, binding) {
    const { hasPermission } = usePermission();
    if (!hasPermission(binding.value)) {
      el.parentNode && el.parentNode.removeChild(el);
    }
  }
};

