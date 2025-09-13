import { createRouter, createWebHashHistory } from 'vue-router'
import LoginView from '../components/LoginView.vue'
import HomeView from '../components/HomeView.vue'
import CatListView from '../components/CatListView.vue'
import ArticleView from '../components/ArticleView.vue'
import EncyclopediaView from '../components/EncyclopediaView.vue'
import CommunityView from '../components/CommunityView.vue'
import ServiceView from '../components/ServiceView.vue'
import MemberView from '../components/MemberView.vue'

const routes = [
  { path: '/login', component: LoginView },
  { path: '/', component: HomeView },
  { path: '/cats', component: CatListView },
  { path: '/articles', component: ArticleView },
  { path: '/encyclopedia', component: EncyclopediaView },
  { path: '/community', component: CommunityView },
  { path: '/service', component: ServiceView },
  { path: '/member', component: MemberView }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

export default router
