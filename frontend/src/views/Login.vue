<template>
  <div class="container">
    <h2>{{ isLogin ? 'Welcome To Flight Wizard!' : 'Create Account' }}</h2>

    <input v-model="username" placeholder="Username" />
    <input v-model="email" v-if="!isLogin" placeholder="Email" />
    <input v-model="phoneNumber" v-if="!isLogin" placeholder="Phone Number" />
    <input v-model="password" type="password" placeholder="Password" />
    <input v-model="confirmPassword" v-if="!isLogin" type="password" placeholder="Confirm Password" />

    <button @click="handleSubmit">{{ isLogin ? 'Login' : 'Sign Up' }}</button>

    <p class="toggle" @click="isLogin = !isLogin">
      {{ isLogin ? "Don't have an account? Sign up" : "Already have an account? Log in" }}
    </p>

    <p v-if="error" class="error">{{ error }}</p>
  </div>
</template>

<script>
export default {
  data() {
    return {
      isLogin: true,
      username: '',
      email: '',
      phoneNumber: '',
      password: '',
      confirmPassword: '',
      error: ''
    }
  },
  methods: {
    async handleSubmit() {
      this.error = ''

      // input validation
      if (!this.username || this.username.length < 3) {
        this.error = 'Username must be at least 3 characters'
        return false
      }

      if (!this.isLogin) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
        if (!emailRegex.test(this.email)) {
          this.error = 'Enter a valid email address'
          return false
        }

        const phoneRegex = /^\d{10}$/
        if (this.phoneNumber && !phoneRegex.test(this.phoneNumber.replace(/\D/g, ''))) {
          this.error = 'Enter a valid 10-digit phone number'
          return false
        }
      }
      if (this.password.length < 6) {
        this.error = 'Password must be at least 6 characters'
        return false
      }
      if (!this.isLogin && this.password !== this.confirmPassword) {
        this.error = 'Passwords do not match'
        return
      }

      // decide endpoint based on whether user is loggin in or resgistering
      const endpoint = this.isLogin ? '/api/auth/login' : '/api/auth/register'
      const body = this.isLogin
        ? { username: this.username, password: this.password }
        : { username: this.username, password: this.password, email: this.email, phoneNumber: this.phoneNumber }

      const res = await fetch(endpoint, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
      })

      // if it saves then we start planning the trip

      console.log(res.status)
      if (res.ok) {
          localStorage.setItem('username', this.username)
          this.$router.push('/')
        } else {
          this.error = this.isLogin ? 'Invalid credentials' : 'Registration failed'
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
  justify-content: center;
  height: 100vh;
  gap: 1rem;
}

input {
  padding: 0.5rem 1rem;
  font-size: 1rem;
  border: 1px solid #ccc;
  border-radius: 6px;
  width: 260px;
}

button {
  padding: 0.5rem 1.5rem;
  font-size: 1rem;
  background-color: #4a90e2;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  width: 260px;
}

.toggle {
  color: #4a90e2;
  cursor: pointer;
  font-size: 0.9rem;
}

.error {
  color: red;
  font-size: 0.9rem;
}
</style>