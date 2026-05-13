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
          <div class="companion-top">
            <span class="companion-name">{{ person.name }}</span>
            <button class="remove-btn" @click="removeCompanion(i)">Remove</button>
          </div>
          <span class="sub">
            {{ person.email }} · {{ person.phone }}
          </span>
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
              <option value="dontcare">Don't care</option>
            </select>
          </label>

          <label v-if="!oneWay" class="timing-field">
            <span>Return timing</span>
            <select v-model="returnTiming">
              <option value="early">Early</option>
              <option value="midday">Mid-day</option>
              <option value="late">Late</option>
              <option value="dontcare">Don't care</option>
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

    <div v-if="showModal" class="overlay" @click.self="showModal = false">
      <div class="modal">
        <h2>Add a traveler</h2>

        <input v-model="form.name" placeholder="Name" />
        <input v-model="form.phone" placeholder="Phone" />
        <input v-model="form.email" placeholder="Email" />

        <p v-if="modalError" class="modal-error">{{ modalError }}</p>

        <div class="modal-actions">
          <button @click="showModal = false">Cancel</button>
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
      topics: [
        { id: 1, label: "Price", rating: 0, hover: 0 },
        { id: 2, label: "On-Time Reliability", rating: 0, hover: 0 },
        { id: 3, label: "Flight Duration", rating: 0, hover: 0 },
        { id: 4, label: "Direct Flight", rating: 0, hover: 0 }
      ]
    };
  },

  computed: {
    allRated() {
      return this.topics.every((t) => t.rating > 0);
    },

    hasCompanions() {
      return this.companions.length > 0;
    },

    today() {
      const d = new Date();
      const yyyy = d.getFullYear();
      const mm = String(d.getMonth() + 1).padStart(2, "0");
      const dd = String(d.getDate()).padStart(2, "0");
      return `${yyyy}-${mm}-${dd}`;
    },

    canProceedStep1() {
      const base =
        this.starterLo.trim() !== "" &&
        this.destination.trim() !== "" &&
        this.dateFrom !== "";

      if (this.oneWay) return base;

      return base && this.dateTo !== "";
    }
  },

  methods: {
    addCompanion() {
      const validation = this.validateCompanion(this.form);

      if (!validation.ok) {
        this.modalError = validation.message;
        return;
      }

      this.companions.push({
        name: validation.name,
        phone: validation.phone,
        email: validation.email
      });

      this.form = { name: "", phone: "", email: "" };
      this.modalError = "";
      this.showModal = false;
    },

    removeCompanion(index) {
      this.companions.splice(index, 1);
    },

    validateCompanion(form) {
      const name = String(form.name || "").trim().replace(/\s+/g, " ");
      const phoneRaw = String(form.phone || "").trim();
      const email = String(form.email || "").trim();

      if (!name) return { ok: false, message: "Name is required." };

      const nameParts = name.split(" ").filter(Boolean);
      if (nameParts.length < 2) {
        return { ok: false, message: "Enter both a first and last name." };
      }

      if (!/^[A-Za-z][A-Za-z'\-]*(\s+[A-Za-z][A-Za-z'\-]*)+$/.test(name)) {
        return { ok: false, message: "Name must include a first and last name." };
      }

      if (!email || !email.includes("@") || !email.toLowerCase().endsWith(".com")) {
        return { ok: false, message: "Email must include @ and end with .com." };
      }

      const digits = phoneRaw.replace(/\D/g, "");
      const plusCount = (phoneRaw.match(/\+/g) || []).length;

      if (plusCount > 1 || (phoneRaw.includes("+") && phoneRaw.indexOf("+") !== 0)) {
        return { ok: false, message: "Phone number format is invalid." };
      }

      const digitCount = digits.length;

      if (phoneRaw.startsWith("+")) {
        if (digitCount < 10 || digitCount > 13) {
          return { ok: false, message: "International phone numbers must have 10 to 13 digits." };
        }
      } else {
        if (digitCount !== 10) {
          return {
            ok: false,
            message: "Phone number must have exactly 10 digits, or use + for international numbers."
          };
        }
      }

      return { ok: true, name, phone: phoneRaw, email };
    },

    goToFlights() {
      if (!this.canProceedStep1) return;

      this.$router.push({
        path: "/live-flights",
        query: {
          dep: this.starterLo,
          arr: this.destination,
          oneWay: this.oneWay,
          dateFrom: this.dateFrom,
          dateTo: this.oneWay ? "" : this.dateTo,
          outboundTiming: this.outboundTiming,
          returnTiming: this.returnTiming,
          ratings: JSON.stringify(this.topics)
        }
      });
    }
  }
};
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
</style>