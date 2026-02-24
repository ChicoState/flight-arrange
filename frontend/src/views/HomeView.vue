<template>
  <div class="container">
    <h1>Welcome to Flight Arrange</h1>
    <button @click="getFlightInfo">Retrieve Flight Info</button>
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
      this.flightInfo = data.flightInfo
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