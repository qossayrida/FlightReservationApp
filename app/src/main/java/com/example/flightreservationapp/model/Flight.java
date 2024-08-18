package com.example.flightreservationapp.model;

import java.io.Serializable;
import java.util.Date;

public class Flight implements Serializable {
    private String flightNumber;
    private String departurePlace;
    private String destination;
    private String departureDate;
    private String departureTime;
    private String arrivalDate;
    private String arrivalTime;
    private int duration; // duration in minutes
    private String aircraftModel;
    private int currentReservations;
    private int maxSeats;
    private int missedFlights;
    private String bookingOpenDate;
    private double economyClassPrice;
    private double businessClassPrice;
    private double extraBaggagePrice;
    private RecurrentType recurrent;

    public enum RecurrentType {
        NONE,
        DAILY,
        WEEKLY
    }

    // Constructor
    public Flight(String flightNumber, String departurePlace, String destination, String departureDate,
                  String departureTime, String arrivalDate, String arrivalTime, int duration, String aircraftModel,
                  int maxSeats, String bookingOpenDate, double economyClassPrice, double businessClassPrice,
                  double extraBaggagePrice, RecurrentType recurrent) {
        this.flightNumber = flightNumber;
        this.departurePlace = departurePlace;
        this.destination = destination;
        this.departureDate = departureDate;
        this.departureTime = departureTime;
        this.arrivalDate = arrivalDate;
        this.arrivalTime = arrivalTime;
        this.duration = duration;
        this.aircraftModel = aircraftModel;
        this.currentReservations = 0;
        this.maxSeats = maxSeats;
        this.missedFlights = 0;
        this.bookingOpenDate = bookingOpenDate;
        this.economyClassPrice = economyClassPrice;
        this.businessClassPrice = businessClassPrice;
        this.extraBaggagePrice = extraBaggagePrice;
        this.recurrent = recurrent;
    }

    public Flight(String flightNumber, String departurePlace, String destination,String aircraftModel) {
        this.aircraftModel = aircraftModel;
        this.flightNumber = flightNumber;
        this.departurePlace = departurePlace;
        this.destination = destination;
    }

    // Getters and Setters
    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getDeparturePlace() {
        return departurePlace;
    }

    public void setDeparturePlace(String departurePlace) {
        this.departurePlace = departurePlace;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public String getArrivalDate() {
        return arrivalDate;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public String getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(String departureDate) {
        this.departureDate = departureDate;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public void setArrivalDate(String arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getAircraftModel() {
        return aircraftModel;
    }

    public void setAircraftModel(String aircraftModel) {
        this.aircraftModel = aircraftModel;
    }

    public int getCurrentReservations() {
        return currentReservations;
    }

    public void setCurrentReservations(int currentReservations) {
        this.currentReservations = currentReservations;
    }

    public int getMaxSeats() {
        return maxSeats;
    }

    public void setMaxSeats(int maxSeats) {
        this.maxSeats = maxSeats;
    }

    public int getMissedFlights() {
        return missedFlights;
    }

    public void setMissedFlights(int missedFlights) {
        this.missedFlights = missedFlights;
    }

    public String getBookingOpenDate() {
        return bookingOpenDate;
    }

    public void setBookingOpenDate(String bookingOpenDate) {
        this.bookingOpenDate = bookingOpenDate;
    }

    public double getEconomyClassPrice() {
        return economyClassPrice;
    }

    public void setEconomyClassPrice(double economyClassPrice) {
        this.economyClassPrice = economyClassPrice;
    }

    public double getBusinessClassPrice() {
        return businessClassPrice;
    }

    public void setBusinessClassPrice(double businessClassPrice) {
        this.businessClassPrice = businessClassPrice;
    }

    public double getExtraBaggagePrice() {
        return extraBaggagePrice;
    }

    public void setExtraBaggagePrice(double extraBaggagePrice) {
        this.extraBaggagePrice = extraBaggagePrice;
    }

    public RecurrentType getRecurrent() {
        return recurrent;
    }

    public void setRecurrent(RecurrentType recurrent) {
        this.recurrent = recurrent;
    }

    // Method to update reservations
    public void updateReservations(int delta) {
        this.currentReservations += delta;
    }

    // Method to update missed flights
    public void updateMissedFlights(int delta) {
        this.missedFlights += delta;
    }

    @Override
    public String toString() {
        return "Flight{" +
                "flightNumber='" + flightNumber + '\'' +
                ", departurePlace='" + departurePlace + '\'' +
                ", destination='" + destination + '\'' +
                ", departureDate=" + departureDate +
                ", departureTime=" + departureTime +
                ", arrivalDate=" + arrivalDate +
                ", arrivalTime=" + arrivalTime +
                ", duration=" + duration +
                ", aircraftModel='" + aircraftModel + '\'' +
                ", currentReservations=" + currentReservations +
                ", maxSeats=" + maxSeats +
                ", missedFlights=" + missedFlights +
                ", bookingOpenDate=" + bookingOpenDate +
                ", economyClassPrice=" + economyClassPrice +
                ", businessClassPrice=" + businessClassPrice +
                ", extraBaggagePrice=" + extraBaggagePrice +
                ", recurrent=" + recurrent +
                '}';
    }
}
