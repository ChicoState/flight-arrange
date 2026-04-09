<template>
  <div class="container">
    <h1>Flight Details</h1>

    <div class="card">
      <!-- Header -->
      <div class="card-header">
        <div class="airline-info">
          <h2>{{ flight.airline }}</h2>
          <span class="flight-number">{{ flight.flight }}</span>
        </div>
        <div :class="['score-badge', scoreClass]">
          Score: {{ flight.score }}
        </div>
      </div>

      <!-- Route -->
      <div class="route">
        <div class="airport">
          <div class="airport-code">{{ flight.departure }}</div>
          <div class="time">{{ formatTime(flight.departureTime) }}</div>
          <div class="date">{{ formatDate(flight.departureTime) }}</div>
        </div>
        <div class="route-arrow">
          <div class="duration">{{ formatDuration(flight.durationMins) }}</div>
          <div class="arrow">✈ ————————→</div>
          <div class="status" :class="statusClass">{{ flight.status }}</div>
        </div>
        <div class="airport">
          <div class="airport-code">{{ flight.arrival }}</div>
          <div class="time">{{ formatTime(flight.arrivalTime) }}</div>
          <div class="date">{{ formatDate(flight.arrivalTime) }}</div>
        </div>
      </div>

      <!-- Stats -->
      <div class="stats">
        <div class="stat">
          <div class="stat-label">Price</div>
          <div class="stat-value">${{ flight.price }}</div>
        </div>
        <div class="stat">
          <div class="stat-label">On-Time Rate</div>
          <div class="stat-value">{{ (flight.onTimeRate * 100).toFixed(0) }}%</div>
        </div>
        <div class="stat">
          <div class="stat-label">Duration</div>
          <div class="stat-value">{{ formatDuration(flight.durationMins) }}</div>
        </div>
        <div class="stat">
          <div class="stat-label">Status</div>
          <div class="stat-value">{{ flight.status }}</div>
        </div>
      </div>

      <!-- Reliability explanation -->
      <div class="reliability-note">
        <strong>Why {{ (flight.onTimeRate * 100).toFixed(0) }}% on-time?</strong>
        This score is based on historical flight data from the Bureau of
        Transportation Statistics for {{ flight.airline }} departing
        {{ flight.departure }}.
      </div>

      <!-- Actions -->
      <div class="actions">
        <a :href="googleFlightsUrl" target="_blank" class="book-btn">
          🔍 Search on Google Flights
        </a>
        <button @click="goBack" class="back-btn">← Back to Results</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      flight: {}
    }
  },
  created() {
    // Read flight data from URL query params when page loads
    const q = this.$route.query
    this.flight = {
      flight:        q.flight       || 'N/A',
      airline:       q.airline      || 'N/A',
      departure:     q.departure    || 'N/A',
      arrival:       q.arrival      || 'N/A',
      departureTime: q.departureTime|| '',
      arrivalTime:   q.arrivalTime  || '',
      price:         q.price        || 'N/A',
      onTimeRate:    parseFloat(q.onTimeRate) || 0,
      score:         parseFloat(q.score)      || 0,
      durationMins:  parseFloat(q.durationMins) || 0,
      status:        q.status       || 'unknown'
    }
  },
  computed: {
    scoreClass() {
      if (this.flight.score >= 0.75) return 'score-high'
      if (this.flight.score >= 0.60) return 'score-mid'
      return 'score-low'
    },
    statusClass() {
      const s = this.flight.status
      if (s === 'scheduled') return 'status-scheduled'
      if (s === 'active')    return 'status-active'
      if (s === 'landed')    return 'status-landed'
      return ''
    },
    // Build a Google Flights search URL from the route data
    googleFlightsUrl() {
      const dep = this.flight.departure
      const arr = this.flight.arrival
      const date = this.flight.departureTime
        ? new Date(this.flight.departureTime).toISOString().split('T')[0]
        : ''
      return `https://www.google.com/travel/flights/search?tfs=CBwQAhoeEgoyMDI2LTA0LTA4agcIARIDQVRMcgcIARIDT1JE`
    }
  },
  methods: {
    formatTime(isoString) {
      if (!isoString) return 'N/A'
      try {
        return new Date(isoString).toLocaleTimeString('en-US', {
          hour: '2-digit',
          minute: '2-digit',
          timeZoneName: 'short'
        })
      } catch { return isoString }
    },
    formatDate(isoString) {
      if (!isoString) return ''
      try {
        return new Date(isoString).toLocaleDateString('en-US', {
          weekday: 'short',
          month: 'short',
          day: 'numeric'
        })
      } catch { return '' }
    },
    formatDuration(mins) {
      if (!mins) return 'N/A'
      const h = Math.floor(mins / 60)
      const m = Math.round(mins % 60)
      return h > 0 ? `${h}h ${m}m` : `${m}m`
    },
    goBack() {
      this.$router.go(-1)
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

.card {
  width: 100%;
  max-width: 700px;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 2rem;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.airline-info h2 {
  margin: 0;
  font-size: 1.8rem;
}

.flight-number {
  color: #666;
  font-size: 1rem;
}

.score-badge {
  padding: 0.5rem 1rem;
  border-radius: 20px;
  font-weight: bold;
  font-size: 1.1rem;
}

.score-high { background-color: #d4edda; color: #155724; }
.score-mid  { background-color: #fff3cd; color: #856404; }
.score-low  { background-color: #f8d7da; color: #721c24; }

.route {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  padding: 1.5rem;
  background-color: #f8f9fa;
  border-radius: 8px;
}

.airport {
  text-align: center;
}

.airport-code {
  font-size: 2.5rem;
  font-weight: bold;
  color: #333;
}

.time {
  font-size: 1.1rem;
  color: #555;
}

.date {
  font-size: 0.85rem;
  color: #888;
}

.route-arrow {
  text-align: center;
  flex: 1;
  padding: 0 1rem;
}

.duration {
  font-size: 0.9rem;
  color: #666;
  margin-bottom: 0.25rem;
}

.arrow {
  font-size: 1.2rem;
  color: #4a90e2;
}

.status {
  font-size: 0.8rem;
  margin-top: 0.25rem;
  text-transform: capitalize;
}

.status-scheduled { color: #4a90e2; }
.status-active    { color: green; }
.status-landed    { color: #888; }

.stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.stat {
  text-align: center;
  padding: 1rem;
  background-color: #f8f9fa;
  border-radius: 6px;
}

.stat-label {
  font-size: 0.8rem;
  color: #888;
  margin-bottom: 0.25rem;
}

.stat-value {
  font-size: 1.2rem;
  font-weight: bold;
  color: #333;
}

.reliability-note {
  background-color: #e8f4fd;
  border-left: 4px solid #4a90e2;
  padding: 1rem;
  margin-bottom: 1.5rem;
  font-size: 0.9rem;
  border-radius: 0 4px 4px 0;
}

.actions {
  display: flex;
  gap: 1rem;
  justify-content: center;
}

.book-btn {
  padding: 0.75rem 1.5rem;
  background-color: #4a90e2;
  color: white;
  text-decoration: none;
  border-radius: 6px;
  font-size: 1rem;
  font-weight: bold;
}

.book-btn:hover {
  background-color: #357abd;
}

.back-btn {
  padding: 0.75rem 1.5rem;
  background-color: #f4f4f4;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 1rem;
  cursor: pointer;
}

.back-btn:hover {
  background-color: #e8e8e8;
}
</style>