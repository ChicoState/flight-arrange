package com.FlightArrange.FlightArrange.repository;

import com.FlightArrange.FlightArrange.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}