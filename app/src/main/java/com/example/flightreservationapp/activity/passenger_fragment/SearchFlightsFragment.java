package com.example.flightreservationapp.activity.passenger_fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.flightreservationapp.R;
import com.example.flightreservationapp.database.DataBaseHelper;
import com.example.flightreservationapp.model.Flight;
import com.example.flightreservationapp.model.User;
import com.example.flightreservationapp.utility.FlightAdapter;
import com.example.flightreservationapp.utility.JsonConverter;
import com.example.flightreservationapp.utility.SharedPrefManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SearchFlightsFragment extends Fragment {

    // UI elements
    private EditText etDepartureDate, etReturnDate;
    private TextView tvReturnDateLabel;
    private RadioGroup rgTripType;
    private Spinner spDepartureCity, spArrivalCity, spSorting;
    private Button btnSearch;
    private ListView lvOneWayFlightResults;
    private ListView lvReturnWayFlightResults;

    // Database helper
    private DataBaseHelper dbHelper;

    // Flight lists for storing search results
    private ArrayList<Flight> oneWayFlightList, returnWayFlightList;
    private User savedUser = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_flights, container, false);

        // Initialize UI elements
        spDepartureCity = view.findViewById(R.id.sp_departure_city);
        spArrivalCity = view.findViewById(R.id.sp_arrival_city);
        etDepartureDate = view.findViewById(R.id.et_departure_date);
        etReturnDate = view.findViewById(R.id.et_return_date);
        tvReturnDateLabel = view.findViewById(R.id.tv_return_date_label);
        rgTripType = view.findViewById(R.id.rg_trip_type);
        spSorting = view.findViewById(R.id.sp_sorting);
        btnSearch = view.findViewById(R.id.btn_search);
        lvOneWayFlightResults = view.findViewById(R.id.lv_one_way_flight_results);
        lvReturnWayFlightResults = view.findViewById(R.id.lv_Return_way_flight_results);

        // Set up date pickers for departure and return dates
        setDatePicker(etDepartureDate);
        setDatePicker(etReturnDate);

        // Initialize database helper and flight lists
        dbHelper = new DataBaseHelper(getContext());
        oneWayFlightList = new ArrayList<>();
        returnWayFlightList = new ArrayList<>();

        // Show/hide return date fields based on selected trip type
        rgTripType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_round_trip) {
                etReturnDate.setVisibility(View.VISIBLE);
                tvReturnDateLabel.setVisibility(View.VISIBLE);
                setFullHeight();
            } else {
                etReturnDate.setVisibility(View.GONE);
                tvReturnDateLabel.setVisibility(View.GONE);
                DivideHeightInHalf();
            }
        });

        // Set up search button click listener
        btnSearch.setOnClickListener(v -> searchFlights());

        // Fetch the current user from shared preferences
        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(requireContext());
        String savedUserJson = sharedPrefManager.readString("userJson", null);
        if (savedUserJson != null)
            savedUser = JsonConverter.jsonToUser(savedUserJson);

        return view;
    }

    // Adjust height of ListView for one-way flights
    private void DivideHeightInHalf() {
        View parent = (View) lvOneWayFlightResults.getParent();

        if (parent instanceof FrameLayout) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
            );
            lvOneWayFlightResults.setLayoutParams(params);
        } else if (parent instanceof LinearLayout) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
            );
            lvOneWayFlightResults.setLayoutParams(params);
        }
    }

    // Set ListView height to full size for round trip
    private void setFullHeight() {
        View parent = (View) lvOneWayFlightResults.getParent();

        if (parent instanceof FrameLayout) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    240
            );
            lvOneWayFlightResults.setLayoutParams(params);
        } else if (parent instanceof LinearLayout) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    240
            );
            lvOneWayFlightResults.setLayoutParams(params);
        }
    }

    // Perform flight search based on user input
    private void searchFlights() {
        String departureCity = spDepartureCity.getSelectedItem().toString().trim();
        String arrivalCity = spArrivalCity.getSelectedItem().toString().trim();
        String departureDate = etDepartureDate.getText().toString().trim();
        String returnDate = etReturnDate.getText().toString().trim();
        String sortingOption = spSorting.getSelectedItem().toString();

        int selectedTripType = rgTripType.getCheckedRadioButtonId();
        if (selectedTripType == R.id.rb_one_way) {
            returnDate = "";
        } else {
            // Search return flights
            Cursor cursor = dbHelper.searchReturnFlights(departureCity, arrivalCity, returnDate, sortingOption, savedUser.getPassportNumber());

            if (cursor != null && cursor.moveToFirst()) {
                returnWayFlightList.clear(); // Clear previous results

                do {
                    // Extract flight details from cursor
                    String flightNumber = cursor.getString(cursor.getColumnIndexOrThrow("FLIGHT_NUMBER"));
                    String departurePlace = cursor.getString(cursor.getColumnIndexOrThrow("DEPARTURE_PLACE"));
                    String destination = cursor.getString(cursor.getColumnIndexOrThrow("DESTINATION"));
                    String aircraftModel = cursor.getString(cursor.getColumnIndexOrThrow("AIRCRAFT_MODEL"));
                    String departureDateObj = cursor.getString(cursor.getColumnIndexOrThrow("DEPARTURE_DATE"));
                    String departureTime = cursor.getString(cursor.getColumnIndexOrThrow("DEPARTURE_TIME"));
                    String arrivalDate = cursor.getString(cursor.getColumnIndexOrThrow("ARRIVAL_DATE"));
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

                    // Create Flight object and add to list
                    Flight flight = new Flight(destination, departureDate, arrivalDate,
                            duration, flightNumber, departurePlace, departureTime, arrivalTime,
                            aircraftModel, currentReservations, maxSeats, missedFlights, bookingOpenDate,
                            economyClassPrice, businessClassPrice, extraBaggagePrice, recurrent);
                    returnWayFlightList.add(flight);

                } while (cursor.moveToNext());

                // Set adapter for return flight results
                FlightAdapter adapter = new FlightAdapter(getContext(), returnWayFlightList);
                lvReturnWayFlightResults.setAdapter(adapter);

                // Set item click listener for return flights
                lvReturnWayFlightResults.setOnItemClickListener((parent, view, position, id) -> showFlightDetailsDialog(returnWayFlightList.get(position)));
            } else {
                returnWayFlightList.clear(); // Clear list if no results found
                Toast.makeText(getContext(), "No flights found to return for the specified criteria", Toast.LENGTH_SHORT).show();

                // Update ListView to reflect cleared list
                FlightAdapter adapter = new FlightAdapter(getContext(), returnWayFlightList);
                lvReturnWayFlightResults.setAdapter(adapter);
            }
        }

        // Validate required fields
        if (TextUtils.isEmpty(departureCity) || TextUtils.isEmpty(arrivalCity) || TextUtils.isEmpty(departureDate)) {
            Toast.makeText(getContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Perform database query for one-way flights
        Cursor cursor = dbHelper.searchFlights(departureCity, arrivalCity, departureDate, sortingOption, savedUser.getPassportNumber());

        if (cursor != null && cursor.moveToFirst()) {
            oneWayFlightList.clear(); // Clear previous results

            do {
                // Extract flight details from cursor
                String flightNumber = cursor.getString(cursor.getColumnIndexOrThrow("FLIGHT_NUMBER"));
                String departurePlace = cursor.getString(cursor.getColumnIndexOrThrow("DEPARTURE_PLACE"));
                String destination = cursor.getString(cursor.getColumnIndexOrThrow("DESTINATION"));
                String aircraftModel = cursor.getString(cursor.getColumnIndexOrThrow("AIRCRAFT_MODEL"));
                String departureDateObj = cursor.getString(cursor.getColumnIndexOrThrow("DEPARTURE_DATE"));
                String departureTime = cursor.getString(cursor.getColumnIndexOrThrow("DEPARTURE_TIME"));
                String arrivalDate = cursor.getString(cursor.getColumnIndexOrThrow("ARRIVAL_DATE"));
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

                // Create Flight object and add to list
                Flight flight = new Flight(destination, departureDate, arrivalDate,
                        duration, flightNumber, departurePlace, departureTime, arrivalTime,
                        aircraftModel, currentReservations, maxSeats, missedFlights, bookingOpenDate,
                        economyClassPrice, businessClassPrice, extraBaggagePrice, recurrent);
                oneWayFlightList.add(flight);

            } while (cursor.moveToNext());

            cursor.close();

            // Set adapter for one-way flight results
            FlightAdapter adapter = new FlightAdapter(getContext(), oneWayFlightList);
            lvOneWayFlightResults.setAdapter(adapter);

            // Set item click listener for one-way flights
            lvOneWayFlightResults.setOnItemClickListener((parent, view, position, id) -> showFlightDetailsDialog(oneWayFlightList.get(position)));
        } else {
            oneWayFlightList.clear(); // Clear list if no results found
            Toast.makeText(getContext(), "No flights found on one way for the specified criteria", Toast.LENGTH_SHORT).show();

            // Update ListView to reflect cleared list
            FlightAdapter adapter = new FlightAdapter(getContext(), oneWayFlightList);
            lvOneWayFlightResults.setAdapter(adapter);
        }
    }

    // Display flight details in a dialog
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
                "Current Reservations: " + flight.getCurrentReservations() + "\n" +
                "Missed Flights: " + flight.getMissedFlights() + "\n" +
                "Recurrent: " + flight.getRecurrent();

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Flight Details")
                .setMessage(flightDetails)
                .setPositiveButton("Make Reservation", (dialog, which) -> {
                    // Trigger the reservation process for the selected flight
                    makeReservation(flight);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    // Open reservation dialog for selected flight
    private void makeReservation(Flight flight) {
        ReservationDialogFragment reservationDialogFragment = ReservationDialogFragment.newInstance(flight, savedUser, false, null);
        reservationDialogFragment.show(getParentFragmentManager(), "reservationDialog");
    }

    // Set up date picker for EditText
    private void setDatePicker(EditText editText) {
        editText.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getContext(),
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        // Format the date as yyyy-MM-dd
                        String formattedDate = String.format("%04d-%02d-%02d", year1, monthOfYear + 1, dayOfMonth);

                        // Validate dates
                        if (editText == etDepartureDate) {
                            if (!isDateValid(formattedDate)) {
                                Toast.makeText(getContext(), "Date cannot be earlier than today", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        if (editText == etReturnDate) {
                            String departureDate = etDepartureDate.getText().toString().trim();
                            if (!departureDate.isEmpty() && !isReturnDateValid(departureDate, formattedDate)) {
                                Toast.makeText(getContext(), "Arrival date must be later than departure date", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        if (editText == etDepartureDate) {
                            String arrivalDate = etReturnDate.getText().toString().trim();
                            if (!arrivalDate.isEmpty() && !isReturnDateValid(formattedDate, arrivalDate)) {
                                Toast.makeText(getContext(), "Departure date cannot be later than arrival date", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        editText.setText(formattedDate);
                    },
                    year, month, day);
            datePickerDialog.show();
        });
    }

    // Check if selected date is valid (not earlier than today)
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

    // Check if return date is valid (not before departure date)
    private boolean isReturnDateValid(String departureDate, String returnDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date depDate = format.parse(departureDate);
            Date arrDate = format.parse(returnDate);
            return !arrDate.before(depDate); // Return date should not be before departure date
        } catch (ParseException e) {
            return false;
        }
    }
}
