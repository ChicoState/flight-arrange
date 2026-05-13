computed: {
  googleFlightsUrl() {
    const dep = this.flight.departure
    const arr = this.flight.arrival
    const date = this.flight.departureTime
      ? new Date(this.flight.departureTime).toISOString().split('T')[0]
      : ''

    return `https://www.google.com/travel/flights?q=Flights%20from%20${dep}%20to%20${arr}%20on%20${date}`
  }
}