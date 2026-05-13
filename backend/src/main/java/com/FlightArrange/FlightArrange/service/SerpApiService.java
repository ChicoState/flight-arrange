package com.FlightArrange.FlightArrange.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

@Service
public class SerpApiService {

    // *** TOGGLE THIS: false = mock data, true = real Serpapi call ***
    private static final boolean USE_LIVE_DATA = true;

    @Value("ad415c9f68208ffcc8cee7b9b4ed9d5d949b7b49c73c10dfa9862150905584d1")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Returns a list of SerpFlight objects for a given route and date
    public List<SerpFlight> getFlights(String dep, String arr, String date) {
        if (!USE_LIVE_DATA) {
            return getMockFlights(dep, arr);
        }
        return getLiveFlights(dep, arr, date);
    }

    private List<SerpFlight> getLiveFlights(String dep, String arr, String date) {
        // Use today's date if none provided
        String searchDate = (date != null && !date.isEmpty())
            ? date
            : java.time.LocalDate.now().toString();

        String url = "https://serpapi.com/search"
                + "?engine=google_flights"
                + "&departure_id=" + dep
                + "&arrival_id=" + arr
                + "&outbound_date=" + searchDate
                + "&type=2"           // one way
                + "&currency=USD"
                + "&hl=en"
                + "&api_key=" + apiKey;

        System.out.println("Calling Serpapi: " + url.replace(apiKey, "***"));

        try {
            String response = restTemplate.getForObject(url, String.class);
            return parseFlights(response);
        } catch (Exception e) {
            System.out.println("Serpapi error: " + e.getMessage());
            return getMockFlights(dep, arr);
        }
    }

    private List<SerpFlight> parseFlights(String json) {
        List<SerpFlight> flights = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(json);

            // Serpapi returns best_flights and other_flights
            // Process both arrays
            for (String key : new String[]{"best_flights", "other_flights"}) {
                JsonNode flightGroups = root.get(key);
                if (flightGroups == null || !flightGroups.isArray()) continue;

                for (JsonNode group : flightGroups) {
                    int price = group.path("price").asInt(0);
                    int layovers = 0;

                    // Count layovers from layovers array
                    JsonNode layoverNode = group.get("layovers");
                    if (layoverNode != null && layoverNode.isArray()) {
                        layovers = layoverNode.size();
                    }

                    // Each group has a flights array with individual flight legs
                    JsonNode legs = group.get("flights");
                    if (legs == null || !legs.isArray() || legs.size() == 0) continue;

                    // Use first leg for flight details
                    JsonNode leg = legs.get(0);
                    String flightNumber = leg.path("flight_number").asText("");
                    String airline      = leg.path("airline").asText("");
                    String airlineCode  = leg.path("airline_logo").asText("")
                                            .isEmpty() ? extractAirlineCode(flightNumber)
                                                       : leg.path("airline").asText("");
                    String depTime      = leg.path("departure_airport").path("time").asText("");
                    String arrTime      = leg.path("arrival_airport").path("time").asText("");
                    int duration        = group.path("total_duration").asInt(0);

                    // Extract 2-letter airline code from flight number
                    String carrierCode = flightNumber.length() >= 2
                        ? flightNumber.substring(0, 2)
                        : airlineCode;

                    flights.add(new SerpFlight(
                        flightNumber,
                        airline,
                        carrierCode,
                        price,
                        layovers,
                        depTime,
                        arrTime,
                        duration
                    ));
                }
            }
        } catch (Exception e) {
            System.out.println("Error parsing Serpapi response: " + e.getMessage());
        }
        return flights;
    }

    private String extractAirlineCode(String flightNumber) {
        if (flightNumber == null || flightNumber.length() < 2) return "";
        return flightNumber.substring(0, 2);
    }

    // Mock data matching the same format as real Serpapi response
    private List<SerpFlight> getMockFlights(String dep, String arr) {
        List<SerpFlight> mocks = new ArrayList<>();
        String today = java.time.LocalDate.now().toString();
        mocks.add(new SerpFlight("DL401",  "Delta Air Lines",    "DL", 187, 0, today + "T08:00:00", today + "T09:11:00", 71));
        mocks.add(new SerpFlight("AA202",  "American Airlines",  "AA", 210, 0, today + "T09:30:00", today + "T10:45:00", 75));
        mocks.add(new SerpFlight("UA550",  "United Airlines",    "UA", 245, 1, today + "T07:15:00", today + "T09:30:00", 135));
        mocks.add(new SerpFlight("WN1234", "Southwest Airlines", "WN", 156, 0, today + "T11:00:00", today + "T12:15:00", 75));
        mocks.add(new SerpFlight("B6789",  "JetBlue Airways",    "B6", 198, 0, today + "T13:00:00", today + "T14:10:00", 70));
        return mocks;
    }

    // Simple data class to hold one flight's info from Serpapi
    public static class SerpFlight {
        public String flightNumber;
        public String airline;
        public String carrierCode;
        public double price;
        public int layovers;
        public String departureTime;
        public String arrivalTime;
        public int durationMins;

        public SerpFlight(String flightNumber, String airline, String carrierCode,
                          double price, int layovers, String departureTime,
                          String arrivalTime, int durationMins) {
            this.flightNumber  = flightNumber;
            this.airline       = airline;
            this.carrierCode   = carrierCode;
            this.price         = price;
            this.layovers      = layovers;
            this.departureTime = departureTime;
            this.arrivalTime   = arrivalTime;
            this.durationMins  = durationMins;
        }
    }
}