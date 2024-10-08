package com.example.flightreservationapp.model;


import java.io.Serializable;

public class Reservation implements Serializable{

    private String reservationId;
    private String passportNumber;
    private String flightNumber;
    private String flightClass;
    private int extraBags;
    private double totalCost;

    // Constructor
    public Reservation(String reservationId, String passportNumber, String flightNumber, String flightClass, int extraBags, double totalCost) {
        this.reservationId = reservationId;
        this.passportNumber = passportNumber;
        this.flightNumber = flightNumber;
        this.flightClass = flightClass;
        this.extraBags = extraBags;
        this.totalCost = totalCost;
    }

    // Getters and Setters
    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getFlightClass() {
        return flightClass;
    }

    public void setFlightClass(String flightClass) {
        this.flightClass = flightClass;
    }

    public int getExtraBags() {
        return extraBags;
    }

    public void setExtraBags(int extraBags) {
        this.extraBags = extraBags;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }
}
