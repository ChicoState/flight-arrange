package com.FlightArrange.FlightArrange.model;

import jakarta.persistence.*;

@Entity
public class SavedFlight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String flight;
    private String airline;
    private String departure;
    private String arrival;
    private String departureTime;
    private String arrivalTime;

    private double price;
    private double score;
    private double onTimeRate;
    private double durationMins;

    @ManyToOne
    private User user;

    public SavedFlight() {}

    //Getters
    public Long getId() { return id; }
    public String getFlight() { return flight; }
    public String getAirline() { return airline; }
    public String getDeparture() { return departure; }
    public String getArrival() { return arrival; }
    public String getDepartureTime() { return departureTime; }
    public String getArrivalTime() { return arrivalTime; }

    public double getPrice() { return price; }
    public double getScore() { return score; }
    public double getOnTimeRate() { return onTimeRate; }
    public double getDurationMins() { return durationMins; }

    //Setters
    public void setFlight(String flight) { this.flight = flight; }
    public void setAirline(String airline) { this.airline = airline; }
    public void setDeparture(String departure) { this.departure = departure; }
    public void setArrival(String arrival) { this.arrival = arrival; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }
    public void setArrivalTime(String arrivalTime) { this.arrivalTime = arrivalTime; }

    public void setPrice(double price) { this.price = price; }
    public void setScore(double score) { this.score = score; }
    public void setOnTimeRate(double onTimeRate) { this.onTimeRate = onTimeRate; }
    public void setDurationMins(double durationMins) { this.durationMins = durationMins; }

    public void setUser(User user) { this.user = user; }
}