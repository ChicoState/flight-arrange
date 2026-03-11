package com.FlightArrange.FlightArrange;

import com.FlightArrange.FlightArrange.model.FlightReliability;
import com.FlightArrange.FlightArrange.repository.FlightReliabilityRepo;
import com.FlightArrange.FlightArrange.service.AviationStackService;
import com.FlightArrange.FlightArrange.service.FlightScoreService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class HomeController {

    private final FlightReliabilityRepo flightReliabilityRepo;
    private final AviationStackService aviationStackService;
    private final FlightScoreService flightScoreService;

    public HomeController(FlightReliabilityRepo flightReliabilityRepo,
                          AviationStackService aviationStackService,
                          FlightScoreService flightScoreService) {
        this.flightReliabilityRepo = flightReliabilityRepo;
        this.aviationStackService  = aviationStackService;
        this.flightScoreService    = flightScoreService;
    }

    // Returns all historical BTS data
    @GetMapping("/api/flights")
    public List<FlightReliability> getFlights() {
        return flightReliabilityRepo.findAll();
    }

    // Returns live flights enriched with price, on-time rate and score
    @GetMapping("/api/liveFlights")
    public String getLiveFlights(
            @RequestParam(defaultValue = "LAX") String dep,
            @RequestParam(defaultValue = "JFK") String arr) {
        String rawFlights = aviationStackService.getFlights(dep, arr);
        return flightScoreService.scoreFlight(rawFlights);
    }
}