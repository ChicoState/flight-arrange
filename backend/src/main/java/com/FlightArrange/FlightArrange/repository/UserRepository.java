package com.FlightArrange.FlightArrange.repository;

import com.FlightArrange.FlightArrange.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}