<template>
  <nav class="navbar navbar-dark bg-primary px-4" v-if="$route.path !== '/login'">
    <span class="navbar-brand" @click="$router.push('/')" style="cursor:pointer">Flight Wizard</span>
    <div v-if="username" class="dropdown" ref="dropdown">
      <div class="d-flex align-items-center gap-2" @click="toggleDropdown" style="cursor:pointer">
        <i class="bi bi-person-circle fs-4 text-white"></i>
        <span class="text-white">{{ username }}</span>
        <i class="bi bi-chevron-down text-white" style="font-size:0.75rem"></i>
      </div>
  <ul class="dropdown-menu dropdown-menu-end show" v-if="dropdownOpen">
    <li><a class="dropdown-item" @click="goToAccount">Account Details</a></li>
    <li><hr class="dropdown-divider"></li>
    <li><a class="dropdown-item" @click="$router.push('/user-trips')">My Trips</a></li>
    <li><hr class="dropdown-divider"></li>
    <li><a class="dropdown-item text-danger" @click="logout">Logout</a></li>
  </ul>
    </div>
    <div v-else>
      <button class="btn btn-outline-light btn-sm" @click="$router.push('/login')">Login</button>
    </div>
  </nav>
  <RouterView />
</template>

<script>
export default {
  data() {
    return {
      username: '',
      dropdownOpen: false
    }
  },
  mounted() {
    this.username = localStorage.getItem('username') || ''
    document.addEventListener('click', this.handleOutsideClick)
  },
  beforeUnmount() {
    document.removeEventListener('click', this.handleOutsideClick)
  },
  watch: {
    $route() {
      this.username = localStorage.getItem('username') || ''
      this.dropdownOpen = false
    }
  },
  methods: {
    toggleDropdown() {
      this.dropdownOpen = !this.dropdownOpen
    },
    handleOutsideClick(e) {
      if (this.$refs.dropdown && !this.$refs.dropdown.contains(e.target)) {
        this.dropdownOpen = false
      }
    },
    goToAccount() {
      this.dropdownOpen = false
      this.$router.push('/account')
    },
    logout() {
      localStorage.clear()
      this.username = ''
      this.dropdownOpen = false
      this.$router.push('/login')
    }
  }
}
</script>

  <style>
  .navbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0.75rem 2rem;
    background-color: #4a90e2;
    color: white;
  }

  .brand {
    font-size: 1.2rem;
    font-weight: bold;
  }

  .user-icon {
    font-size: 1rem;
    cursor: pointer;
  }

  .dropdown {
  position: relative;
  }

  .dropdown-menu {
    position: absolute;
    right: 0;
    top: 100%;
    z-index: 1000;
    min-width: 160px;
  }
  
</style>