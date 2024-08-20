package com.example.flightreservationapp.activity.admin_fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.flightreservationapp.R;
import com.example.flightreservationapp.database.DataBaseHelper;
import com.example.flightreservationapp.model.Reservation;
import com.example.flightreservationapp.utility.ReservationAdapter;

import java.util.ArrayList;

public class ViewAllReservationsFragment extends Fragment {

    private EditText etFlightNumber; // EditText for entering the flight number
    private Button btnSearchReservations; // Button to initiate reservation search
    private ListView lvReservations; // ListView to display reservations
    private DataBaseHelper dbHelper; // Database helper for querying reservations
    private ArrayList<Reservation> reservationList; // List to hold reservations
    private ReservationAdapter reservationAdapter; // Adapter for displaying reservations

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_all_reservations, container, false);

        // Initialize views
        etFlightNumber = view.findViewById(R.id.et_flight_number);
        btnSearchReservations = view.findViewById(R.id.btn_search_reservations);
        lvReservations = view.findViewById(R.id.lv_reservations);

        // Initialize database helper and reservation list
        dbHelper = new DataBaseHelper(getContext());
        reservationList = new ArrayList<>();

        // Set up the adapter for the ListView
        reservationAdapter = new ReservationAdapter(getContext(), reservationList);
        lvReservations.setAdapter(reservationAdapter);

        // Set up click listener for the search button
        btnSearchReservations.setOnClickListener(v -> {
            String flightNumber = etFlightNumber.getText().toString().trim();
            if (!flightNumber.isEmpty()) {
                loadReservations(flightNumber); // Load reservations for the given flight number
            } else {
                Toast.makeText(getContext(), "Please enter a flight number", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    // Load reservations from the database based on flight number
    private void loadReservations(String flightNumber) {
        Cursor cursor = dbHelper.getReservationsForFlight(flightNumber);
        reservationList.clear(); // Clear existing data

        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Retrieve reservation details from the cursor
                String reservationId = cursor.getString(cursor.getColumnIndexOrThrow("RESERVATION_ID"));
                String passportNumber = cursor.getString(cursor.getColumnIndexOrThrow("PASSPORT_NUMBER"));
                String flightClass = cursor.getString(cursor.getColumnIndexOrThrow("FLIGHT_CLASS"));
                int extraBags = cursor.getInt(cursor.getColumnIndexOrThrow("EXTRA_BAGS"));
                double totalCost = cursor.getDouble(cursor.getColumnIndexOrThrow("TOTAL_COST"));

                // Create a Reservation object and add it to the list
                Reservation reservation = new Reservation(reservationId, passportNumber, flightNumber, flightClass, extraBags, totalCost);
                reservationList.add(reservation);
            } while (cursor.moveToNext());
            cursor.close(); // Close the cursor
        }

        // Notify the adapter of the data change
        reservationAdapter.notifyDataSetChanged();

        // Show a message if no reservations are found
        if (reservationList.isEmpty()) {
            Toast.makeText(getContext(), "No reservations found for this flight", Toast.LENGTH_SHORT).show();
        }
    }
}
