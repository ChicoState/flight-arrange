<template>
  <div class="container">
    <h1>Welcome to Flight Wizard!</h1>
    <p class="subtitle">Search and manage your flights with ease.</p>
    <button @click="handleStart">Get Started</button>
    <p v-if="message" class="message">{{ message }}</p>
<<<<<<< HEAD
    <router-link to="/live-flights">
      <button>Go to Flight Search</button>
    </router-link>
=======
>>>>>>> merged_branch-5-11-26
    <pre v-if="flightInfo">{{ flightInfo }}</pre>
  </div>
</template>

<script>
export default {
<<<<<<< HEAD
  data() {
    return {
      message: null,
      flightInfo:null
    }
  },
  methods: {
    handleStart() {
      this.$router.push('/planTrip/beginTrip') // switches view
    },
  }
}
=======
  name: "HomeView",
  data() {
    return {
      message: null,
      flightInfo: null
    };
  },
  methods: {
    handleStart() {
      this.$router.push("/planTrip/beginTrip");
    }
  }
};
>>>>>>> merged_branch-5-11-26
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

<<<<<<< HEAD
h1 { font-size: 3rem; }

.subtitle { color: #666; font-size: 1.2rem; }
=======
h1 {
  font-size: 3rem;
}

.subtitle {
  color: #666;
  font-size: 1.2rem;
}
>>>>>>> merged_branch-5-11-26

button {
  padding: 0.5rem 1.5rem;
  font-size: 1rem;
  cursor: pointer;
  background-color: #4a90e2;
  color: white;
  border: none;
  border-radius: 6px;
}

<<<<<<< HEAD
.message { color: #4a90e2; }
=======
.message {
  color: #4a90e2;
}
>>>>>>> merged_branch-5-11-26
</style>