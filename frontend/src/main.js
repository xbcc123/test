import { createApp } from 'vue'
import App from './App.vue'
import router from './router/index.js'
import Antd from 'ant-design-vue'
import 'ant-design-vue/dist/reset.css';
import './assets/global.css';
import permission from './directives/permission.js';

const app = createApp(App)
app.use(router)
app.use(Antd)
app.directive('permission', permission);
app.mount('#app')
