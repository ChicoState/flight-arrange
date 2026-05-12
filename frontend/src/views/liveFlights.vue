<template>
<<<<<<< HEAD
  <div class="container">
    <h1>Flight Search</h1>

    <!-- Search Form -->
    <div class="form">
      <input v-model="dep" placeholder="From (e.g. LAX)" />
      <input v-model="arr" placeholder="To (e.g. JFK)" />
      <input v-model="maxPrice" placeholder="Max budget (e.g. 500)" type="number" />
      <select v-model="maxLayovers">
        <option value="">Any layovers</option>
        <option value="0">Non-stop only</option>
        <option value="1">Max 1 layover</option>
        <option value="2">Max 2 layovers</option>
      </select>
      <button @click="getLiveFlights">Search Flights</button>
    </div>

    <!-- Results -->
    <ul v-if="flights.length">
      <li v-for="(flight, index) in flights" :key="index">
        <strong>{{ flight.airline }}</strong> {{ flight.flight }} |
        {{ flight.departure }} → {{ flight.arrival }} |
        Departs: {{ flight.departureTime }} |
        Price: ${{ flight.price }} |
        On-Time: {{ (flight.onTimeRate * 100).toFixed(0) }}% |
        Score: {{ flight.score }}
      </li>
    </ul>

    <p v-else-if="searched">No flights found for that route.</p>
    <p v-else>Enter your search above to find flights.</p>

    <!-- Back button -->
    <router-link to="/">
      <button class="back-btn">Back to Home</button>
    </router-link>
=======
  <div class="page">
    <div class="shell">
      <header class="hero">
        <div class="hero-copy">
          <p class="eyebrow">Flight Results</p>
          <h1>{{ dep || "—" }} → {{ arr || "—" }}</h1>

          <p class="subtle">
            Ranked from price, reliability, flight duration, and nonstop fit, with historical route data influencing the result.
          </p>

          <div class="trip-meta">
            <span class="meta-pill" :class="{ active: oneWay }">
              {{ oneWay ? "One-way trip" : "Round trip" }}
            </span>

            <span class="meta-pill">
              Depart: {{ dateFrom || "Not selected" }}
            </span>

            <span v-if="!oneWay" class="meta-pill">
              Return: {{ dateTo || "Not selected" }}
            </span>

            <span class="meta-pill">
              Outbound timing: {{ timingLabel(outboundTiming) }}
            </span>

            <span v-if="!oneWay" class="meta-pill">
              Return timing: {{ timingLabel(returnTiming) }}
            </span>
          </div>
        </div>

        <div class="legend">
          <div class="legend-item">
            <span class="dot dot-green"></span>
            Cheapest
          </div>
          <div class="legend-item">
            <span class="dot dot-yellow"></span>
            Most balanced
          </div>
          <div class="legend-item">
            <span class="dot dot-blue"></span>
            Strong historical fit
          </div>
        </div>
      </header>

      <section class="panel">
        <div class="panel-head">
          <h2>Your Preferences</h2>
          <span class="panel-note">Weights taken from your star ratings</span>
        </div>

        <div class="prefs">
          <div v-for="r in ratings" :key="r.id" class="pref-row">
            <div class="pref-top">
              <span class="pref-label">{{ r.label }}</span>
              <span class="pref-value">{{ r.rating }}/5</span>
            </div>

            <div class="meter">
              <div class="meter-fill" :style="{ width: `${r.rating * 20}%` }"></div>
            </div>
          </div>
        </div>
      </section>

      <section class="panel">
        <div class="panel-head">
          <h2>Recommended Flights</h2>
          <span class="panel-note">Generated from route data and preference fit</span>
        </div>

        <div v-if="loading" class="loading-state">
          Loading flights...
        </div>

        <div v-else-if="!rankedFlights.length" class="empty-state">
          No flights were returned for this route, so fallback examples are shown below.
        </div>

        <div class="cards">
          <article
            v-for="flight in rankedFlights"
            :key="flight.name + flight.price + flight.score"
            class="flight-card"
            :class="`tone-${flight.tone}`"
          >
            <div class="card-top">
              <div>
                <div class="carrier-row">
                  <h3>{{ flight.name }}</h3>
                  <span class="badge" :class="`badge-${flight.tone}`">
                    {{ flight.badge }}
                  </span>
                </div>
                <p class="route-line">{{ dep || "—" }} → {{ arr || "—" }}</p>
              </div>

              <div class="price-box">
                <span class="price-label">Estimated price range</span>
                <span class="price">{{ flight.price }}</span>
              </div>
            </div>

            <div class="score-wrap">
              <div class="score-text">
                <span>Match score</span>
                <strong>{{ flight.score }}%</strong>
              </div>
              <div class="score-bar">
                <div class="score-fill" :style="{ width: `${flight.score}%` }"></div>
              </div>
            </div>

            <p class="desc">{{ flight.desc }}</p>

            <div class="tag-row">
              <span v-for="t in flight.tags" :key="t" class="tag">{{ t }}</span>
            </div>

            <a
              class="link"
              :href="flight.bookingUrl || googleFlightsLink"
              target="_blank"
              rel="noreferrer"
            >
              View on Google Flights →
            </a>
          </article>
        </div>
      </section>
    </div>
>>>>>>> merged_branch-5-11-26
  </div>
</template>

<script>
<<<<<<< HEAD
export default {
  data() {
    return {
      dep: '',
      arr: '',
      maxPrice: '',
      maxLayovers: '',
      flights: [],
      searched: false
    }
  },
  methods: {
    async getLiveFlights() {
      try {
        const response = await fetch(`/api/liveFlights?dep=${this.dep}&arr=${this.arr}`)
        const data = await response.json()
        this.searched = true

        // AviationStack wraps results in a "data" array
        if (Array.isArray(data)) {
          this.flights = data
        } else {
          this.flights = []
        }
      } catch (err) {
        console.error('Error fetching live flights:', err)
        this.flights = []
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
  padding: 2rem;
}

h1 {
  font-size: 2.5rem;
  margin-bottom: 1.5rem;
}

.form {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  width: 300px;
  margin-bottom: 2rem;
}

input, select {
  padding: 0.5rem;
  font-size: 1rem;
  border: 1px solid #ccc;
  border-radius: 4px;
}

button {
  padding: 0.5rem 1rem;
  font-size: 1rem;
  cursor: pointer;
}

ul {
  list-style: none;
  padding: 0;
  width: 100%;
  max-width: 700px;
}

li {
  padding: 0.75rem;
  border-bottom: 1px solid #eee;
  font-size: 1rem;
}

.back-btn {
  margin-top: 2rem;
=======
const AIRLINE_PROFILES = {
  southwest: {
    priceValue: 96,
    onTimeReliability: 72,
    speed: 78,
    directFlight: 88
  },
  alaska: {
    priceValue: 88,
    onTimeReliability: 86,
    speed: 82,
    directFlight: 85
  },
  united: {
    priceValue: 76,
    onTimeReliability: 80,
    speed: 79,
    directFlight: 78
  },
  jetblue: {
    priceValue: 73,
    onTimeReliability: 79,
    speed: 78,
    directFlight: 77
  },
  delta: {
    priceValue: 68,
    onTimeReliability: 91,
    speed: 84,
    directFlight: 83
  },
  american: {
    priceValue: 74,
    onTimeReliability: 76,
    speed: 75,
    directFlight: 79
  },
  hawaiian: {
    priceValue: 77,
    onTimeReliability: 82,
    speed: 76,
    directFlight: 80
  },
  spirit: {
    priceValue: 57,
    onTimeReliability: 54,
    speed: 63,
    directFlight: 55
  },
  frontier: {
    priceValue: 54,
    onTimeReliability: 52,
    speed: 62,
    directFlight: 53
  },
  default: {
    priceValue: 70,
    onTimeReliability: 72,
    speed: 72,
    directFlight: 72
  }
};

export default {
  name: "LiveFlights",

  data() {
    return {
      dep: "",
      arr: "",
      ratings: [],
      oneWay: false,
      dateFrom: "",
      dateTo: "",
      outboundTiming: "early",
      returnTiming: "late",
      flights: [],
      loading: false
    };
  },

  computed: {
    googleFlightsLink() {
      const base = `https://www.google.com/travel/flights?q=Flights%20from%20${encodeURIComponent(
        this.dep || ""
      )}%20to%20${encodeURIComponent(this.arr || "")}`;

      if (this.oneWay) {
        return `${base}%20one%20way%20${encodeURIComponent(this.dateFrom || "")}`;
      }

      return `${base}%20${encodeURIComponent(this.dateFrom || "")}%20to%20${encodeURIComponent(
        this.dateTo || ""
      )}`;
    },

    preferenceWeights() {
      const weights = {
        price: 0,
        onTime: 0,
        duration: 0,
        direct: 0
      };

      for (const r of this.ratings) {
        const v = Number(r.rating || 0);
        const label = String(r.label || "").toLowerCase();

        if (label.includes("price")) weights.price = v;
        if (label.includes("on-time")) weights.onTime = v;
        if (label.includes("duration")) weights.duration = v;
        if (label.includes("direct")) weights.direct = v;
      }

      return weights;
    },

    normalizedImportance() {
      const p = this.importanceValue(this.preferenceWeights.price);
      const o = this.importanceValue(this.preferenceWeights.onTime);
      const d = this.importanceValue(this.preferenceWeights.duration);
      const x = this.importanceValue(this.preferenceWeights.direct);
      const total = p + o + d + x || 1;

      return {
        price: p / total,
        onTime: o / total,
        duration: d / total,
        direct: x / total
      };
    },

    preferenceSummary() {
      const w = this.preferenceWeights;

      return {
        priceFocus: w.price >= 4,
        priceWeak: w.price <= 2,
        reliabilityFocus: w.onTime >= 4,
        durationFocus: w.duration >= 4,
        directFocus: w.direct >= 4
      };
    },

    rankedFlights() {
      const weights = this.preferenceWeights;
      const summary = this.preferenceSummary;
      const importance = this.normalizedImportance;

      const availableFlights = (this.flights || [])
        .filter((f) => f && f.available !== false)
        .filter((f) => f.seatsLeft == null || Number(f.seatsLeft) > 0)
        .map((f) => this.normalizeFlight(f));

      if (!availableFlights.length) return [];

      const priceFloor = Math.min(...availableFlights.map((f) => f.priceMin));
      const priceCeil = Math.max(...availableFlights.map((f) => f.priceMax));
      const durationMin = Math.min(...availableFlights.map((f) => f.durationMinutes));
      const durationMax = Math.max(...availableFlights.map((f) => f.durationMinutes));

      const enriched = availableFlights.map((flight) => {
        const priceScore = this.computePriceScore(flight, priceFloor, priceCeil, weights.price);
        const onTimeScore = this.computeOnTimeScore(flight, weights.onTime);
        const durationScore = this.computeDurationScore(flight, durationMin, durationMax, weights.duration);
        const directScore = this.computeDirectScore(flight, weights.direct);
        const departureScore = this.computeDepartureScore(flight);
        const brandBoost = this.computeBrandBoost(flight, summary);

        const preferenceScore =
          priceScore * importance.price +
          onTimeScore * importance.onTime +
          durationScore * importance.duration +
          directScore * importance.direct;

        const variancePenalty =
          Math.abs(50 - priceScore) * importance.price +
          Math.abs(50 - onTimeScore) * importance.onTime +
          Math.abs(50 - durationScore) * importance.duration +
          Math.abs(50 - directScore) * importance.direct;

        const routeBias = this.stableFactor(`${this.dep}|${this.arr}|${flight.name}`) * 4 - 2;

        let score =
          preferenceScore * 0.9 +
          departureScore * 0.08 +
          brandBoost +
          routeBias -
          variancePenalty * 0.12;

        score = this.clamp(Math.round(score), 0, 100);

        return {
          ...flight,
          priceScore,
          onTimeScore,
          durationScore,
          directScore,
          departureScore,
          score
        };
      });

      const sorted = [...enriched].sort((a, b) => b.score - a.score).slice(0, 5);
      if (!sorted.length) return [];

      const cheapestName = summary.priceFocus
        ? [...enriched].sort((a, b) => a.priceMin - b.priceMin)[0]?.name
        : null;

      const mostReliableName = [...enriched].sort((a, b) => b.onTimeScore - a.onTimeScore)[0]?.name;
      const fastestName = [...enriched].sort((a, b) => b.durationScore - a.durationScore)[0]?.name;
      const mostDirectName = [...enriched].sort((a, b) => b.directScore - a.directScore)[0]?.name;

      return sorted.map((flight, index) => ({
        ...flight,
        badge:
          index === 0
            ? "Top Pick"
            : cheapestName && flight.name === cheapestName
              ? "Cheapest"
              : flight.name === mostReliableName
                ? "Most Reliable"
                : flight.name === fastestName
                  ? "Fastest"
                  : flight.name === mostDirectName
                    ? "Most Direct"
                    : "Best Value"
      }));
    }
  },

  async mounted() {
    const q = this.$route.query;

    this.dep = q.dep || "";
    this.arr = q.arr || "";
    this.oneWay = q.oneWay === "true" || q.oneWay === true;
    this.dateFrom = q.dateFrom || "";
    this.dateTo = q.dateTo || "";
    this.outboundTiming = q.outboundTiming || "early";
    this.returnTiming = q.returnTiming || "late";

    try {
      this.ratings = q.ratings ? JSON.parse(q.ratings) : [];
    } catch {
      this.ratings = [];
    }

    await this.fetchFlights();
  },

  methods: {
    timingLabel(value) {
      if (value === "early") return "Early";
      if (value === "midday") return "Mid-day";
      if (value === "late") return "Late";
      return "Don't care";
    },

    importanceValue(rating) {
      const r = this.clamp(Number(rating) || 0, 0, 5);
      if (r <= 0) return 0;
      return Math.pow(r, 2.35);
    },

    flexScore(rawScore, rating) {
      const strength = this.clamp((Number(rating) || 0) / 5, 0, 1);
      const compressed = 50 + (this.clamp(rawScore, 0, 100) - 50) * strength;
      return Math.round(this.clamp(compressed, 0, 100));
    },

    async fetchFlights() {
      this.loading = true;

      try {
        const params = new URLSearchParams({
          dep: this.dep,
          arr: this.arr,
          oneWay: String(this.oneWay),
          dateFrom: this.dateFrom || "",
          dateTo: this.dateTo || "",
          outboundTiming: this.outboundTiming || "",
          returnTiming: this.returnTiming || ""
        });

        const res = await fetch(`/api/liveFlights?${params.toString()}`);
        if (!res.ok) throw new Error(`HTTP ${res.status}`);

        const payload = await res.json();
        const rows = Array.isArray(payload)
          ? payload
          : Array.isArray(payload.flights)
            ? payload.flights
            : Array.isArray(payload.data)
              ? payload.data
              : [];

        this.flights = rows.length ? rows : this.generateFallbackFlights();
      } catch (err) {
        console.error("Failed to load flights:", err);
        this.flights = this.generateFallbackFlights();
      } finally {
        this.loading = false;
      }
    },

    generateFallbackFlights() {
      return [
        {
          carrierName: "Southwest Airlines",
          historicalAvgFare: 128,
          historicalLowFare: 109,
          historicalHighFare: 164,
          durationMinutes: 95,
          nonstop: true,
          departureTime: "2026-06-13T06:15:00",
          onTimeReliability: 82,
          stops: 0
        },
        {
          carrierName: "Alaska Airlines",
          historicalAvgFare: 144,
          historicalLowFare: 118,
          historicalHighFare: 178,
          durationMinutes: 98,
          nonstop: true,
          departureTime: "2026-06-13T07:05:00",
          onTimeReliability: 86,
          stops: 0
        },
        {
          carrierName: "American Airlines",
          historicalAvgFare: 151,
          historicalLowFare: 132,
          historicalHighFare: 189,
          durationMinutes: 122,
          nonstop: true,
          departureTime: "2026-06-13T18:20:00",
          onTimeReliability: 77,
          stops: 0
        },
        {
          carrierName: "Delta Air Lines",
          historicalAvgFare: 172,
          historicalLowFare: 154,
          historicalHighFare: 219,
          durationMinutes: 118,
          nonstop: false,
          departureTime: "2026-06-13T09:40:00",
          onTimeReliability: 89,
          stops: 1
        },
        {
          carrierName: "United Airlines",
          historicalAvgFare: 167,
          historicalLowFare: 148,
          historicalHighFare: 208,
          durationMinutes: 130,
          nonstop: false,
          departureTime: "2026-06-13T18:45:00",
          onTimeReliability: 76,
          stops: 1
        }
      ];
    },

    normalizeFlight(flight) {
      const name =
        flight.carrierName ||
        flight.airlineName ||
        flight.airline ||
        flight.name ||
        "Unknown Airline";

      const profile = this.getAirlineProfile(name);

      const historicalAvg = this.toNumber(
        flight.historicalAvgFare ?? flight.avgFare ?? flight.medianFare ?? flight.baseFare
      );

      const historicalLow = this.toNumber(
        flight.historicalLowFare ?? flight.lowFare ?? (historicalAvg ? historicalAvg * 0.9 : 0)
      );

      const historicalHigh = this.toNumber(
        flight.historicalHighFare ?? flight.highFare ?? (historicalAvg ? historicalAvg * 1.15 : 0)
      );

      const durationMinutes = this.toNumber(
        flight.durationMinutes ??
          flight.duration ??
          flight.elapsedMinutes ??
          flight.tripDurationMinutes ??
          flight.flightTimeMinutes
      );

      const departureTime = flight.departureTime || flight.departure || flight.departureDateTime || "";
      const departureHour = this.extractHour(departureTime);

      const stopsRaw =
        flight.stops ??
        flight.layovers ??
        flight.numStops ??
        (flight.nonstop === true || flight.direct === true ? 0 : null);

      const stops = Number.isFinite(Number(stopsRaw)) ? Number(stopsRaw) : null;
      const nonstop =
        flight.nonstop === true ||
        flight.direct === true ||
        stops === 0 ||
        String(flight.stopType || "").toLowerCase().includes("nonstop") ||
        String(flight.stopsText || "").toLowerCase().includes("nonstop");

      const urgencyMultiplier = this.getUrgencyMultiplier(this.getDaysToDeparture());
      const routeFactor = this.stableFactor(`${this.dep}|${this.arr}|${name}`);
      const dayFactor = this.getDayFactor(this.dateFrom);
      const seasonFactor = this.getSeasonFactor(this.dateFrom);

      const basePrice = historicalAvg || 220;
      const center = Math.max(
        49,
        Math.round(basePrice * urgencyMultiplier * routeFactor * dayFactor * seasonFactor)
      );

      const baseSpread = Math.max(18, Math.round((historicalHigh - historicalLow) / 2) || 25);
      const spread = Math.round(baseSpread * (this.getDaysToDeparture() <= 7 ? 1.2 : 1.0));

      let adjustedCenter = center;

      if (
        (profile.name === "southwest" || profile.name === "alaska") &&
        (this.outboundTiming === "early" || this.outboundTiming === "late")
      ) {
        adjustedCenter -= 12;
      }

      if (profile.name === "american" && this.returnTiming === "late") {
        adjustedCenter -= 8;
      }

      const priceMin = this.roundToNearestFive(Math.max(49, adjustedCenter - spread));
      const priceMax = this.roundToNearestFive(Math.max(priceMin + 5, adjustedCenter + spread));

      const routeTimingPreference = this.getTimingPreferenceForFlight(flight);

      return {
        name,
        tone: flight.tone || this.toneFromPrice(priceMin, historicalLow || priceMin),
        bookingUrl: flight.bookingUrl || flight.googleFlightsUrl || this.googleFlightsLink,
        desc:
          flight.description ||
          flight.desc ||
          "Generated from route demand, historical fares, airline history, and your preference fit.",
        tags:
          flight.tags ||
          this.defaultTagsFromTraits({
            price: priceMin,
            onTime: flight.onTimeReliability ?? flight.historicalOnTime ?? profile.onTimeReliability,
            duration: durationMinutes,
            direct: nonstop ? 100 : stops === 1 ? 65 : 35,
            departureHour
          }),
        profile,
        historicalAvg,
        historicalLow,
        historicalHigh,
        priceMin,
        priceMax,
        priceMid: Math.round((priceMin + priceMax) / 2),
        durationMinutes,
        departureTime,
        departureHour,
        stops,
        nonstop,
        routeTimingPreference
      };
    },

    computePriceScore(flight, floor, ceil, rating) {
      const price = flight.priceMid || flight.priceMin || flight.historicalAvg || 0;
      const airlineBias = this.getAirlineProfile(flight.name).priceValue || 68;

      let normalized;
      if (!Number.isFinite(price) || price <= 0) {
        normalized = (airlineBias + 60) / 2;
      } else if (!Number.isFinite(floor) || !Number.isFinite(ceil) || ceil === floor) {
        normalized = 100 - Math.min(100, Math.max(0, price / 5));
      } else {
        normalized = 100 - ((price - floor) / (ceil - floor)) * 100;
      }

      const flexed = this.flexScore(normalized, rating);
      const blended = flexed * 0.9 + airlineBias * 0.1;

      return Math.round(this.clamp(blended, 0, 100));
    },

    computeOnTimeScore(flight, rating) {
      const raw =
        flight.onTimeReliability ??
        flight.historicalOnTime ??
        flight.onTimeScore ??
        flight.reliability ??
        this.getAirlineProfile(flight.name).onTimeReliability;

      return this.flexScore(this.normalizeTo100(raw, 82), rating);
    },

    computeDurationScore(flight, min, max, rating) {
      const duration = flight.durationMinutes;
      const baseline = this.getAirlineProfile(flight.name).speed;

      let normalized;

      if (!Number.isFinite(duration) || duration <= 0) {
        normalized = this.normalizeTo100(baseline, 75);
      } else if (!Number.isFinite(min) || !Number.isFinite(max) || min === max) {
        normalized = this.normalizeTo100(100 - duration, 75);
      } else {
        normalized = 100 - ((duration - min) / (max - min)) * 100;
      }

      return this.flexScore(normalized, rating);
    },

    computeDirectScore(flight, rating) {
      let raw;

      if (flight.nonstop) {
        raw = 100;
      } else {
        const stops = Number.isFinite(flight.stops) ? flight.stops : null;

        if (stops === 0) raw = 100;
        else if (stops === 1) raw = 66;
        else if (stops === 2) raw = 35;
        else if (stops > 2) raw = 20;
        else {
          const text = `${flight.desc || ""} ${flight.name || ""}`.toLowerCase();
          if (text.includes("nonstop") || text.includes("direct")) raw = 95;
          else if (text.includes("1 stop")) raw = 62;
          else if (text.includes("2 stops")) raw = 36;
          else raw = this.normalizeTo100(this.getAirlineProfile(flight.name).directFlight, 68);
        }
      }

      return this.flexScore(raw, rating);
    },

    computeDepartureScore(flight) {
      const pref = flight.routeTimingPreference || this.outboundTiming;
      const hour = flight.departureHour;

      if (!Number.isFinite(hour)) {
        return pref === "dontcare" ? 75 : 60;
      }

      if (pref === "dontcare") return 80;

      const targetRanges = {
        early: [5, 10],
        midday: [10, 15],
        late: [16, 23]
      };

      const [start, end] = targetRanges[pref] || targetRanges.midday;
      let score;

      if (hour >= start && hour < end) {
        score = 100;
      } else {
        const targetCenter = (start + end) / 2;
        const distance = Math.min(Math.abs(hour - targetCenter), 24 - Math.abs(hour - targetCenter));
        score = Math.round(this.clamp(100 - distance * 18, 0, 100));
      }

      return score;
    },

    computeBrandBoost(flight, summary) {
      const airline = this.normalizeText(flight.name);

      let boost = 0;

      if (summary.priceFocus) {
        if (airline.includes("southwest")) boost += 7;
        if (airline.includes("alaska")) boost += 6;
        if (airline.includes("american")) boost += 4;
        if (airline.includes("united")) boost += 2;
      }

      if (summary.priceWeak) {
        if (airline.includes("delta")) boost += 5;
        if (airline.includes("american")) boost += 3;
        if (airline.includes("united")) boost += 3;
        if (airline.includes("southwest")) boost -= 1;
        if (airline.includes("alaska")) boost -= 1;
      }

      if (summary.reliabilityFocus) {
        if (airline.includes("delta")) boost += 7;
        if (airline.includes("alaska")) boost += 4;
        if (airline.includes("southwest")) boost += 1;
        if (airline.includes("american")) boost += 1;
      }

      if (summary.durationFocus) {
        if (airline.includes("united")) boost += 5;
        if (airline.includes("delta")) boost += 3;
        if (airline.includes("alaska")) boost += 2;
        if (airline.includes("southwest")) boost += 1;
      }

      if (summary.directFocus) {
        if (airline.includes("southwest")) boost += 5;
        if (airline.includes("american")) boost += 4;
        if (airline.includes("alaska")) boost += 3;
        if (airline.includes("delta")) boost += 2;
      }

      if (this.outboundTiming === "early") {
        if (airline.includes("southwest")) boost += 2;
        if (airline.includes("alaska")) boost += 2;
      }

      if (this.returnTiming === "late") {
        if (airline.includes("american")) boost += 2;
      }

      return this.clamp(boost, -4, 12);
    },

    getTimingPreferenceForFlight(flight) {
      const label = String(
        flight.direction || flight.leg || flight.segment || flight.bound || ""
      ).toLowerCase();

      if (label.includes("return") || label.includes("inbound") || label.includes("back")) {
        return this.returnTiming || "late";
      }

      if (label.includes("outbound") || label.includes("depart")) {
        return this.outboundTiming || "early";
      }

      return this.outboundTiming || "early";
    },

    getDaysToDeparture() {
      if (!this.dateFrom) return 21;
      const start = new Date();
      const depart = new Date(`${this.dateFrom}T00:00:00`);
      const diff = depart.getTime() - start.getTime();
      return Math.max(0, Math.ceil(diff / 86400000));
    },

    getUrgencyMultiplier(days) {
      if (days <= 3) return 1.38;
      if (days <= 7) return 1.24;
      if (days <= 14) return 1.16;
      if (days <= 30) return 1.08;
      if (days <= 60) return 1.02;
      return 0.97;
    },

    getDayFactor(dateStr) {
      if (!dateStr) return 1.0;
      const d = new Date(`${dateStr}T00:00:00`);
      const day = d.getDay();
      if (day === 5 || day === 6) return 1.08;
      if (day === 0) return 1.05;
      return 1.0;
    },

    getSeasonFactor(dateStr) {
      if (!dateStr) return 1.0;
      const d = new Date(`${dateStr}T00:00:00`);
      const month = d.getMonth() + 1;
      if ([6, 7, 8].includes(month)) return 1.07;
      if ([11, 12].includes(month)) return 1.1;
      return 1.0;
    },

    stableFactor(seed) {
      let hash = 0;
      for (let i = 0; i < seed.length; i++) {
        hash = (hash * 31 + seed.charCodeAt(i)) >>> 0;
      }
      const range = (hash % 17) - 8;
      return 1 + range / 200;
    },

    toneFromPrice(priceMin, lowFloor) {
      if (priceMin <= lowFloor + 15) return "green";
      if (priceMin <= lowFloor + 45) return "yellow";
      return "blue";
    },

    defaultTagsFromTraits(traits) {
      const tags = [];
      if (traits.price >= 80) tags.push("Better value");
      if (traits.onTime >= 80) tags.push("Reliable");
      if (traits.duration >= 80) tags.push("Fast");
      if (traits.direct >= 80) tags.push("Nonstop");
      if (traits.departureHour >= 0 && traits.departureHour <= 9) tags.push("Early");
      if (traits.departureHour >= 16) tags.push("Late");
      return tags.length ? tags : ["Standard"];
    },

    getAirlineProfile(airline = "") {
      const key = this.normalizeText(airline);

      if (key.includes("southwest")) return { name: "southwest", ...AIRLINE_PROFILES.southwest };
      if (key.includes("alaska")) return { name: "alaska", ...AIRLINE_PROFILES.alaska };
      if (key.includes("united")) return { name: "united", ...AIRLINE_PROFILES.united };
      if (key.includes("jetblue") || key.includes("jetblueairways")) return { name: "jetblue", ...AIRLINE_PROFILES.jetblue };
      if (key.includes("delta")) return { name: "delta", ...AIRLINE_PROFILES.delta };
      if (key.includes("american")) return { name: "american", ...AIRLINE_PROFILES.american };
      if (key.includes("hawaiian")) return { name: "hawaiian", ...AIRLINE_PROFILES.hawaiian };
      if (key.includes("spirit")) return { name: "spirit", ...AIRLINE_PROFILES.spirit };
      if (key.includes("frontier")) return { name: "frontier", ...AIRLINE_PROFILES.frontier };

      return { name: "default", ...AIRLINE_PROFILES.default };
    },

    normalizeTo100(value, fallback = 70) {
      const n = Number(value);
      if (!Number.isFinite(n)) return fallback;
      if (n <= 5) return Math.round((n / 5) * 100);
      if (n <= 1) return Math.round(n * 100);
      return Math.round(this.clamp(n, 0, 100));
    },

    normalizeText(value = "") {
      return String(value).toLowerCase().replace(/[^a-z0-9]/g, "");
    },

    toNumber(value) {
      const n = Number(value);
      return Number.isFinite(n) ? n : 0;
    },

    extractHour(value) {
      if (!value) return null;
      const s = String(value).trim();
      const parsed = new Date(s);
      if (!Number.isNaN(parsed.getTime())) {
        return parsed.getHours();
      }
      const match = s.match(/(\d{1,2})(?::(\d{2}))?\s*(am|pm)?/i);
      if (!match) return null;
      let hour = Number(match[1]);
      const suffix = (match[3] || "").toLowerCase();
      if (suffix === "pm" && hour < 12) hour += 12;
      if (suffix === "am" && hour === 12) hour = 0;
      return Number.isFinite(hour) ? hour : null;
    },

    clamp(value, min, max) {
      return Math.min(max, Math.max(min, value));
    },

    roundToNearestFive(n) {
      return Math.round(n / 5) * 5;
    }
  }
};
</script>

<style scoped>
.page {
  min-height: 100vh;
  background:
    radial-gradient(circle at top, rgba(255,255,255,0.95), rgba(244,246,248,1) 40%, rgba(236,239,243,1) 100%);
  color: #111827;
}

.shell {
  max-width: 1080px;
  margin: 0 auto;
  padding: 40px 20px 56px;
}

.hero {
  display: flex;
  align-items: end;
  justify-content: space-between;
  gap: 20px;
  margin-bottom: 28px;
}

.hero-copy {
  min-width: 0;
}

.eyebrow {
  margin: 0 0 8px;
  font-size: 12px;
  letter-spacing: 0.14em;
  text-transform: uppercase;
  color: #6b7280;
}

h1 {
  margin: 0;
  font-size: clamp(2rem, 4vw, 3.25rem);
  line-height: 1.05;
  font-weight: 700;
}

.subtle {
  margin: 12px 0 0;
  color: #6b7280;
  max-width: 640px;
}

.trip-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 18px;
}

.meta-pill {
  display: inline-flex;
  align-items: center;
  padding: 8px 12px;
  border-radius: 999px;
  background: rgba(255,255,255,0.8);
  color: #374151;
  border: 1px solid rgba(17, 24, 39, 0.08);
  box-shadow: 0 8px 24px rgba(17, 24, 39, 0.05);
  font-size: 13px;
}

.meta-pill.active {
  background: rgba(17, 24, 39, 0.94);
  color: #fff;
  border-color: rgba(17, 24, 39, 0.94);
}

.legend {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 14px 16px;
  border: 1px solid rgba(17, 24, 39, 0.08);
  border-radius: 18px;
  background: rgba(255,255,255,0.72);
  backdrop-filter: blur(14px);
  box-shadow: 0 12px 40px rgba(17, 24, 39, 0.06);
  min-width: 220px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  color: #374151;
}

.dot {
  width: 10px;
  height: 10px;
  border-radius: 999px;
}

.dot-green {
  background: #22c55e;
}

.dot-yellow {
  background: #f59e0b;
}

.dot-blue {
  background: #3b82f6;
}

.panel {
  margin-top: 18px;
  padding: 22px;
  border-radius: 24px;
  border: 1px solid rgba(17, 24, 39, 0.08);
  background: rgba(255,255,255,0.78);
  backdrop-filter: blur(16px);
  box-shadow: 0 20px 60px rgba(17, 24, 39, 0.08);
}

.panel-head {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 18px;
}

.panel h2 {
  margin: 0;
  font-size: 1.15rem;
}

.panel-note {
  color: #6b7280;
  font-size: 0.92rem;
}

.prefs {
  display: grid;
  gap: 14px;
}

.pref-row {
  padding: 14px;
  border-radius: 16px;
  background: rgba(249, 250, 251, 0.92);
  border: 1px solid rgba(17, 24, 39, 0.06);
}

.pref-top {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 10px;
}

.pref-label {
  font-weight: 600;
  color: #111827;
}

.pref-value {
  color: #6b7280;
}

.meter {
  height: 10px;
  border-radius: 999px;
  background: #e5e7eb;
  overflow: hidden;
}

.meter-fill {
  height: 100%;
  border-radius: 999px;
  background: linear-gradient(90deg, #111827, #374151);
}

.cards {
  display: grid;
  gap: 16px;
}

.flight-card {
  position: relative;
  overflow: hidden;
  padding: 20px;
  border-radius: 22px;
  border: 1px solid rgba(17, 24, 39, 0.08);
  background: linear-gradient(180deg, rgba(255,255,255,0.96), rgba(250,250,252,0.92));
  box-shadow: 0 12px 32px rgba(17, 24, 39, 0.07);
}

.flight-card::before {
  content: "";
  position: absolute;
  left: 0;
  top: 0;
  width: 7px;
  height: 100%;
}

.tone-green::before {
  background: linear-gradient(180deg, #4ade80, #22c55e);
}

.tone-yellow::before {
  background: linear-gradient(180deg, #fbbf24, #f59e0b);
}

.tone-blue::before {
  background: linear-gradient(180deg, #60a5fa, #3b82f6);
}

.card-top {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: start;
}

.carrier-row {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.carrier-row h3 {
  margin: 0;
  font-size: 1.15rem;
}

.route-line {
  margin: 8px 0 0;
  color: #6b7280;
}

.price-box {
  text-align: right;
  min-width: 140px;
}

.price-label {
  display: block;
  font-size: 12px;
  color: #6b7280;
  margin-bottom: 4px;
}

.price {
  display: inline-block;
  font-size: 1.15rem;
  font-weight: 700;
  color: #111827;
}

.badge {
  display: inline-flex;
  align-items: center;
  padding: 6px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.01em;
}

.badge-green {
  color: #166534;
  background: rgba(34, 197, 94, 0.14);
}

.badge-yellow {
  color: #92400e;
  background: rgba(245, 158, 11, 0.16);
}

.badge-blue {
  color: #1d4ed8;
  background: rgba(59, 130, 246, 0.14);
}

.score-wrap {
  margin-top: 18px;
}

.score-text {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  margin-bottom: 8px;
  color: #4b5563;
}

.score-text strong {
  font-size: 1.1rem;
  color: #111827;
}

.score-bar {
  height: 10px;
  border-radius: 999px;
  background: #e5e7eb;
  overflow: hidden;
}

.score-fill {
  height: 100%;
  border-radius: 999px;
  background: linear-gradient(90deg, #111827, #4b5563);
}

.desc {
  margin: 16px 0 0;
  color: #374151;
  line-height: 1.55;
}

.tag-row {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-top: 14px;
}

.tag {
  padding: 6px 10px;
  border-radius: 999px;
  background: #f3f4f6;
  color: #374151;
  font-size: 12px;
  border: 1px solid rgba(17, 24, 39, 0.06);
}

.link {
  display: inline-flex;
  margin-top: 16px;
  color: #111827;
  text-decoration: none;
  font-weight: 600;
}

.link:hover {
  text-decoration: underline;
}

.loading-state,
.empty-state {
  padding: 14px 0 6px;
  color: #6b7280;
  font-size: 0.95rem;
}

@media (max-width: 780px) {
  .hero {
    flex-direction: column;
    align-items: stretch;
  }

  .legend {
    min-width: 0;
  }

  .card-top {
    flex-direction: column;
  }

  .price-box {
    text-align: left;
  }

  .panel-head {
    flex-direction: column;
    align-items: start;
  }
>>>>>>> merged_branch-5-11-26
}
</style>