package com.example.flightreservationapp.activity.admin_fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.flightreservationapp.R;
import com.example.flightreservationapp.database.DataBaseHelper;
import com.example.flightreservationapp.model.Flight;
import com.example.flightreservationapp.utility.FlightAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class FlightFilterFragment extends Fragment {

    private EditText etDepartureDate, etArrivalDate; // EditText for departure and arrival dates
    private Spinner spDepartureCity, spArrivalCity; // Spinner for selecting departure and arrival cities
    private Button btnSearchFlights; // Button to initiate flight search
    private ListView lvFlightResults; // ListView to display search results
    private DataBaseHelper databaseHelper; // Database helper for querying flights
    private ArrayList<Flight> flightList; // List to hold filtered flights

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_flight_filter, container, false);

        // Initialize views
        spDepartureCity = view.findViewById(R.id.spDepartureCity);
        spArrivalCity = view.findViewById(R.id.spArrivalCity);
        etDepartureDate = view.findViewById(R.id.etDepartureDate);
        etArrivalDate = view.findViewById(R.id.etArrivalDate);
        btnSearchFlights = view.findViewById(R.id.btnSearchFlights);
        lvFlightResults = view.findViewById(R.id.lvFlightResults);

        // Set date pickers for departure and arrival date fields
        setDatePicker(etDepartureDate);
        setDatePicker(etArrivalDate);

        // Initialize database helper
        databaseHelper = new DataBaseHelper(getContext());

        // Set up search button click listener
        btnSearchFlights.setOnClickListener(v -> {
            // Retrieve user input
            String departureCity = spDepartureCity.getSelectedItem().toString().trim();
            String arrivalCity = spArrivalCity.getSelectedItem().toString().trim();
            String departureDate = etDepartureDate.getText().toString().trim();
            String arrivalDate = etArrivalDate.getText().toString().trim();

            // Check if all required fields are filled
            if (departureCity.isEmpty() || arrivalCity.isEmpty() || departureDate.isEmpty()) {
                Toast.makeText(getContext(), "Please enter all fields", Toast.LENGTH_SHORT).show();
            } else {
                // Perform flight search with the provided criteria
                searchFlights(departureCity, arrivalCity, departureDate, arrivalDate);
            }
        });

        // Set item click listener for flight results
        lvFlightResults.setOnItemClickListener((parent, view1, position, id) -> {
            // Show details dialog for selected flight
            showFlightDetailsDialog(flightList.get(position));
        });

        return view;
    }

    // Search flights based on provided criteria
    private void searchFlights(String departureCity, String arrivalCity, String departureDate, String arrivalDate) {
        // Retrieve all flights from the database
        Cursor cursor = databaseHelper.getAllFlights();
        flightList = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Retrieve flight details from the cursor
                String flightNumber = cursor.getString(cursor.getColumnIndexOrThrow("FLIGHT_NUMBER"));
                String depPlace = cursor.getString(cursor.getColumnIndexOrThrow("DEPARTURE_PLACE"));
                String destPlace = cursor.getString(cursor.getColumnIndexOrThrow("DESTINATION"));
                String depDate = cursor.getString(cursor.getColumnIndexOrThrow("DEPARTURE_DATE"));
                String destDate = cursor.getString(cursor.getColumnIndexOrThrow("DEPARTURE_DATE"));

                // Apply filtering based on user input
                if (depPlace.equalsIgnoreCase(departureCity) && destPlace.equalsIgnoreCase(arrivalCity)
                        && depDate.equals(departureDate) && destDate.equals(arrivalDate)) {
                    // Retrieve additional flight details
                    String aircraftModel = cursor.getString(cursor.getColumnIndexOrThrow("AIRCRAFT_MODEL"));
                    String departureTime = cursor.getString(cursor.getColumnIndexOrThrow("DEPARTURE_TIME"));
                    String arrivalTime = cursor.getString(cursor.getColumnIndexOrThrow("ARRIVAL_TIME"));
                    int duration = cursor.getInt(cursor.getColumnIndexOrThrow("DURATION"));
                    int maxSeats = cursor.getInt(cursor.getColumnIndexOrThrow("MAX_SEATS"));
                    int currentReservations = cursor.getInt(cursor.getColumnIndexOrThrow("CURRENT_RESERVATIONS"));
                    int missedFlights = cursor.getInt(cursor.getColumnIndexOrThrow("MISSED_FLIGHTS"));
                    String bookingOpenDate = cursor.getString(cursor.getColumnIndexOrThrow("BOOKING_OPEN_DATE"));
                    double economyClassPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("ECONOMY_CLASS_PRICE"));
                    double businessClassPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("BUSINESS_CLASS_PRICE"));
                    double extraBaggagePrice = cursor.getDouble(cursor.getColumnIndexOrThrow("EXTRA_BAGGAGE_PRICE"));
                    Flight.RecurrentType recurrent = Flight.RecurrentType.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("RECURRENT")));

                    // Create and add the flight object to the list
                    Flight flight = new Flight(destPlace, depDate, destDate, duration, flightNumber,
                            depPlace, departureTime, arrivalTime, aircraftModel, currentReservations,
                            maxSeats, missedFlights, bookingOpenDate, economyClassPrice, businessClassPrice,
                            extraBaggagePrice, recurrent);
                    flightList.add(flight);
                }
            } while (cursor.moveToNext());
            cursor.close(); // Close the cursor when done
        }

        // Set the adapter to the ListView to display the search results
        FlightAdapter adapter = new FlightAdapter(getContext(), flightList);
        lvFlightResults.setAdapter(adapter);

        // Show a message if no flights are found
        if (flightList.isEmpty()) {
            Toast.makeText(getContext(), "No flights found", Toast.LENGTH_SHORT).show();
        }
    }

    // Show a dialog with detailed information for the selected flight
    private void showFlightDetailsDialog(Flight flight) {
        String flightDetails = "Flight Number: " + flight.getFlightNumber() + "\n" +
                "Departure Place: " + flight.getDeparturePlace() + "\n" +
                "Destination: " + flight.getDestination() + "\n" +
                "Aircraft Model: " + flight.getAircraftModel() + "\n" +
                "Departure Date: " + flight.getDepartureDate() + "\n" +
                "Departure Time: " + flight.getDepartureTime() + "\n" +
                "Arrival Date: " + flight.getArrivalDate() + "\n" +
                "Arrival Time: " + flight.getArrivalTime() + "\n" +
                "Duration: " + flight.getDuration() + " minutes\n" +
                "Max Seats: " + flight.getMaxSeats() + "\n" +
                "Booking Open Date: " + flight.getBookingOpenDate() + "\n" +
                "Economy Class Price: " + flight.getEconomyClassPrice() + "\n" +
                "Business Class Price: " + flight.getBusinessClassPrice() + "\n" +
                "Extra Baggage Price: " + flight.getExtraBaggagePrice() + "\n" +
                "Recurrent: " + flight.getRecurrent() + "\n" +
                "Current Reservations: " + flight.getCurrentReservations() + "\n" +
                "Missed Flights: " + flight.getMissedFlights();

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Flight Details")
                .setMessage(flightDetails)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    // Set up a date picker for the specified EditText
    private void setDatePicker(EditText editText) {
        editText.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getContext(),
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        // Format the selected date
                        String formattedDate = String.format("%04d-%02d-%02d", year1, monthOfYear + 1, dayOfMonth);

                        // Validate the selected date
                        if (editText == etDepartureDate) {
                            if (!isDateValid(formattedDate)) {
                                Toast.makeText(getContext(), "Date cannot be earlier than today", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }

                        // Validate arrival date against departure date
                        if (editText == etArrivalDate) {
                            String departureDate = etDepartureDate.getText().toString().trim();
                            if (!departureDate.isEmpty() && !isArrivalDateValid(departureDate, formattedDate)) {
                                Toast.makeText(getContext(), "Arrival date must be later than departure date", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }

                        // Validate departure date against arrival date
                        if (editText == etDepartureDate) {
                            String arrivalDate = etArrivalDate.getText().toString().trim();
                            if (!arrivalDate.isEmpty() && !isArrivalDateValid(formattedDate, arrivalDate)) {
                                Toast.makeText(getContext(), "Departure date cannot be later than arrival date", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        // Set the selected date to the EditText
                        editText.setText(formattedDate);
                    },
                    year, month, day);
            datePickerDialog.show();
        });
    }

    // Check if the selected date is valid (not earlier than today)
    private boolean isDateValid(String selectedDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = format.parse(selectedDate);
            Date currentDate = new Date();
            return !date.before(currentDate); // Date should not be before today
        } catch (ParseException e) {
            return false;
        }
    }

    // Check if the arrival date is valid (not earlier than departure date)
    private boolean isArrivalDateValid(String departureDate, String arrivalDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date depDate = format.parse(departureDate);
            Date arrDate = format.parse(arrivalDate);
            return !arrDate.before(depDate); // Arrival date should not be before departure date
        } catch (ParseException e) {
            return false;
        }
    }
}
