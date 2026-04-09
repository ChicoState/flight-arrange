import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import LiveFlights from '../views/liveFlights.vue'
import FlightDetail from '../views/FlightDetail.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      component: HomeView
    },
    {
      path: '/live-flights',
      component: LiveFlights
    },
    {
      path: '/flight-detail',
      component: FlightDetail
    }
  ]
})

export default router