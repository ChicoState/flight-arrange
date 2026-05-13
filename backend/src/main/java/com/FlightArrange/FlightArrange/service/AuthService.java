package com.FlightArrange.FlightArrange.service;

import com.FlightArrange.FlightArrange.model.User;
import com.FlightArrange.FlightArrange.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    public User register(User user) {
            System.out.println("Registering user: " + user.getUsername());

        return userRepository.save(user);
    }
    
    public Optional<User> login(String username, String password) {
        return userRepository.findByUsername(username)
            .filter(u -> u.getPassword().equals(password));
    }
}