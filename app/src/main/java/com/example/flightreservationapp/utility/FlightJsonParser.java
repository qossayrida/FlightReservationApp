package com.example.flightreservationapp.utility;

import android.util.Log;
import com.example.flightreservationapp.model.Flight;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FlightJsonParser {

    private static final String TAG = "FlightJsonParser";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm", Locale.getDefault());

    public static List<Flight> getObjectFromJson(String json) {
        List<Flight> flights = new ArrayList<>();
        if (json == null || json.isEmpty()) {
            Log.e(TAG, "JSON string is null or empty");
            return flights; // Return empty list if JSON is null or empty
        }

        try {
            JSONArray jsonArray = new JSONArray(json);  // Parse JSON as an array
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject flightObject = jsonArray.getJSONObject(i);

                // Parsing date and time
                Date departureDate = parseDate(flightObject, "departureDate");
                Date departureTime = parseTime(flightObject, "departureTime");
                Date arrivalDate = parseDate(flightObject, "arrivalDate");
                Date arrivalTime = parseTime(flightObject, "arrivalTime");

                // Combine date and time if needed
                Date fullDepartureDateTime = combineDateAndTime(departureDate, departureTime);
                Date fullArrivalDateTime = combineDateAndTime(arrivalDate, arrivalTime);

                Flight flight = new Flight(
                        flightObject.optString("flightNumber", "Unknown"),
                        flightObject.optString("departurePlace", "Unknown"),
                        flightObject.optString("destination", "Unknown"),
                        fullDepartureDateTime,
                        fullDepartureDateTime,
                        fullArrivalDateTime,
                        fullArrivalDateTime,
                        flightObject.optInt("duration", 0),
                        flightObject.optString("aircraftModel", "Unknown"),
                        flightObject.optInt("maxSeats", 0),
                        parseDate(flightObject, "bookingOpenDate"),
                        flightObject.optDouble("economyClassPrice", 0.0),
                        flightObject.optDouble("businessClassPrice", 0.0),
                        flightObject.optDouble("extraBaggagePrice", 0.0),
                        parseRecurrentType(flightObject.optString("recurrent", "NONE"))
                );

                flight.setCurrentReservations(flightObject.optInt("currentReservations", 0));
                flight.setMissedFlights(flightObject.optInt("missedFlights", 0));

                flights.add(flight);
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSON parsing error: " + e.getMessage());
        }

        Log.d(TAG, "Parsed flights: " + flights.toString());
        return flights;
    }

    private static Date parseDate(JSONObject jsonObject, String key) {
        String dateString = jsonObject.optString(key, null);
        if (dateString != null) {
            try {
                return DATE_FORMAT.parse(dateString);
            } catch (ParseException e) {
                Log.e(TAG, "Date parsing error for key " + key + ": " + e.getMessage());
            }
        }
        return null;
    }

    private static Date parseTime(JSONObject jsonObject, String key) {
        String timeString = jsonObject.optString(key, null);
        if (timeString != null) {
            try {
                return TIME_FORMAT.parse(timeString);
            } catch (ParseException e) {
                Log.e(TAG, "Time parsing error for key " + key + ": " + e.getMessage());
            }
        }
        return null;
    }

    private static Date combineDateAndTime(Date date, Date time) {
        if (date == null || time == null) {
            return date;
        }
        long combinedTime = date.getTime() + (time.getTime() % (24 * 60 * 60 * 1000)); // Combine date with time of day
        return new Date(combinedTime);
    }

    private static Flight.RecurrentType parseRecurrentType(String recurrentString) {
        try {
            return Flight.RecurrentType.valueOf(recurrentString);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Invalid recurrent type: " + recurrentString);
            return Flight.RecurrentType.NONE;
        }
    }
}
