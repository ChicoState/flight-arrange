package com.FlightArrange.FlightArrange;

import com.FlightArrange.FlightArrange.model.FlightReliability;
import com.FlightArrange.FlightArrange.repository.FlightReliabilityRepo;
import com.FlightArrange.FlightArrange.service.AviationStackService;
import com.FlightArrange.FlightArrange.service.FlightScoreService;
import org.springframework.web.bind.annotation.*;

import com.FlightArrange.FlightArrange.model.*;
import com.FlightArrange.FlightArrange.repository.*;
import com.FlightArrange.FlightArrange.service.UserService;

import java.util.List;

@RestController
public class HomeController {

    private final FlightReliabilityRepo flightReliabilityRepo;
    private final AviationStackService aviationStackService;
    private final FlightScoreService flightScoreService;

    private final UserService userService;
    private final UserRepository userRepository;

    public HomeController(FlightReliabilityRepo flightReliabilityRepo,
                          AviationStackService aviationStackService,
                          FlightScoreService flightScoreService,
                          UserService userService,
                          UserRepository userRepository) {

        this.flightReliabilityRepo = flightReliabilityRepo;
        this.aviationStackService  = aviationStackService;
        this.flightScoreService    = flightScoreService;
        this.userService           = userService;
        this.userRepository        = userRepository;
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

    @PostMapping("/api/users")
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    @PostMapping("/api/users/{userId}/saveFlight")
    public SavedFlight saveFlight(
        @PathVariable Long userId,
        @RequestBody SavedFlight flight) {

        return userService.saveFlight(userId, flight);
    }

    @GetMapping("/api/users/{userId}/flights")
    public List<SavedFlight> getSavedFlights(@PathVariable Long userId) {

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getSavedFlights();
    }
}