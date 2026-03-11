package com.FlightArrange.FlightArrange.service;

import com.FlightArrange.FlightArrange.model.FlightReliability;
import com.FlightArrange.FlightArrange.repository.FlightReliabilityRepo;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

@Service
public class FlightScoreService {

    private final FlightReliabilityRepo reliabilityRepo;
    private final SkyscannerService skyscannerService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Scoring weights - these three must add up to 1.0
    private static final double WEIGHT_RELIABILITY = 0.5;
    private static final double WEIGHT_PRICE       = 0.3;
    private static final double WEIGHT_DURATION    = 0.2;

    public FlightScoreService(FlightReliabilityRepo reliabilityRepo,
                               SkyscannerService skyscannerService) {
        this.reliabilityRepo = reliabilityRepo;
        this.skyscannerService = skyscannerService;
    }

    public String scoreFlight(String aviationStackJson) {
        try {
            JsonNode root = objectMapper.readTree(aviationStackJson);
            JsonNode flights = root.get("data");

            if (flights == null || !flights.isArray()) {
                return "{\"error\": \"No flight data found\"}";
            }

            ArrayNode results = objectMapper.createArrayNode();

            // Find max price across all flights for normalization
            double maxPrice = 1.0;
            double[] prices = new double[flights.size()];
            for (int i = 0; i < flights.size(); i++) {
                JsonNode flight = flights.get(i);
                String dep = flight.path("departure").path("iata").asText();
                String arr = flight.path("arrival").path("iata").asText();
                String date = flight.path("departure").path("scheduled").asText();
                prices[i] = skyscannerService.getPrice(dep, arr, date);
                if (prices[i] > maxPrice) maxPrice = prices[i];
            }

            // Build scored result for each flight
            for (int i = 0; i < flights.size(); i++) {
                JsonNode flight = flights.get(i);

                String flightIata   = flight.path("flight").path("iata").asText();
                String airlineName  = flight.path("airline").path("name").asText();
                String depIata      = flight.path("departure").path("iata").asText();
                String arrIata      = flight.path("arrival").path("iata").asText();
                String depTime      = flight.path("departure").path("scheduled").asText();
                String arrTime      = flight.path("arrival").path("scheduled").asText();
                String status       = flight.path("flight_status").asText();

                // Strip numbers from flight code to get carrier: "DL401" -> "DL"
                String carrierCode = flightIata.replaceAll("[^A-Za-z]", "");

                // Look up historical on-time rate from BTS database
                double onTimeRate = getOnTimeRate(carrierCode, depIata);

                // Get price
                double price = prices[i];

                // Calculate duration in minutes
                double durationMinutes = calculateDuration(depTime, arrTime);

                // Normalize scores to 0-1 range
                // Price: cheaper = higher score
                double priceScore = maxPrice > 0 ? 1.0 - (price / (maxPrice * 1.2)) : 0.5;

                // Duration: shorter = higher score (normalize against 600 min / 10 hrs max)
                double durationScore = durationMinutes > 0
                    ? Math.max(0, 1.0 - (durationMinutes / 600.0))
                    : 0.5;

                // Final weighted score
                double score = (onTimeRate    * WEIGHT_RELIABILITY)
                             + (priceScore    * WEIGHT_PRICE)
                             + (durationScore * WEIGHT_DURATION);

                // Round to 2 decimal places for cleanliness
                score    = Math.round(score    * 100.0) / 100.0;
                onTimeRate = Math.round(onTimeRate * 100.0) / 100.0;

                // Build the combined result object
                ObjectNode result = objectMapper.createObjectNode();
                result.put("flight",        flightIata);
                result.put("airline",       airlineName);
                result.put("departure",     depIata);
                result.put("arrival",       arrIata);
                result.put("departureTime", depTime);
                result.put("arrivalTime",   arrTime);
                result.put("status",        status);
                result.put("price",         price);
                result.put("onTimeRate",    onTimeRate);
                result.put("score",         score);
                result.put("durationMins",  durationMinutes);

                results.add(result);
            }

            // Sort by score descending - best flights first
            results.elements();
            ArrayNode sorted = sortByScore(results);

            return objectMapper.writeValueAsString(sorted);

        } catch (Exception e) {
            return "{\"error\": \"Scoring failed: " + e.getMessage() + "\"}";
        }
    }

    // Look up on-time rate from BTS database by carrier + departure airport
    private double getOnTimeRate(String carrier, String airport) {
        List<FlightReliability> records =
            reliabilityRepo.findByCarrierAndAirport(carrier, airport);
        if (records.isEmpty()) {
            return 0.75; // default if no historical data found
        }
        // Average across all months in the database
        return records.stream()
            .mapToDouble(FlightReliability::getOnTimeRate)
            .average()
            .orElse(0.75);
    }

    // Calculate flight duration in minutes from ISO datetime strings
    private double calculateDuration(String depTime, String arrTime) {
        try {
            java.time.LocalDateTime dep =
                java.time.LocalDateTime.parse(depTime);
            java.time.LocalDateTime arr =
                java.time.LocalDateTime.parse(arrTime);
            return java.time.Duration.between(dep, arr).toMinutes();
        } catch (Exception e) {
            return 180; // default 3 hours if parsing fails
        }
    }

    // Sort ArrayNode by score field descending
    private ArrayNode sortByScore(ArrayNode array) {
        java.util.List<JsonNode> list = new java.util.ArrayList<>();
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