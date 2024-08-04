package com.example.flightreservationapp.service;


import java.util.*;
import com.example.flightreservationapp.model.Flight;


public class APIService {

    public static List<Flight> getFlights(boolean flag) throws Exception{
        List<Flight> flights = new ArrayList<>();

        // Populate the list with sample flights
        for (int i = 0; i < 10; i++) {
            String flightNumber = "FL" + (100 + i);
            String departurePlace = "City" + i;
            String destination = "Destination" + i;
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, i);
            Date departureDate = calendar.getTime();
            Date departureTime = calendar.getTime();
            calendar.add(Calendar.HOUR_OF_DAY, 3); // Add 3 hours for arrival
            Date arrivalDate = calendar.getTime();
            Date arrivalTime = calendar.getTime();
            int duration = 180 + i; // in minutes
            String aircraftModel = "AIRBUS A320 JET";
            int maxSeats = 180 + i;
            Date bookingOpenDate = new Date(); // Current date
            double economyClassPrice = 150.0 + i;
            double businessClassPrice = 500.0 + i;
            double extraBaggagePrice = 50.0 + i;
            Flight.RecurrentType recurrent = Flight.RecurrentType.NONE;

            Flight flight = new Flight(flightNumber, departurePlace, destination, departureDate, departureTime,
                    arrivalDate, arrivalTime, duration, aircraftModel, maxSeats, bookingOpenDate, economyClassPrice,
                    businessClassPrice, extraBaggagePrice, recurrent);
            flights.add(flight);
        }

        if (flag)
            throw new IllegalArgumentException("No data found");

        return flights;
    }

}
