import { createRouter, createWebHashHistory } from 'vue-router'
import LoginView from '../components/LoginView.vue'
import HomeView from '../components/HomeView.vue'
import CatListView from '../components/CatListView.vue'
import ArticleView from '../components/ArticleView.vue'
import ArticleDetailView from '../components/ArticleDetailView.vue'
import EncyclopediaView from '../components/EncyclopediaView.vue'
import CommunityView from '../components/CommunityView.vue'
import ServiceView from '../components/ServiceView.vue'
import MemberView from '../components/MemberView.vue'
import AdminView from '../components/AdminView.vue'
import ServiceDetailView from '../components/ServiceDetailView.vue'
import CommunityDetailView from '../components/CommunityDetailView.vue'
import EncyclopediaDetailView from '../components/EncyclopediaDetailView.vue'
import ServiceOrderView from '../components/ServiceOrderView.vue'
import PetDiseaseView from '../components/PetDiseaseView.vue'
import CatShopView from '../components/CatShopView.vue'
import { getToken } from '../utils/auth'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
NProgress.configure({ showSpinner: false, speed: 380, trickleSpeed: 120 })

const routes = [
  { path: '/login', component: LoginView, meta: { title: '登录' } },
  { path: '/', component: HomeView, meta: { title: '首页' } },
  { path: '/cats', component: CatListView },
  { path: '/articles', component: ArticleView },
  { path: '/articles/:id', component: ArticleDetailView },
  { path: '/encyclopedia', component: EncyclopediaView },
  { path: '/encyclopedia/:id', component: EncyclopediaDetailView },
  { path: '/community', component: CommunityView },
  { path: '/community/:id', component: CommunityDetailView },
  { path: '/service', component: ServiceView },
  { path: '/service/:id', component: ServiceDetailView },
  { path: '/member', component: MemberView },
  { path: '/admin', component: AdminView },
  { path: '/admin/user-manage', component: () => import('../components/admin/UserManageView.vue') },
  { path: '/admin/role-manage', component: () => import('../components/admin/RoleManageView.vue') },
  { path: '/admin/permission-manage', component: () => import('../components/admin/PermissionManageView.vue') },
  { path: '/admin/department-manage', component: () => import('../components/admin/DepartmentManageView.vue') },
  { path: '/admin/position-manage', component: () => import('../components/admin/PositionManageView.vue') },
  { path: '/service-order', component: ServiceOrderView },
  { path: '/pet-disease', component: PetDiseaseView },
  { path: '/cat-shop', component: CatShopView },
  { path: '/system-monitor', component: () => import('../components/admin/SystemMonitor.vue') },
  { path: '/announcements', component: () => import('../components/AnnouncementListView.vue'), meta:{ title:'公告'} },
  { path: '/admin/announcement-manage', component: () => import('../components/admin/AnnouncementManageView.vue') },
  { path: '/chat', component: () => import('../components/ChatView.vue'), meta:{ title:'聊天'} },
  { path: '/contacts', component: () => import('../components/ContactListView.vue'), meta:{ title:'通讯录'} },
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  NProgress.start()
  const publicPages = ['/login']
  const authRequired = !publicPages.includes(to.path)
  const token = getToken()
  if (authRequired && !token) {
    next('/login')
  } else {
    next()
  }
})
router.afterEach((to) => {
  if (to.meta && to.meta.title) {
    document.title = to.meta.title + ' - 宠物综合平台'
  }
  NProgress.done()
})

export default router
