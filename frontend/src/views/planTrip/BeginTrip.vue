<template>
  <div class="page">
    <div v-if="step === 1" class="step">
      <h1>Where do you want to go?</h1>

      <input v-model="starterLo" type="text" placeholder="From" />
      <input v-model="destination" type="text" placeholder="To" />

      <label class="toggle">
        <input type="checkbox" v-model="oneWay" />
        One-way trip
      </label>

      <div class="date-row">
        <input v-model="dateFrom" type="date" :min="today" />

        <input
          v-model="dateTo"
          type="date"
          :min="dateFrom || today"
          :disabled="oneWay"
          :class="{ disabled: oneWay }"
        />
      </div>

      <button :disabled="!canProceedStep1" @click="step = 2">
        Next →
      </button>
    </div>

    <div v-else-if="step === 2" class="step">
      <h1>Who's coming with you?</h1>

      <p class="hint">Optional — you can skip this step</p>

    <div v-for="(person, i) in companions" :key="i" class="companion-card">
      <div class="companion-main">
        <button class="remove-btn" @click="removeCompanion(i)">✕</button>
        <span class="companion-name">{{ person.name }}</span>
        <span class="sub">{{ person.email }} · {{ person.phone }}</span>
      </div>
    </div>

      <button class="add-btn" @click="showModal = true">
        + Add traveler
      </button>

      <div class="actions">
        <button class="back" @click="step = 1">← Back</button>

        <button
          :disabled="!hasCompanions"
          :class="{ disabledBtn: !hasCompanions }"
          @click="step = 3"
        >
          Next →
        </button>

        <button class="skip" @click="step = 3">
          Skip →
        </button>
      </div>
    </div>

    <div v-else-if="step === 3" class="step">
      <h1>What matters most to you?</h1>

      <div v-for="topic in topics" :key="topic.id" class="topic-row">
        <span class="topic-label">{{ topic.label }}</span>

        <div class="stars">
          <span
            v-for="n in 5"
            :key="n"
            class="star"
            :class="{ filled: n <= (topic.hover || topic.rating) }"
            @click="topic.rating = n"
            @mouseover="topic.hover = n"
            @mouseleave="topic.hover = 0"
          >
            ★
          </span>
        </div>
      </div>

      <div class="timing-card">
        <div class="timing-head">
          <h2>Departure timing</h2>
          <p class="hint">
            Use this to favor early, mid-day, late, or no preference.
          </p>
        </div>

        <div class="timing-grid">
          <label class="timing-field">
            <span>Outbound timing</span>
            <select v-model="outboundTiming">
              <option value="early">Early</option>
              <option value="midday">Mid-day</option>
              <option value="late">Late</option>
              <option value="nopreference">No preference</option>
            </select>
          </label>

          <label v-if="!oneWay" class="timing-field">
            <span>Return timing</span>
            <select v-model="returnTiming">
              <option value="early">Early</option>
              <option value="midday">Mid-day</option>
              <option value="late">Late</option>
              <option value="nopreference">No preference</option>
            </select>
          </label>
        </div>
      </div>

      <div class="actions">
        <button class="back" @click="step = 2">← Back</button>
        <button :disabled="!allRated" @click="goToFlights">
          Next →
        </button>
      </div>
    </div>

<div v-if="showModal" class="overlay" @click.self="closeModal">
  <div class="modal">
    <h2>Add a traveler</h2>

    <input v-model="form.name" type="text" placeholder="Name" />
    <small v-if="formErrors.name">{{ formErrors.name }}</small>

    <input v-model="form.phone" type="tel" placeholder="Phone" />
    <small v-if="formErrors.phone">{{ formErrors.phone }}</small>

    <input v-model="form.email" type="email" placeholder="Email" />
    <small v-if="formErrors.email">{{ formErrors.email }}</small>

    <div class="modal-actions">
      <button class="secondary" @click="closeModal">Cancel</button>
      <button @click="addCompanion">Add</button>
    </div>
  </div>
</div>

    

  </div>
</template>

<script>
export default {
  name: "BeginTrip",

  data() {
    return {
      step: 1,
      starterLo: "",
      destination: "",
      dateFrom: "",
      dateTo: "",
      oneWay: false,
      companions: [],
      showModal: false,
      modalError: "",
      outboundTiming: "early",
      returnTiming: "late",
      form: {
        name: "",
        phone: "",
        email: ""
      },
      formErrors: {
        name: "",
        phone: "",
        email: ""
      },
      topics: [
        { id: 1, label: "Price", rating: 0, hover: 0 },
        { id: 2, label: "On-Time Reliability", rating: 0, hover: 0 },
        { id: 3, label: "Flight Duration", rating: 0, hover: 0 },
        { id: 4, label: "Direct Flight", rating: 0, hover: 0 }
      ]
    };
  },
  methods: {
    addCompanion() {
      this.formErrors = { name: '', phone: '', email: '' }

      if (!this.form.name.trim()) this.formErrors.name = 'Name is required'
      if (!this.form.phone.trim()) this.formErrors.phone = 'Phone is required'
      if (!this.form.email.trim()) this.formErrors.email = 'Email is required'

      if (this.form.phone && !/^\d{10}$/.test(this.form.phone.trim())) {
        this.formErrors.phone = 'Use 10 digits' // expects standard 10 digit phone #
      }

      if (this.form.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.form.email.trim())) {
        this.formErrors.email = 'Invalid email' // expects something@email.somethingelse
      }

      if (this.formErrors.name || this.formErrors.phone || this.formErrors.email) return

      this.companions.push({ ...this.form })
      this.form = { name: '', phone: '', email: '' }
      this.formErrors = { name: '', phone: '', email: '' }
      this.showModal = false
    },

    closeModal() {
      this.form = { name: '', phone: '', email: '' }
      this.formErrors = { name: '', phone: '', email: '' }
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
  },
    handleNext() {
      if (!this.starterLo.trim()) {
        alert('Please enter a starting location.')
        return
      }

      if (!this.destination.trim()) {
        alert('Please enter a destination.')
        return
      }

      if (!this.dateFrom) {
        alert('Please enter a departure date.')
        return
      }

      if (!this.dateTo) {
        alert('Please enter a return date.')
        return
      }

      const today = new Date()
      today.setHours(0, 0, 0, 0)
      const fromDate = new Date(this.dateFrom + 'T00:00:00')
      const toDate = new Date(this.dateTo + 'T00:00:00')

      if (fromDate < today) {
        alert('Departure date cannot be in the past.')
        return
      }

      if (toDate <= fromDate) {
        alert('Return date must be after departure date.')
        return
      }

      this.step = 2
    },
  },
    
  computed: {
    allRated() {
      return this.topics.every(t => t.rating > 0)
    },
    parsedFlights() {
      if (!this.flightInfo) return []
      return Array.isArray(this.flightInfo) ? this.flightInfo : [this.flightInfo]
    },
    hasCompanions() {
      return this.companions.length > 0
    },
    today() {
      const d = new Date()
      const yyyy = d.getFullYear()
      const mm = String(d.getMonth() + 1).padStart(2, '0')
      const dd = String(d.getDate()).padStart(2, '0')
      return `${yyyy}-${mm}-${dd}`
    },
    canProceedStep1() {
      const base =
        this.starterLo.trim() !== '' &&
        this.destination.trim() !== '' &&
        this.dateFrom !== ''

      if (this.oneWay) return base
      return base && this.dateTo !== ''
    },
    async goToFlights() {
      if (!this.canProceedStep1) return

      await fetch('/api/trips', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          username: localStorage.getItem('username'),
          dep: this.starterLo,
          arr: this.destination,
          oneWay: this.oneWay,
          dateFrom: this.dateFrom,
          dateTo: this.oneWay ? '' : this.dateTo,
          outboundTiming: this.outboundTiming,
          returnTiming: this.returnTiming,
          ratings: this.topics,
          companions: this.companions
        })
      })

      this.$router.push({
        path: '/live-flights',
        query: {
          dep: this.starterLo,
          arr: this.destination,
          oneWay: this.oneWay,
          dateFrom: this.dateFrom,
          dateTo: this.oneWay ? '' : this.dateTo,
          outboundTiming: this.outboundTiming,
          returnTiming: this.returnTiming,
          ratings: JSON.stringify(this.topics)
        }
      })
},
  }
}
</script>

<style scoped>
.page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: Georgia, serif;
}

.step {
  width: 360px;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

input,
select {
  padding: 0.6rem;
  border: 1px solid #ccc;
  background: white;
}

button {
  padding: 0.6rem;
  background: black;
  color: white;
  border: none;
  cursor: pointer;
}

button:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.disabledBtn {
  background: #d1d5db !important;
  color: #6b7280 !important;
  cursor: not-allowed;
  border: 1px solid #ccc;
}

.actions {
  display: flex;
  gap: 0.5rem;
}

.back {
  background: white;
  color: black;
  border: 1px solid #ccc;
}

.skip {
  background: #eee;
  color: black;
  border: 1px solid #ccc;
}

.topic-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stars {
  display: flex;
  gap: 4px;
}

.star {
  cursor: pointer;
  color: #ccc;
  font-size: 20px;
}

.star.filled {
  color: black;
}

.timing-card {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  padding: 12px;
  border: 1px solid #e5e7eb;
  background: #fafafa;
}

.timing-head h2 {
  margin: 0 0 0.25rem;
  font-size: 1rem;
}

.timing-head .hint {
  margin: 0;
}

.timing-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0.75rem;
}

.timing-field {
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
  font-size: 14px;
}

.overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal {
  background: white;
  padding: 20px;
  width: 300px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.modal-error {
  margin: 0;
  font-size: 12px;
  color: #b91c1c;
}

.companion-card {
  padding: 8px;
  border: 1px solid #eee;
}

.companion-main {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.companion-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.companion-name {
  font-weight: 600;
}

.remove-btn {
  padding: 0.35rem 0.65rem;
  font-size: 12px;
  background: #ef4444;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.sub {
  font-size: 12px;
  color: #666;
}

.toggle {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}

.modal small {
  color: #777;
  font-size: 0.75rem;
  margin-top: -0.5rem;
}

.date-row {
  display: flex;
  gap: 0.5rem;
}

.date-row input:disabled,
.disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.hint {
  font-size: 12px;
  color: #666;
}

@media (max-width: 480px) {
  .step {
    width: 92vw;
  }

  .timing-grid {
    grid-template-columns: 1fr;
  }
}

.companion-card {
  position: relative;
  padding: 12px 14px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background: #fafafa;
}

.companion-main {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.companion-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.companion-name {
  font-weight: 600;
  font-size: 15px;
}

.remove-btn {
  position: absolute;
  top: 8px;
  right: 8px;
  padding: 0;
  width: 22px;
  height: 22px;
  font-size: 12px;
  background: #ef4444;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  line-height: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.sub {
  font-size: 12px;
  color: #666;
}

.step h1 {
  text-align: center;
}

.hint {
  text-align: center;
}

.actions {
  display: flex;
  gap: 0.5rem;
  justify-content: center;
}
</style>