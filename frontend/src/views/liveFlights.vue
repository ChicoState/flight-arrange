<template>
  <div class="container">
    <h1>Flight Search</h1>

    <!-- Search Form -->
    <div class="form">
      <input v-model="dep" placeholder="From (e.g. ATL)" />
      <input v-model="arr" placeholder="To (e.g. ORD)" />
      <input v-model="date" type="date" />
      <input v-model="maxPrice" placeholder="Max budget (e.g. 500)" type="number" />
      <select v-model="maxLayovers">
        <option value="">Any layovers</option>
        <option value="0">Non-stop only</option>
        <option value="1">Max 1 layover</option>
        <option value="2">Max 2 layovers</option>
      </select>
      <button @click="getLiveFlights">Search Flights</button>
    </div>

    <!-- Results Table -->
    <table v-if="flights.length">
      <thead>
        <tr>
          <th>Flight</th>
          <th>Airline</th>
          <th>Route</th>
          <th>Departure</th>
          <th>Arrival</th>
          <th>Duration</th>
          <th>Layovers</th>
          <th>Price</th>
          <th>On-Time</th>
          <th>Score</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(flight, index) in filteredFlights" :key="index" class="flight-row">
          <td>{{ flight.flight }}</td>
          <td>{{ flight.airline }}</td>
          <td>{{ flight.departure }} → {{ flight.arrival }}</td>
          <td>{{ formatTime(flight.departureTime) }}</td>
          <td>{{ formatTime(flight.arrivalTime) }}</td>
          <td>{{ formatDuration(flight.durationMins) }}</td>
          <td>{{ flight.layovers === 0 ? 'Non-stop' : flight.layovers + ' stop(s)' }}</td>
          <td>${{ flight.price }}</td>
          <td>{{ (flight.onTimeRate * 100).toFixed(0) }}%</td>
          <td>
            <span :class="scoreClass(flight.score)">{{ flight.score }}</span>
          </td>
          <td>
            <button @click="selectFlight(flight)" class="select-btn">Select</button>
          </td>
        </tr>
      </tbody>
    </table>

    <p v-else-if="searched">No flights found for that route.</p>
    <p v-else>Enter your search above to find flights.</p>

    <!-- Back button -->
    <router-link to="/">
      <button class="back-btn">Back to Home</button>
    </router-link>
  </div>
</template>

<script>
export default {
  data() {
    return {
      dep: '',
      arr: '',
      date: '',
      maxPrice: '',
      maxLayovers: '',
      flights: [],
      searched: false
    }
  },
  computed: {
    // Filter flights by maxPrice if user entered one
    filteredFlights() {
      if (!this.maxPrice) return this.flights
      return this.flights.filter(f => f.price <= parseFloat(this.maxPrice))
    }
  },
  methods: {
    async getLiveFlights() {
      try {
        const response = await fetch(`/api/liveFlights?dep=${this.dep}&arr=${this.arr}&date=${this.date}`)
        const data = await response.json()
        this.searched = true
        this.flights = Array.isArray(data) ? data : []
      } catch (err) {
        console.error('Error fetching live flights:', err)
        this.flights = []
      }
    },

    // Navigate to detail page passing the selected flight as route state
    selectFlight(flight) {
      this.$router.push({
        path: '/flight-detail',
        query: {
          flight:        flight.flight,
          airline:       flight.airline,
          departure:     flight.departure,
          arrival:       flight.arrival,
          departureTime: flight.departureTime,
          arrivalTime:   flight.arrivalTime,
          price:         flight.price,
          onTimeRate:    flight.onTimeRate,
          score:         flight.score,
          durationMins:  flight.durationMins,
          layovers:      flight.layovers,
          status:        flight.status
        }
      })
    },

    // Format ISO datetime to readable time
    formatTime(isoString) {
      if (!isoString) return 'N/A'
      try {
        return new Date(isoString).toLocaleTimeString('en-US', {
          hour: '2-digit',
          minute: '2-digit',
          timeZoneName: 'short'
        })
      } catch {
        return isoString
      }
    },

    // Format minutes to Xh Ym
    formatDuration(mins) {
      if (!mins) return 'N/A'
      const h = Math.floor(mins / 60)
      const m = mins % 60
      return h > 0 ? `${h}h ${m}m` : `${m}m`
    },

    // Color code the score
    scoreClass(score) {
      if (score >= 0.75) return 'score-high'
      if (score >= 0.60) return 'score-mid'
      return 'score-low'
    }
  }
}
</script>

<style scoped>
.container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 2rem;
}

h1 {
  font-size: 2.5rem;
  margin-bottom: 1.5rem;
}

.form {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  width: 300px;
  margin-bottom: 2rem;
}

input, select {
  padding: 0.5rem;
  font-size: 1rem;
  border: 1px solid #ccc;
  border-radius: 4px;
}

button {
  padding: 0.5rem 1rem;
  font-size: 1rem;
  cursor: pointer;
}

table {
  width: 100%;
  max-width: 1000px;
  border-collapse: collapse;
  margin-bottom: 2rem;
}

th {
  background-color: #f4f4f4;
  padding: 0.75rem;
  text-align: left;
  border-bottom: 2px solid #ddd;
  font-size: 0.9rem;
}

td {
  padding: 0.75rem;
  border-bottom: 1px solid #eee;
  font-size: 0.95rem;
}

.flight-row:hover {
  background-color: #f9f9f9;
}

.select-btn {
  padding: 0.3rem 0.75rem;
  background-color: #4a90e2;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.select-btn:hover {
  background-color: #357abd;
}

.score-high { color: green; font-weight: bold; }
.score-mid  { color: orange; font-weight: bold; }
.score-low  { color: red; font-weight: bold; }

.back-btn {
  margin-top: 1rem;
}
</style>