package com.FlightArrange.FlightArrange.model;

import jakarta.persistence.*;

@Entity
@Table(name = "flight_reliability")
public class FlightReliability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int year;
    private int month;
    private String carrier;
    private String carrierName;
    private String airport;
    private String airportName;
    private double arrFlights;
    private double arrDel15;
    private double arrCancelled;
    private double onTimeRate; // we calculate this on load

    public FlightReliability() {}

    // Getters and Setters
    public Long getId() { return id; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }
    public String getCarrier() { return carrier; }
    public void setCarrier(String carrier) { this.carrier = carrier; }
    public String getCarrierName() { return carrierName; }
    public void setCarrierName(String carrierName) { this.carrierName = carrierName; }
    public String getAirport() { return airport; }
    public void setAirport(String airport) { this.airport = airport; }
    public String getAirportName() { return airportName; }
    public void setAirportName(String airportName) { this.airportName = airportName; }
    public double getArrFlights() { return arrFlights; }
    public void setArrFlights(double arrFlights) { this.arrFlights = arrFlights; }
    public double getArrDel15() { return arrDel15; }
    public void setArrDel15(double arrDel15) { this.arrDel15 = arrDel15; }
    public double getArrCancelled() { return arrCancelled; }
    public void setArrCancelled(double arrCancelled) { this.arrCancelled = arrCancelled; }
    public double getOnTimeRate() { return onTimeRate; }
    public void setOnTimeRate(double onTimeRate) { this.onTimeRate = onTimeRate; }
}