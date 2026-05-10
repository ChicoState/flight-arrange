package com.FlightArrange.FlightArrange.controller;

import com.FlightArrange.FlightArrange.model.User;
import com.FlightArrange.FlightArrange.service.AuthService;
import com.FlightArrange.FlightArrange.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        return ResponseEntity.ok(authService.register(user));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        return authService.login(user.getUsername(), user.getPassword())
            .map(u -> ResponseEntity.ok().body("Login successful"))
            .orElse(ResponseEntity.status(401).body("Invalid credentials"));
    }

    // updates current user information when editing accoutn info
    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestBody User user) {
        return userRepository.findByUsername(user.getUsername())
            .map(existing -> {
                existing.setEmail(user.getEmail());
                existing.setPhoneNumber(user.getPhoneNumber());
                if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                    existing.setPassword(user.getPassword());
                }
                return ResponseEntity.ok(userRepository.save(existing));
            })
            .orElse(ResponseEntity.status(404).body(null));
    }

    // gets the users sutff when its time to edit
    @GetMapping("/user/{username}")
    public ResponseEntity<?> getUser(@PathVariable String username) {
        return userRepository.findByUsername(username)
            .map(u -> ResponseEntity.ok(u))
            .orElse(ResponseEntity.status(404).body(null));
    }
}