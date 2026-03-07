<template>
  <div class="container">
    <h1>Welcome to Flight Arrange</h1>
    <button @click="getFlightInfo">Get DB Flights</button>
    <button @click="getLiveFlightInfo">Get Live Flights</button>
    <router-link to="/live-flights">
      <button>Go to Flight Search</button>
    </router-link>
    <p v-if="flightInfo">{{ flightInfo }}</p>
  </div>
</template>

<script>
export default {
  data() {
    return {
      flightInfo: null
    }
  },
  methods: {
    async getFlightInfo() {
      const response = await fetch('/api/flights')
      const data = await response.json()
      this.flightInfo = JSON.stringify(data, null, 2)  
    },

    async getLiveFlightInfo() {
      const response = await fetch('/api/liveFlights?dep=LAX&arr=JFK')
      const data = await response.json()
      this.flightInfo = JSON.stringify(data.data, null, 2)
    }
  }
}
</script> 

<style scoped>
.container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100vh;
}

h1 {
  font-size: 3rem;
}

button {
  margin-top: 1rem;
  padding: 0.5rem 1rem;
  font-size: 1rem;
  cursor: pointer;
}
</style>