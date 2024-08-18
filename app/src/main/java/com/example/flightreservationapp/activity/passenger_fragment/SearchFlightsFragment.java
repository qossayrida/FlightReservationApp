package com.example.flightreservationapp.activity.passenger_fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SearchFlightsFragment extends Fragment {

    private EditText etDepartureCity, etArrivalCity, etDepartureDate, etReturnDate;
    private TextView tvReturnDateLabel;
    private RadioGroup rgTripType;
    private Spinner spSorting;
    private Button btnSearch;
    private ListView lvFlightResults;
    private DataBaseHelper dbHelper;
    private ArrayList<Flight> flightList;
    private User savedUser = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_flights, container, false);

        etDepartureCity = view.findViewById(R.id.et_departure_city);
        etArrivalCity = view.findViewById(R.id.et_arrival_city);
        etDepartureDate = view.findViewById(R.id.et_departure_date);
        etReturnDate = view.findViewById(R.id.et_return_date);
        tvReturnDateLabel = view.findViewById(R.id.tv_return_date_label);
        rgTripType = view.findViewById(R.id.rg_trip_type);
        spSorting = view.findViewById(R.id.sp_sorting);
        btnSearch = view.findViewById(R.id.btn_search);
        lvFlightResults = view.findViewById(R.id.lv_flight_results);

        setDatePicker(etDepartureDate);
        setDatePicker(etReturnDate);

        dbHelper = new DataBaseHelper(getContext());
        flightList = new ArrayList<>();

        // Show/hide return date based on trip type
        rgTripType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_round_trip) {
                etReturnDate.setVisibility(View.VISIBLE);
                tvReturnDateLabel.setVisibility(View.VISIBLE);
            } else {
                etReturnDate.setVisibility(View.GONE);
                tvReturnDateLabel.setVisibility(View.GONE);
            }
        });

        btnSearch.setOnClickListener(v -> searchFlights());

        // Fetch the current user from shared preferences
        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(requireContext());
        String savedUserJson = sharedPrefManager.readString("userJson", null);
        if (savedUserJson != null)
            savedUser = JsonConverter.jsonToUser(savedUserJson);


        return view;
    }

    private void searchFlights() {
        String departureCity = etDepartureCity.getText().toString().trim();
        String arrivalCity = etArrivalCity.getText().toString().trim();
        String departureDate = etDepartureDate.getText().toString().trim();
        String returnDate = etReturnDate.getText().toString().trim();
        String sortingOption = spSorting.getSelectedItem().toString();

        int selectedTripType = rgTripType.getCheckedRadioButtonId();
        if (selectedTripType == R.id.rb_one_way) {
            returnDate = "";
        }

        if (TextUtils.isEmpty(departureCity) || TextUtils.isEmpty(arrivalCity) || TextUtils.isEmpty(departureDate)) {
            Toast.makeText(getContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Perform database query using dbHelper with sorting and filtering criteria
        Cursor cursor = dbHelper.searchFlights(departureCity, arrivalCity, departureDate, returnDate, sortingOption);

        if (cursor != null && cursor.moveToFirst()) {
            flightList.clear(); // Clear the list before adding new results

            do {
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
                String bookingOpenDate = cursor.getString(cursor.getColumnIndexOrThrow("BOOKING_OPEN_DATE"));
                double economyClassPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("ECONOMY_CLASS_PRICE"));
                double businessClassPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("BUSINESS_CLASS_PRICE"));
                double extraBaggagePrice = cursor.getDouble(cursor.getColumnIndexOrThrow("EXTRA_BAGGAGE_PRICE"));
                Flight.RecurrentType recurrent = Flight.RecurrentType.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("RECURRENT")));

                Flight flight = new Flight(flightNumber, departurePlace, destination, departureDateObj, departureTime,
                        arrivalDate, arrivalTime, duration, aircraftModel, maxSeats,
                        bookingOpenDate, economyClassPrice, businessClassPrice, extraBaggagePrice, recurrent);
                flightList.add(flight);

            } while (cursor.moveToNext());

            cursor.close();

            FlightAdapter adapter = new FlightAdapter(getContext(), flightList);
            lvFlightResults.setAdapter(adapter);

            lvFlightResults.setOnItemClickListener((parent, view, position, id) -> showFlightDetailsDialog(flightList.get(position)));
        } else {
            flightList.clear(); // Clear the list if no results are found
            Toast.makeText(getContext(), "No flights found for the specified criteria", Toast.LENGTH_SHORT).show();

            // Update the ListView to reflect the cleared list
            FlightAdapter adapter = new FlightAdapter(getContext(), flightList);
            lvFlightResults.setAdapter(adapter);
        }
    }


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

    private void makeReservation(Flight flight) {
        ReservationDialogFragment reservationDialogFragment = ReservationDialogFragment.newInstance(flight,savedUser,false,null);
        reservationDialogFragment.show(getParentFragmentManager(), "reservationDialog");
    }

    private void setDatePicker(EditText editText) {
        editText.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getContext(),
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        // Format the month and day to always be two digits
                        String formattedDate = String.format("%04d-%02d-%02d", year1, monthOfYear + 1, dayOfMonth);
                        editText.setText(formattedDate);
                    },
                    year, month, day);
            datePickerDialog.show();
        });
    }

}
