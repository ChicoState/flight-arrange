package com.FlightArrange.FlightArrange.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class SkyscannerService {

    // *** TOGGLE THIS: false = mock prices, true = real Skyscanner API ***
    private static final boolean USE_LIVE_DATA = false;

    public double getPrice(String depIata, String arrIata, String date) {
        if (!USE_LIVE_DATA) {
            return getMockPrice(depIata, arrIata);
        }
        return getLivePrice(depIata, arrIata, date);
    }

    private double getMockPrice(String dep, String arr) {
        // Realistic mock prices per route
        Map<String, Double> mockPrices = new HashMap<>();
        mockPrices.put("LAX-JFK", 342.0);
        mockPrices.put("LAX-MIA", 289.0);
        mockPrices.put("SFO-ORD", 198.0);
        mockPrices.put("LAX-ORD", 210.0);
        mockPrices.put("JFK-LAX", 315.0);

        String key = dep + "-" + arr;
        // If route not in mock list, generate a realistic random price
        return mockPrices.getOrDefault(key, 150.0 + new Random().nextInt(300));
    }

    private double getLivePrice(String dep, String arr, String date) {
        // TODO: wire up real Skyscanner/RapidAPI call here when ready
        // Will look something like:
        // GET https://skyscanner44.p.rapidapi.com/search-one-way
        //     ?fromEntityId=LAX&toEntityId=JFK&departDate=2025-06-01
        return 0.0;
    }
}