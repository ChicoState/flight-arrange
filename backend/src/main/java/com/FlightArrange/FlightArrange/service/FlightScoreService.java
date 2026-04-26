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

            for (int i = 0; i < flights.size(); i++) {
                JsonNode flight = flights.get(i);

                String flightIata  = flight.path("flight").path("iata").asText();
                String airlineName = flight.path("airline").path("name").asText();
                String depIata     = flight.path("departure").path("iata").asText();
                String arrIata     = flight.path("arrival").path("iata").asText();
                String depTime     = flight.path("departure").path("scheduled").asText();
                String arrTime     = flight.path("arrival").path("scheduled").asText();
                String status      = flight.path("flight_status").asText();

                String carrierCode = flightIata.length() >= 2 ? flightIata.substring(0, 2) : flightIata;

                java.util.Set<String> usCarriers = java.util.Set.of("AA", "DL", "UA", "WN", "B6", "AS", "F9", "NK", "G4", "SY", "MQ", "OO", "YX", "9E");
                if (!usCarriers.contains(carrierCode)) {
                    continue;
                }

                double onTimeRate = getOnTimeRate(carrierCode, depIata);
                double price = prices[i];
                double durationMinutes = calculateDuration(depTime, arrTime);

                double priceScore = maxPrice > 0 ? 1.0 - (price / (maxPrice * 1.2)) : 0.5;
                double durationScore = durationMinutes > 0
                    ? Math.max(0, 1.0 - (durationMinutes / 600.0))
                    : 0.5;

                double score = (onTimeRate   * WEIGHT_RELIABILITY)
                             + (priceScore   * WEIGHT_PRICE)
                             + (durationScore * WEIGHT_DURATION);

                score      = Math.round(score      * 100.0) / 100.0;
                onTimeRate = Math.round(onTimeRate  * 100.0) / 100.0;

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

            ArrayNode sorted = sortByScore(results);
            return objectMapper.writeValueAsString(sorted);

        } catch (Exception e) {
            return "{\"error\": \"Scoring failed: " + e.getMessage() + "\"}";
        }
    }

    private double getOnTimeRate(String carrier, String airport) {
        List<FlightReliability> records = reliabilityRepo.findByCarrierAndAirport(carrier, airport);
        if (records.isEmpty()) {
            return 0.75;
        }
        return records.stream()
            .mapToDouble(FlightReliability::getOnTimeRate)
            .average()
            .orElse(0.75);
    }

    private double calculateDuration(String depTime, String arrTime) {
        try {
            java.time.OffsetDateTime dep = java.time.OffsetDateTime.parse(depTime);
            java.time.OffsetDateTime arr = java.time.OffsetDateTime.parse(arrTime);
            return java.time.Duration.between(dep, arr).toMinutes();
        } catch (Exception e) {
            return 180;
        }
    }

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