<template>
  <div class="container">
    <h1>Flight Search</h1>

    <!-- Search Form -->
    <div class="form">
      <input v-model="dep" placeholder="From (e.g. LAX)" />
      <input v-model="arr" placeholder="To (e.g. JFK)" />
      <input v-model="maxPrice" placeholder="Max budget (e.g. 500)" type="number" />
      <select v-model="maxLayovers">
        <option value="">Any layovers</option>
        <option value="0">Non-stop only</option>
        <option value="1">Max 1 layover</option>
        <option value="2">Max 2 layovers</option>
      </select>
      <button @click="getLiveFlights">Search Flights</button>
    </div>

    <!-- Results -->
    <ul v-if="flights.length">
      <li v-for="(flight, index) in flights" :key="index">
        <strong>{{ flight.airline.name }}</strong> {{ flight.flight.iata }} |
        {{ flight.departure.iata }} → {{ flight.arrival.iata }} |
        Departs: {{ flight.departure.scheduled }} |
        Status: {{ flight.flight_status }}
      </li>
    </ul>

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
      maxPrice: '',
      maxLayovers: '',
      flights: [],
      searched: false
    }
  },
  methods: {
    async getLiveFlights() {
      try {
        const response = await fetch(`/api/liveFlights?dep=${this.dep}&arr=${this.arr}`)
        const data = await response.json()
        this.searched = true

        // AviationStack wraps results in a "data" array
        if (data.data && Array.isArray(data.data)) {
          this.flights = data.data
        } else {
          this.flights = []
        }
      } catch (err) {
        console.error('Error fetching live flights:', err)
        this.flights = []
      }
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

ul {
  list-style: none;
  padding: 0;
  width: 100%;
  max-width: 700px;
}

li {
  padding: 0.75rem;
  border-bottom: 1px solid #eee;
  font-size: 1rem;
}

.back-btn {
  margin-top: 2rem;
}
</style>