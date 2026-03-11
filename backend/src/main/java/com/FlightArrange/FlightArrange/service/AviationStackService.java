package com.FlightArrange.FlightArrange.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.beans.factory.annotation.Value;

@Service
public class AviationStackService {

    // *** TOGGLE THIS: false = mock data (safe for dev), true = live API call ***
    private static final boolean USE_LIVE_DATA = false;

    @Value("${aviationstack.api.key:e1e0213f1024268b8768de0cf7db9056}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String getFlights(String depIata, String arrIata) {
        if (!USE_LIVE_DATA) {
            return getMockFlights();
        }
        return getLiveFlights(depIata, arrIata);
    }

    private String getLiveFlights(String depIata, String arrIata) {
        String url = "http://api.aviationstack.com/v1/flights"
                + "?access_key=" + apiKey
                + "&dep_iata=" + depIata
                + "&arr_iata=" + arrIata
                + "&limit=10";

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getBody();
        } catch (Exception e) {
            return "{\"error\": \"Failed to fetch live flights: " + e.getMessage() + "\"}";
        }
    }

    private String getMockFlights() {
        return """
        {
          "data": [
            {
              "flight": {"iata": "DL401"},
              "airline": {"name": "Delta"},
              "departure": {"iata": "LAX", "scheduled": "2025-06-01T08:00:00"},
              "arrival":   {"iata": "JFK", "scheduled": "2025-06-01T16:00:00"},
              "flight_status": "scheduled"
            },
            {
              "flight": {"iata": "AA202"},
              "airline": {"name": "American"},
              "departure": {"iata": "LAX", "scheduled": "2025-06-01T09:30:00"},
              "arrival":   {"iata": "MIA", "scheduled": "2025-06-01T17:45:00"},
              "flight_status": "scheduled"
            },
            {
              "flight": {"iata": "UA550"},
              "airline": {"name": "United"},
              "departure": {"iata": "SFO", "scheduled": "2025-06-01T07:15:00"},
              "arrival":   {"iata": "ORD", "scheduled": "2025-06-01T13:30:00"},
              "flight_status": "scheduled"
            }
          ]
        }
        """;
    }
}