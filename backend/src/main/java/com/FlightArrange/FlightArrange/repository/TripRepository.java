package com.FlightArrange.FlightArrange.repository;

import com.FlightArrange.FlightArrange.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findByUsername(String username);
}