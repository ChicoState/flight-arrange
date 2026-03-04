package com.FlightArrange.FlightArrange.repository;

import com.FlightArrange.FlightArrange.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlightRepo extends JpaRepository<Flight, Long> {
}