package com.FlightArrange.FlightArrange;

import com.FlightArrange.FlightArrange.model.FlightReliability;
import com.FlightArrange.FlightArrange.repository.FlightReliabilityRepo;
import com.FlightArrange.FlightArrange.service.FlightScoreService;
import org.springframework.web.bind.annotation.*;
import com.FlightArrange.FlightArrange.model.*;
import com.FlightArrange.FlightArrange.repository.*;
import com.FlightArrange.FlightArrange.service.UserService;
import java.util.List;

@RestController
@RequestMapping("/api")
public class HomeController {

    private final FlightReliabilityRepo flightReliabilityRepo;
    private final FlightScoreService flightScoreService;
    private final UserService userService;
    private final UserRepository userRepository;

    public HomeController(FlightReliabilityRepo flightReliabilityRepo,
                          FlightScoreService flightScoreService,
                          UserService userService,
                          UserRepository userRepository) {
        this.flightReliabilityRepo = flightReliabilityRepo;
        this.flightScoreService    = flightScoreService;
        this.userService           = userService;
        this.userRepository        = userRepository;
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

    @PostMapping("/users")
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    @PostMapping("/users/{userId}/saveFlight")
    public SavedFlight saveFlight(
        @PathVariable Long userId,
        @RequestBody SavedFlight flight) {
        return userService.saveFlight(userId, flight);
    }

    @GetMapping("/users/{userId}/flights")
    public List<SavedFlight> getSavedFlights(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getSavedFlights();
    }
}