package com.FlightArrange.FlightArrange;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HomeController {
    
    @GetMapping("/api/home")
    public String home() {
        return "Backend connected!";
    }

    @GetMapping("/api/flights")
    public Map<String, String> getFlightInfo() {
       System.out.println("Inside getFlightInfo");
        return Map.of("flightInfo", "Flight 101 from LA to New York at 10:00AM");
    }
}