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
import { getToken } from '../utils/auth'

const routes = [
  { path: '/login', component: LoginView },
  { path: '/', component: HomeView },
  { path: '/cats', component: CatListView },
  { path: '/articles', component: ArticleView },
  { path: '/articles/:id', component: ArticleDetailView },
  { path: '/encyclopedia', component: EncyclopediaView },
  { path: '/encyclopedia/:id', component: EncyclopediaDetailView },
  { path: '/community', component: CommunityView },
  { path: '/community/:id', component: CommunityDetailView },
  { path: '/service', component: ServiceView },
  { path: '/service/:id', component: ServiceDetailView },
  { path: '/admin', component: AdminView },
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const publicPages = ['/login']
  const authRequired = !publicPages.includes(to.path)
  const token = getToken()
  if (authRequired && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
