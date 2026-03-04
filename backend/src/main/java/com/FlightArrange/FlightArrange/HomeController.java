package com.FlightArrange.FlightArrange;

import com.FlightArrange.FlightArrange.model.Flight;
import com.FlightArrange.FlightArrange.repository.FlightRepo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class HomeController {

    private final FlightRepo flightRepo;

    public HomeController(FlightRepo flightRepo) {
        this.flightRepo = flightRepo;
    }

    @GetMapping("/api/flights")
    public List<Flight> getFlights() {
        return flightRepo.findAll();
    }
}