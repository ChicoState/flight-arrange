package com.FlightArrange.FlightArrange.model;

import java.util.List;

public class TripRequest {
    private String username;
    private String dep;
    private String arr;
    private boolean oneWay;
    private String dateFrom;
    private String dateTo;
    private String outboundTiming;
    private String returnTiming;
    private Object ratings;
    private List<Object> companions;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getDep() { return dep; }
    public void setDep(String dep) { this.dep = dep; }
    public String getArr() { return arr; }
    public void setArr(String arr) { this.arr = arr; }
    public boolean isOneWay() { return oneWay; }
    public void setOneWay(boolean oneWay) { this.oneWay = oneWay; }
    public String getDateFrom() { return dateFrom; }
    public void setDateFrom(String dateFrom) { this.dateFrom = dateFrom; }
    public String getDateTo() { return dateTo; }
    public void setDateTo(String dateTo) { this.dateTo = dateTo; }
    public String getOutboundTiming() { return outboundTiming; }
    public void setOutboundTiming(String outboundTiming) { this.outboundTiming = outboundTiming; }
    public String getReturnTiming() { return returnTiming; }
    public void setReturnTiming(String returnTiming) { this.returnTiming = returnTiming; }
    public Object getRatings() { return ratings; }
    public void setRatings(Object ratings) { this.ratings = ratings; }
    public List<Object> getCompanions() { return companions; }
    public void setCompanions(List<Object> companions) { this.companions = companions; }
}