package com.FlightArrange.FlightArrange.service;

import com.FlightArrange.FlightArrange.model.FlightReliability;
import com.FlightArrange.FlightArrange.repository.FlightReliabilityRepo;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

@Service
public class FlightScoreService {

    private final FlightReliabilityRepo reliabilityRepo;
    private final SerpApiService serpApiService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final double WEIGHT_RELIABILITY = 0.5;
    private static final double WEIGHT_PRICE       = 0.3;
    private static final double WEIGHT_DURATION    = 0.2;

    public FlightScoreService(FlightReliabilityRepo reliabilityRepo,
                               SerpApiService serpApiService) {
        this.reliabilityRepo = reliabilityRepo;
        this.serpApiService  = serpApiService;
    }

    public String scoreFlight(String dep, String arr, String date) {
        try {
            // Get flights from Serpapi
            List<SerpApiService.SerpFlight> flights = serpApiService.getFlights(dep, arr, date);

            if (flights.isEmpty()) {
                return "{\"error\": \"No flights found for that route\"}";
            }

            // Find max price for normalization
            double maxPrice = flights.stream()
                .mapToDouble(f -> f.price)
                .max()
                .orElse(1.0);

            ArrayNode results = objectMapper.createArrayNode();

            for (SerpApiService.SerpFlight flight : flights) {

                // Filter to US carriers only
                java.util.Set<String> usCarriers = java.util.Set.of(
                    "AA", "DL", "UA", "WN", "B6", "AS", "F9", "NK", "G4", "SY", "MQ", "OO", "YX", "9E"
                );
                if (!usCarriers.contains(flight.carrierCode)) {
                    continue;
                }

                // Look up historical on-time rate from BTS database
                double onTimeRate = getOnTimeRate(flight.carrierCode, dep);

                // Normalize scores to 0-1
                double priceScore = maxPrice > 0
                    ? 1.0 - (flight.price / (maxPrice * 1.2))
                    : 0.5;

                double durationScore = flight.durationMins > 0
                    ? Math.max(0, 1.0 - (flight.durationMins / 600.0))
                    : 0.5;

                double score = (onTimeRate   * WEIGHT_RELIABILITY)
                             + (priceScore   * WEIGHT_PRICE)
                             + (durationScore * WEIGHT_DURATION);

                score      = Math.round(score      * 100.0) / 100.0;
                onTimeRate = Math.round(onTimeRate  * 100.0) / 100.0;

                ObjectNode result = objectMapper.createObjectNode();
                result.put("flight",        flight.flightNumber);
                result.put("airline",       flight.airline);
                result.put("departure",     dep);
                result.put("arrival",       arr);
                result.put("departureTime", flight.departureTime);
                result.put("arrivalTime",   flight.arrivalTime);
                result.put("status",        "scheduled");
                result.put("price",         flight.price);
                result.put("layovers",      flight.layovers);
                result.put("onTimeRate",    onTimeRate);
                result.put("score",         score);
                result.put("durationMins",  flight.durationMins);

                results.add(result);
            }

            ArrayNode sorted = sortByScore(results);
            return objectMapper.writeValueAsString(sorted);

        } catch (Exception e) {
            return "{\"error\": \"Scoring failed: " + e.getMessage() + "\"}";
        }
    }

    private double getOnTimeRate(String carrier, String airport) {
        List<FlightReliability> records =
            reliabilityRepo.findByCarrierAndAirport(carrier, airport);
        if (records.isEmpty()) return 0.75;
        return records.stream()
            .mapToDouble(FlightReliability::getOnTimeRate)
            .average()
            .orElse(0.75);
    }

    private ArrayNode sortByScore(ArrayNode array) {
        java.util.List<com.fasterxml.jackson.databind.JsonNode> list = new java.util.ArrayList<>();
        array.forEach(list::add);
        list.sort((a, b) -> Double.compare(
            b.get("score").asDouble(),
            a.get("score").asDouble()
        ));
        ArrayNode sorted = objectMapper.createArrayNode();
        list.forEach(sorted::add);
        return sorted;
    }
}