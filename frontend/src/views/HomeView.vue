<template>
  <div class="container">
    <h1>Welcome to Flight Wizard!</h1>
    <p class="subtitle">Search and manage your flights with ease.</p>
    <button @click="handleStart">Get Started</button>
    <p v-if="message" class="message">{{ message }}</p>
    <router-link to="/live-flights">
      <button>Go to Flight Search</button>
    </router-link>
    <pre v-if="flightInfo">{{ flightInfo }}</pre>
  </div>
</template>

<script>
export default {
  data() {
    return {
      message: null,
      flightInfo:null
    }
  },
  methods: {
    handleStart() {
      this.$router.push('/planTrip/beginTrip')
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
  gap: 1rem;
}

h1 { font-size: 3rem; }

.subtitle { color: #666; font-size: 1.2rem; }

button {
  padding: 0.5rem 1.5rem;
  font-size: 1rem;
  cursor: pointer;
  background-color: #4a90e2;
  color: white;
  border: none;
  border-radius: 6px;
}

.message { color: #4a90e2; }
</style>