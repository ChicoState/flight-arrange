package com.FlightArrange.FlightArrange;

import com.FlightArrange.FlightArrange.model.FlightReliability;
import com.FlightArrange.FlightArrange.repository.FlightReliabilityRepo;
import com.FlightArrange.FlightArrange.service.AviationStackService;
import com.FlightArrange.FlightArrange.service.FlightScoreService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
<<<<<<< HEAD
=======
@RequestMapping("/api")
>>>>>>> merged_branch-5-11-26
public class HomeController {

    private final FlightReliabilityRepo flightReliabilityRepo;
    private final AviationStackService aviationStackService;
    private final FlightScoreService flightScoreService;

<<<<<<< HEAD
    public HomeController(FlightReliabilityRepo flightReliabilityRepo,
                          AviationStackService aviationStackService,
                          FlightScoreService flightScoreService) {
        this.flightReliabilityRepo = flightReliabilityRepo;
        this.aviationStackService  = aviationStackService;
        this.flightScoreService    = flightScoreService;
    }

    // Returns all historical BTS data
    @GetMapping("/api/flights")
=======
    public HomeController(
            FlightReliabilityRepo flightReliabilityRepo,
            AviationStackService aviationStackService,
            FlightScoreService flightScoreService
    ) {
        this.flightReliabilityRepo = flightReliabilityRepo;
        this.aviationStackService = aviationStackService;
        this.flightScoreService = flightScoreService;
    }

    @GetMapping("/flights")
>>>>>>> merged_branch-5-11-26
    public List<FlightReliability> getFlights() {
        return flightReliabilityRepo.findAll();
    }

<<<<<<< HEAD
    // Returns live flights enriched with price, on-time rate and score
    @GetMapping("/api/liveFlights")
    public String getLiveFlights(
            @RequestParam(defaultValue = "LAX") String dep,
            @RequestParam(defaultValue = "JFK") String arr) {
=======
    @GetMapping("/liveFlights")
    public Object getLiveFlights(
            @RequestParam(defaultValue = "LAX") String dep,
            @RequestParam(defaultValue = "JFK") String arr
    ) {
>>>>>>> merged_branch-5-11-26
        String rawFlights = aviationStackService.getFlights(dep, arr);
        return flightScoreService.scoreFlight(rawFlights);
    }
}