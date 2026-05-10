import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import BeginTrip from '../views/planTrip/BeginTrip.vue'
import LiveFlights from '../views/liveFlights.vue'
import FlightDetail from '../views/FlightDetail.vue'
import Login from '@/views/Login.vue'
import Account from '@/views/Account.vue'
import UserTrips from '@/views/UserTrips.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', component: HomeView },
    { path: '/planTrip/beginTrip', component: BeginTrip },
    { path: '/live-flights', component: LiveFlights },
    { path: '/flight-detail', component: FlightDetail },
    { path: '/login', component: Login },
    { path: '/account', component: Account },
    { path: '/user-trips', component: UserTrips }
  ]
})

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