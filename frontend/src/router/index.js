import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import BeginTrip from '../views/planTrip/BeginTrip.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
  { path: '/', component: HomeView },
  { path: '/planTrip/beginTrip', component: BeginTrip }
]
})

export default router