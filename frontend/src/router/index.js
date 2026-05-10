import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import BeginTrip from '../views/planTrip/BeginTrip.vue'
import LiveFlights from '../views/liveFlights.vue'
import Login from '@/views/Login.vue'
import Account from '@/views/Account.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
  { path: '/', component: HomeView },
  { path: '/planTrip/beginTrip', component: BeginTrip },
  { path: '/live-flights', component: LiveFlights},
  { path: '/login', component: Login },
  { path: '/account', component: Account }
  ]
})

// forces users to be logged in  before they can reach any endpoitnts
router.beforeEach((to, from, next) => {
  const isLoggedIn = localStorage.getItem('username')
  const publicRoutes = ['/login']
  
  if (!isLoggedIn && !publicRoutes.includes(to.path)) {
    next('/login')
  } else {
    next()
  }
})

export default router