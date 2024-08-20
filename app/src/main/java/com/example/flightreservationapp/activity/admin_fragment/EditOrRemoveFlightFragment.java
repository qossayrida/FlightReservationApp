package com.example.flightreservationapp.activity.admin_fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.flightreservationapp.R;
import com.example.flightreservationapp.database.DataBaseHelper;
import com.example.flightreservationapp.model.Flight;
import com.example.flightreservationapp.utility.FlightAdapter;

import java.util.ArrayList;

public class EditOrRemoveFlightFragment extends Fragment {

    private ListView lvFlights; // ListView to display flights
    private EditText etSearchFlight; // EditText for search input
    private DataBaseHelper dbHelper; // Database helper for managing flights
    private ArrayList<Flight> flightList; // List to hold flight data
    private FlightAdapter adapter; // Adapter to bind flight data to ListView

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_or_remove_flight, container, false);

        // Initialize views
        lvFlights = view.findViewById(R.id.lv_flights);
        etSearchFlight = view.findViewById(R.id.et_search_flight);
        dbHelper = new DataBaseHelper(getContext());
        flightList = new ArrayList<>();

        // Load all flights from the database
        loadFlights();

        // Set up ListView item click listener to edit or remove a flight
        lvFlights.setOnItemClickListener((parent, view1, position, id) -> {
            Flight selectedFlight = flightList.get(position);
            showOptionsDialog(selectedFlight);
        });

        // Set up a text watcher to filter the flight list based on the search input
        etSearchFlight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed here
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterFlights(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed here
            }
        });

        return view;
    }

    // Load flights from the database and populate the ListView
    private void loadFlights() {
        Cursor cursor = dbHelper.getAllFlights(); // Fetch all flights from the database
        flightList.clear(); // Clear existing list

        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Retrieve flight details from the cursor
                String flightNumber = cursor.getString(cursor.getColumnIndexOrThrow("FLIGHT_NUMBER"));
                String departurePlace = cursor.getString(cursor.getColumnIndexOrThrow("DEPARTURE_PLACE"));
                String destination = cursor.getString(cursor.getColumnIndexOrThrow("DESTINATION"));
                String aircraftModel = cursor.getString(cursor.getColumnIndexOrThrow("AIRCRAFT_MODEL"));
                String departureDate = cursor.getString(cursor.getColumnIndexOrThrow("DEPARTURE_DATE"));
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

                // Create a Flight object and add it to the list
                Flight flight = new Flight(destination, departureDate, arrivalDate, duration, flightNumber,
                        departurePlace, departureTime, arrivalTime, aircraftModel, currentReservations,
                        maxSeats, missedFlights, bookingOpenDate, economyClassPrice, businessClassPrice,
                        extraBaggagePrice, recurrent);
                flightList.add(flight);

            } while (cursor.moveToNext());
            cursor.close(); // Close the cursor when done
        }

        // Set up the adapter and bind it to the ListView
        adapter = new FlightAdapter(getContext(), flightList);
        lvFlights.setAdapter(adapter);
    }

    // Filter flights based on the search query
    private void filterFlights(String query) {
        ArrayList<Flight> filteredList = new ArrayList<>();
        for (Flight flight : flightList) {
            // Add flights that match the search query to the filtered list
            if (flight.getFlightNumber().toLowerCase().startsWith(query.toLowerCase())) {
                filteredList.add(flight);
            }
        }

        // Update the adapter with the filtered list
        adapter = new FlightAdapter(getContext(), filteredList);
        lvFlights.setAdapter(adapter);

        // Set up the item click listener for the filtered list
        lvFlights.setOnItemClickListener((parent, view1, position, id) -> {
            Flight selectedFlight = filteredList.get(position);
            showOptionsDialog(selectedFlight);
        });

        // Show a message if no flights match the search
        if (filteredList.isEmpty()) {
            Toast.makeText(getContext(), "No flights match your search", Toast.LENGTH_SHORT).show();
        }
    }

    // Show a dialog with options to edit or delete the selected flight
    private void showOptionsDialog(Flight flight) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select Action");
        builder.setItems(new CharSequence[]{"Edit", "Delete"}, (dialog, which) -> {
            switch (which) {
                case 0:
                    showEditFlightDialog(flight); // Show dialog to edit flight
                    break;
                case 1:
                    // Confirm deletion
                    showDeleteConfirmationDialog(flight); // Show dialog to confirm deletion
                    break;
            }
        });
        builder.show(); // Display the dialog
    }

    // Show a confirmation dialog before deleting a flight
    private void showDeleteConfirmationDialog(Flight flight) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete Flight");
        builder.setMessage("Are you sure you want to delete this flight?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            dbHelper.deleteFlight(flight.getFlightNumber()); // Delete the flight from the database
            loadFlights(); // Refresh the list after deletion
            Toast.makeText(getContext(), "Flight deleted successfully", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("No", null); // Dismiss the dialog
        builder.show(); // Display the dialog
    }

    // Show a dialog to edit the selected flight
    private void showEditFlightDialog(Flight flight) {
        EditFlightDialogFragment editFlightDialogFragment = EditFlightDialogFragment.newInstance(flight);
        editFlightDialogFragment.setOnFlightUpdatedListener(this::loadFlights); // Refresh the list when flight is updated
        editFlightDialogFragment.show(getParentFragmentManager(), "editFlightDialog"); // Display the dialog
    }
}
