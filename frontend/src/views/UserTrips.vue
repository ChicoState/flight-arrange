<template>
  <div class="container mt-4">
    <h2>My Trips</h2>
    <div v-if="trips.length === 0">
      <p>No trips saved yet.</p>
    </div>
    <div class="card mb-3" v-for="trip in trips" :key="trip.id">
      <div class="card-body">
        <h5 class="card-title">{{ trip.dep.toUpperCase() }} → {{ trip.arr.toUpperCase() }}</h5>
        <p>{{ trip.dateFrom }} <span v-if="!trip.oneWay">— {{ trip.dateTo }}</span></p>
        <span class="badge bg-secondary">{{ trip.oneWay ? 'One Way' : 'Round Trip' }}</span>

        <div class="mt-2">
          <strong>Travelers</strong>
          <div v-if="trip.companions.length">
            <div v-for="(c, i) in trip.companions" :key="i">
              {{ c.name }} · {{ c.email }} · {{ c.phone }}
            </div>
          </div>
          <div v-else>N/A</div>
        </div>

        <div v-if="trip.ratings.length" class="mt-2">
          <strong>Preferences</strong>
          <div v-for="(r, i) in trip.ratings" :key="i">
            {{ r.label }}: {{ '★'.repeat(Number(r.rating)) }}
          </div>
        </div>

        <button class="btn btn-dark mt-3" @click="goToFlights(trip)">View Flights</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'UserTrips',
  data() {
    return {
      trips: []
    }
  },
  async mounted() {
    const username = localStorage.getItem('username')
    const res = await fetch('/api/trips?username=' + username)
    const data = await res.json()

    console.log('username:', username)

    if (!Array.isArray(data)) {
      console.error('Expected array, got:', data)
      return
    }

    this.trips = data.map(trip => ({
      ...trip,
      rawDateFrom: trip.dateFrom,
      rawDateTo: trip.dateTo,
      dateFrom: this.formatDate(trip.dateFrom),
      dateTo: this.formatDate(trip.dateTo),
      companions: trip.companions ? this.parseJavaList(trip.companions) : [],
      ratings: trip.ratings ? this.parseJavaList(trip.ratings) : []
    }))
  },
  methods: {
    parseJavaList(str) {
      try {
        return str
          .slice(1, -1)
          .split('}, {')
          .map(item => {
            const obj = {}
            item.replace('{', '').replace('}', '').split(', ').forEach(pair => {
              const [key, ...rest] = pair.split('=')
              obj[key.trim()] = rest.join('=').trim()
            })
            return obj
          })
      } catch {
        return []
      }
    },
    formatDate(date) {
      if (!date) return ''
      const [year, month, day] = date.split('-')
      return new Date(year, month - 1, day).toLocaleDateString('en-US', {
        month: 'long',
        day: 'numeric',
        year: 'numeric'
      })
    },
    goToFlights(trip) {
      this.$router.push({
        path: '/live-flights',
        query: {
          dep: trip.dep,
          arr: trip.arr,
          oneWay: trip.oneWay,
          dateFrom: trip.rawDateFrom,
          dateTo: trip.rawDateTo,
          outboundTiming: trip.outboundTiming,
          returnTiming: trip.returnTiming,
          ratings: JSON.stringify(trip.ratings)
        }
      })
    }
  }
}
</script>