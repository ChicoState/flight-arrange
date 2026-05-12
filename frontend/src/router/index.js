import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import BeginTrip from '../views/planTrip/BeginTrip.vue'
import LiveFlights from '../views/liveFlights.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
  { path: '/', component: HomeView },
  { path: '/planTrip/beginTrip', component: BeginTrip },
  { path: '/live-flights', component: LiveFlights}
  ]
})

export default router