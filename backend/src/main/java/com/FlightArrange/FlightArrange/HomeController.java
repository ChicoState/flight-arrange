package com.FlightArrange.FlightArrange;

import com.FlightArrange.FlightArrange.model.Flight;
import com.FlightArrange.FlightArrange.repository.FlightRepo;
import org.springframework.web.bind.annotation.*;
import com.FlightArrange.FlightArrange.service.AviationStackService;

import java.util.List;

@RestController
public class HomeController {

    private final FlightRepo flightRepo;
    private final AviationStackService aviationStackService;  

    public HomeController(FlightRepo flightRepo, AviationStackService aviationStackService) {  
        this.flightRepo = flightRepo;
        this.aviationStackService = aviationStackService;  
    }

    @GetMapping("/api/flights")
    public List<Flight> getFlights() {
        return flightRepo.findAll();
    }

    @GetMapping("/api/liveFlights")
    public String getLiveFlights(
            @RequestParam(defaultValue = "LAX") String dep,
            @RequestParam(defaultValue = "JFK") String arr) {
        return aviationStackService.getFlights(dep, arr);
    }
}