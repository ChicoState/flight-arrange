package com.FlightArrange.FlightArrange.controller;

import com.FlightArrange.FlightArrange.model.Trip;
import com.FlightArrange.FlightArrange.model.TripRequest;
import com.FlightArrange.FlightArrange.repository.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class TripController {

    @Autowired
    private TripRepository tripRepository;

    @PostMapping("/trips")
    public ResponseEntity<?> saveTrip(@RequestBody TripRequest body) {
        Trip trip = new Trip();
        trip.setUsername(body.getUsername());
        trip.setDep(body.getDep());
        trip.setArr(body.getArr());
        trip.setOneWay(body.isOneWay());
        trip.setDateFrom(body.getDateFrom());
        trip.setDateTo(body.getDateTo());
        trip.setOutboundTiming(body.getOutboundTiming());
        trip.setReturnTiming(body.getReturnTiming());
        trip.setRatings(body.getRatings().toString());
        trip.setCompanions(body.getCompanions() != null ? body.getCompanions().toString() : null);

        tripRepository.save(trip);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/trips")
    public ResponseEntity<List<Trip>> getTrips(@RequestParam String username) {
        return ResponseEntity.ok(tripRepository.findByUsername(username));
    }
}