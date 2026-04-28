package com.FlightArrange.FlightArrange.service;

import com.FlightArrange.FlightArrange.model.*;
import com.FlightArrange.FlightArrange.repository.*;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepo;
    private final SavedFlightRepository savedFlightRepo;

    public UserService(UserRepository userRepo,
                       SavedFlightRepository savedFlightRepo) {
        this.userRepo = userRepo;
        this.savedFlightRepo = savedFlightRepo;
    }

    public SavedFlight saveFlight(Long userId, SavedFlight flight) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        flight.setUser(user);
        return savedFlightRepo.save(flight);
    }
}