package com.FlightArrange.FlightArrange;

import com.FlightArrange.FlightArrange.model.FlightReliability;
import com.FlightArrange.FlightArrange.repository.FlightReliabilityRepo;
import com.FlightArrange.FlightArrange.service.FlightScoreService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class HomeController {

    private final FlightReliabilityRepo flightReliabilityRepo;
    private final FlightScoreService flightScoreService;

    public HomeController(FlightReliabilityRepo flightReliabilityRepo,
                          FlightScoreService flightScoreService) {
        this.flightReliabilityRepo = flightReliabilityRepo;
        this.flightScoreService    = flightScoreService;
    }

    @GetMapping("/flights")
    public List<FlightReliability> getFlights() {
        return flightReliabilityRepo.findAll();
    }

    @GetMapping("/liveFlights")
    public String getLiveFlights(
            @RequestParam(defaultValue = "ATL") String dep,
            @RequestParam(defaultValue = "ORD") String arr,
            @RequestParam(defaultValue = "") String date) {
        return flightScoreService.scoreFlight(dep, arr, date);
    }
}