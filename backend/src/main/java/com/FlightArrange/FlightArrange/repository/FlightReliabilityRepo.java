package com.FlightArrange.FlightArrange.repository;

import com.FlightArrange.FlightArrange.model.FlightReliability;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FlightReliabilityRepo extends JpaRepository<FlightReliability, Long> {
    List<FlightReliability> findByCarrierAndAirport(String carrier, String airport);
}