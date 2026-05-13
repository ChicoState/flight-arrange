<template>
  <div class="container mt-5" style="max-width: 500px;">
    <h2 class="mb-4">Manage Account</h2>

    <div class="mb-3">
      <label class="form-label">Username</label>
      <input v-model="username" class="form-control" placeholder="Username" />
    </div>

    <div class="mb-3">
      <label class="form-label">Email</label>
      <input v-model="email" class="form-control" placeholder="Email" />
    </div>

    <div class="mb-3">
      <label class="form-label">Phone Number</label>
      <input v-model="phoneNumber" class="form-control" placeholder="Phone Number" />
    </div>

    <div class="mb-3">
      <label class="form-label">New Password</label>
      <input v-model="password" type="password" class="form-control" placeholder="Leave blank to keep current" />
    </div>

    <button class="btn btn-primary w-100" @click="handleSave">Save Changes</button>
    <p v-if="message" class="mt-3 text-success">{{ message }}</p>
    <p v-if="error" class="mt-3 text-danger">{{ error }}</p>
  </div>
</template>

<script>
export default {
  data() {
    return {
      username: localStorage.getItem('username') || '',
      email: '',
      phoneNumber: '',
      password: '',
      message: '',
      error: ''
    }
  },
  methods: {
    async handleSave() {
      this.message = ''
      this.error = ''

      const body = {
        username: this.username,
        email: this.email,
        phoneNumber: this.phoneNumber,
        ...(this.password && { password: this.password })
      }

      const res = await fetch('/api/auth/update', {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
      })

      if (res.ok) {
        localStorage.setItem('username', this.username)
        this.message = 'Account updated successfully'
      } else {
        this.error = 'Failed to update account'
      }
    }
  },
  async mounted() {
    const username = localStorage.getItem('username')
    const res = await fetch(`/api/auth/user/${username}`)
    if (res.ok) {
        const data = await res.json()
        this.username = data.username
        this.email = data.email
        this.phoneNumber = data.phoneNumber || ''
    }
}
}
</script>