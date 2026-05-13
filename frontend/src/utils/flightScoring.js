rankedFlights() {
  const weights = this.preferenceWeights;

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
    const priceScore = this.computePriceScore(flight, priceFloor, priceCeil);
    const onTimeScore = this.computeOnTimeScore(flight);
    const durationScore = this.computeDurationScore(flight, durationMin, durationMax);
    const directScore = this.computeDirectScore(flight);
    const departureScore = this.computeDepartureScore(flight);
    const airlineBoost = this.getAirlineBoost(flight, weights);

    const historicalBlend = Math.round(
      (priceScore + onTimeScore + durationScore + directScore + departureScore) / 5
    );

    const fitScore = this.computeScore(
      { price: priceScore, onTime: onTimeScore, duration: durationScore, direct: directScore },
      weights
    );

    const hasRatings = Object.values(weights).some((v) => v > 0);

    let score = hasRatings
      ? Math.round(fitScore * 0.8 + historicalBlend * 0.16 + airlineBoost * 0.04)
      : Math.round(historicalBlend * 0.92 + airlineBoost * 0.08);

    score = this.clamp(score, 0, 100);

    return {
      ...flight,
      priceScore,
      onTimeScore,
      durationScore,
      directScore,
      departureScore,
      historicalBlend,
      score
    };
  });

  // 🔍 DEBUG LOGGING (ADD THIS BLOCK)
  console.log("WEIGHTS:", weights);
  console.log("ENRICHED SAMPLE:", enriched.slice(0, 2));
  console.table(
    enriched.slice(0, 5).map(f => ({
      name: f.name,
      priceScore: f.priceScore,
      onTimeScore: f.onTimeScore,
      durationScore: f.durationScore,
      directScore: f.directScore,
      score: f.score
    }))
  );

  const sorted = [...enriched].sort((a, b) => b.score - a.score).slice(0, 5);
  if (!sorted.length) return [];

  const cheapestName = [...enriched].sort((a, b) => a.priceMin - b.priceMin)[0]?.name;
  const mostReliableName = [...enriched].sort((a, b) => b.onTimeScore - a.onTimeScore)[0]?.name;
  const fastestName = [...enriched].sort((a, b) => b.durationScore - a.durationScore)[0]?.name;
  const mostDirectName = [...enriched].sort((a, b) => b.directScore - a.directScore)[0]?.name;

  return sorted.map((flight, index) => ({
    ...flight,
    badge:
      index === 0
        ? "Top Pick"
        : flight.name === cheapestName
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