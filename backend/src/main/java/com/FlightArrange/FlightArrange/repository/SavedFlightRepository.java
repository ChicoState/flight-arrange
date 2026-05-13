package com.FlightArrange.FlightArrange.repository;

import com.FlightArrange.FlightArrange.model.SavedFlight;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SavedFlightRepository extends JpaRepository<SavedFlight, Long> {
}