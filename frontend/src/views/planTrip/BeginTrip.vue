<template>
  <div class="page">

    <div v-if="step === 1" class="step">
      <h1>Where do you want to go?</h1>
      <input v-model="starterLo" type="text" placeholder="From" />
      <input v-model="destination" type="text" placeholder="To" />
      <div class="date-row">
        <input v-model="dateFrom" type="date" />
        <input v-model="dateTo" type="date" />
      </div>
      <button :disabled="!destination.trim()" @click="step = 2">Next →</button>
    </div>

    <div v-if="step === 2" class="step">
      <h1>Who's coming with you?</h1>

      <div v-for="(person, i) in companions" :key="i" class="companion-card">
        <span>{{ person.name }}</span>
        <span class="sub">{{ person.email }} · {{ person.phone }}</span>
      </div>

      <button class="add-btn" @click="showModal = true">+ Add traveler</button>

      <div class="actions">
        <button class="back" @click="step = 1">← Back</button>
        <button :disabled="!destination.trim()" @click="step = 3">Next →</button>
      </div>
    </div>

    <div v-if="step === 3" class="step">
      <h1>What matters most to you?</h1>

      <div v-for="topic in topics" :key="topic.id" class="topic-row">
        <span class="topic-label">{{ topic.label }}</span>
        <div class="stars">
          <span
            v-for="n in 5"
            :key="n"
            :class="['star', { filled: n <= (topic.hover || topic.rating) }]"
            @click="topic.rating = n"
            @mouseover="topic.hover = n"
            @mouseleave="topic.hover = 0"
          >★</span>
        </div>
      </div>

  <div class="actions">
    <button class="back" @click="step = 2">← Back</button>
    <button :disabled="!allRated || loading" @click="getFlightInfo">
      <span v-if="loading">Loading...</span>
      <span v-else>Next →</span>
    </button>
  </div>
</div>

<div v-if="step === 4" class="step centered">
  <div class="spinner"></div>
  <p>Getting your flights...</p>
</div>
<div v-if="step === 5" class="step step--wide">
  <h1>Your Flights</h1>
  <table>
    <thead>
      <tr>
        <th>Year</th>
        <th>Month</th>
        <th>Carrier</th>
        <th>Carrier Name</th>
        <th>Airport</th>
        <th>Airport Name</th>
        <th>Flights</th>
        <th>Delayed</th>
        <th>Cancelled</th>
        <th>On-Time Rate</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="flight in parsedFlights" :key="flight.id">
        <td>{{ flight.year }}</td>
        <td>{{ flight.month }}</td>
        <td>{{ flight.carrier }}</td>
        <td>{{ flight.carrierName }}</td>
        <td>{{ flight.airport }}</td>
        <td>{{ flight.airportName }}</td>
        <td>{{ flight.arrFlights }}</td>
        <td>{{ flight.arrDel15 }}</td>
        <td>{{ flight.arrCancelled }}</td>
        <td>{{ (flight.onTimeRate * 100).toFixed(1) }}%</td>
      </tr>
    </tbody>
  </table>
  <button @click="step = 1">Start Over</button>
</div>

    <div v-if="showModal" class="overlay" @click.self="showModal = false">
      <div class="modal">
        <h2>Add a traveler</h2>
        <input v-model="form.name" type="text" placeholder="Name" />
        <input v-model="form.phone" type="tel" placeholder="Phone" />
        <input v-model="form.email" type="email" placeholder="Email" />
        <div class="modal-actions">
          <button class="secondary" @click="showModal = false">Cancel</button>
          <button @click="addCompanion">Add</button>
        </div>
      </div>
    </div>

    

  </div>
</template>

<script>
export default {
  name: 'FlightForm',
  data() {
    return {
      step: 1,
      destination: '',
      companions: [],
      showModal: false,
      form: { name: '', phone: '', email: '' },
      dateFrom: '',
      dateTo: '',
      topics: [
        { id: 1, label: 'Baggage Allowance', rating: 0, hover: 0 },
        { id: 2, label: 'Wi-Fi Access',      rating: 0, hover: 0 },
        { id: 3, label: 'Customer Service',  rating: 0, hover: 0 },
        { id: 4, label: 'Price',             rating: 0, hover: 0 },
        { id: 5, label: 'Seat Selection',    rating: 0, hover: 0 },
      ],
      loading: false,
      flightInfo: null,
    }
  },
  methods: {
    addCompanion() {
      if (!this.form.name.trim()) return
      this.companions.push({ ...this.form })
      this.form = { name: '', phone: '', email: '' }
      this.showModal = false
    },
    async getFlightInfo() {
      this.loading = true
      this.step = 4;
      const response = await fetch('/api/flights')
      const data = await response.json()
      this.flightInfo = data
      this.loading = false
      this.step = 5;
    },

    async getLiveFlightInfo() {
    this.loading = true
    this.step = 4;
    const response = await fetch('/api/liveFlights?dep=LAX&arr=JFK')
    const data = await response.json()
    this.flightInfo = data
    this.loading = false
    this.step = 5;
  }
    
  },
  computed: {
  allRated() {
    return this.topics.every(t => t.rating > 0)
  },
  parsedFlights() {
    if (!this.flightInfo) return []
    return Array.isArray(this.flightInfo) ? this.flightInfo : [this.flightInfo]
  }
},
}
</script>

<style scoped>
.page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: Georgia, serif;
  background: #fff;
}

.step {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  width: 360px;
}

h1 {
  font-size: 1.8rem;
  font-weight: normal;
  margin: 0;
  white-space: nowrap;
}

input {
  padding: 0.6rem 1rem;
  font-size: 1rem;
  border: 1px solid #ccc;
  border-radius: 4px;
  outline: none;
}

input:focus { border-color: #111; }

button {
  padding: 0.6rem 1.2rem;
  font-size: 1rem;
  background: #111;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

button:disabled { background: #ccc; cursor: not-allowed; }

.add-btn {
  background: none;
  color: #111;
  border: 1px solid #ccc;
}

.actions {
  display: flex;
  gap: 0.75rem;
}

.actions button {
  flex: 1;
}

.back {
  background: #fff;
  color: #111;
  border: 1px solid #ccc;
}

.companion-card {
  display: flex;
  flex-direction: column;
  gap: 0.2rem;
  padding: 0.75rem;
  border: 1px solid #eee;
  border-radius: 6px;
}

.sub { font-size: 0.8rem; color: #888; }

.overlay {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.25);
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal {
  background: #fff;
  padding: 1.5rem;
  border-radius: 8px;
  width: 320px;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.date-row {
  display: flex;
  gap: 0.5rem;
}

.date-row input {
  flex: 1;
}

.topic-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.topic-label {
  font-size: 0.95rem;
  color: #111;
}

.stars {
  display: flex;
  gap: 0.25rem;
}

.star {
  font-size: 1.4rem;
  color: #ccc;
  cursor: pointer;
  transition: color 0.1s;
}

.star.filled {
  color: #111;
}

.centered {
  align-items: center;
}

.spinner {
  width: 32px;
  height: 32px;
  border: 2px solid #eee;
  border-top-color: #111;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}
.step table {
  width: 100%;
  border-collapse: collapse;
  font-size: 0.85rem;
}

.step th, .step td {
  padding: 0.6rem 1rem;
  border: 1px solid #eee;
  text-align: left;
}

.step th {
  background: #f9f9f9;
  font-weight: bold;
}

.step tr:hover {
  background: #fafafa;
}

.step--wide {
  width: 90vw;
  max-width: 900px;
  align-items: center;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.modal h2 { font-size: 1.1rem; font-weight: normal; margin: 0; }

.modal-actions { display: flex; justify-content: flex-end; gap: 0.5rem; }

.secondary { background: #fff; color: #111; border: 1px solid #ccc; }
</style>