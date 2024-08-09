package com.example.flightreservationapp.utility;

import com.example.flightreservationapp.model.Flight;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FlightJsonParser {

    public static List<Flight> getObjectFromJson(String json) {
        List<Flight> flights = new ArrayList<>();
        if (json == null || json.isEmpty()) {
            return flights; // Return empty list if JSON is null or empty
        }

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject flightObject = jsonArray.getJSONObject(i);
                Flight flight = new Flight(
                        flightObject.optString("flightNumber", "Unknown"),
                        flightObject.optString("departurePlace", "Unknown"),
                        flightObject.optString("destination", "Unknown"),
                        new Date(flightObject.optLong("departureDate", 0)),
                        new Date(flightObject.optLong("departureTime", 0)),
                        new Date(flightObject.optLong("arrivalDate", 0)),
                        new Date(flightObject.optLong("arrivalTime", 0)),
                        flightObject.optInt("duration", 0),
                        flightObject.optString("aircraftModel", "Unknown"),
                        flightObject.optInt("maxSeats", 0),
                        new Date(flightObject.optLong("bookingOpenDate", 0)),
                        flightObject.optDouble("economyClassPrice", 0.0),
                        flightObject.optDouble("businessClassPrice", 0.0),
                        flightObject.optDouble("extraBaggagePrice", 0.0),
                        Flight.RecurrentType.valueOf(flightObject.optString("recurrent", "NONE"))
                );

                flight.setCurrentReservations(flightObject.optInt("currentReservations", 0));
                flight.setMissedFlights(flightObject.optInt("missedFlights", 0));

                flights.add(flight);
            }
        } catch (JSONException e) {
            e.printStackTrace(); // Log error message
        }
        return flights;
    }
}
