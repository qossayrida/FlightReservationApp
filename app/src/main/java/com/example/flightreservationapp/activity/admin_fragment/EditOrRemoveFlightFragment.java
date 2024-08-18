package com.example.flightreservationapp.activity.admin_fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    private ListView lvFlights;
    private DataBaseHelper dbHelper;
    private ArrayList<Flight> flightList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_or_remove_flight, container, false);

        lvFlights = view.findViewById(R.id.lv_flights);
        dbHelper = new DataBaseHelper(getContext());
        flightList = new ArrayList<>();

        // Load all flights from the database
        loadFlights();

        // Set up ListView item click listener to edit or remove a flight
        lvFlights.setOnItemClickListener((parent, view1, position, id) -> {
            Flight selectedFlight = flightList.get(position);
            showOptionsDialog(selectedFlight);
        });

        return view;
    }

    private void loadFlights() {
        Cursor cursor = dbHelper.getAllFlights();
        flightList.clear();

        if (cursor != null && cursor.moveToFirst()) {
            do {
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
                String bookingOpenDate = cursor.getString(cursor.getColumnIndexOrThrow("BOOKING_OPEN_DATE"));
                double economyClassPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("ECONOMY_CLASS_PRICE"));
                double businessClassPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("BUSINESS_CLASS_PRICE"));
                double extraBaggagePrice = cursor.getDouble(cursor.getColumnIndexOrThrow("EXTRA_BAGGAGE_PRICE"));
                Flight.RecurrentType recurrent = Flight.RecurrentType.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("RECURRENT")));

                Flight flight = new Flight(flightNumber, departurePlace, destination, departureDate, departureTime,
                        arrivalDate, arrivalTime, duration, aircraftModel, maxSeats,
                        bookingOpenDate, economyClassPrice, businessClassPrice, extraBaggagePrice, recurrent);
                flightList.add(flight);

            } while (cursor.moveToNext());
            cursor.close();
        }

        FlightAdapter adapter = new FlightAdapter(getContext(), flightList);
        lvFlights.setAdapter(adapter);
    }


    private void showOptionsDialog(Flight flight) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select Action");
        builder.setItems(new CharSequence[]{"Edit", "Delete"}, (dialog, which) -> {
            switch (which) {
                case 0:
                    showEditFlightDialog(flight);
                    break;
                case 1:
                    // Confirm deletion
                    showDeleteConfirmationDialog(flight);
                    break;
            }
        });
        builder.show();
    }

    private void showDeleteConfirmationDialog(Flight flight) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete Flight");
        builder.setMessage("Are you sure you want to delete this flight?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            dbHelper.deleteFlight(flight.getFlightNumber());
            loadFlights(); // Refresh the list after deletion
            Toast.makeText(getContext(), "Flight deleted successfully", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }

    private void showEditFlightDialog(Flight flight) {
        EditFlightDialogFragment editFlightDialogFragment = EditFlightDialogFragment.newInstance(flight);
        editFlightDialogFragment.setOnFlightUpdatedListener(() -> loadFlights());
        editFlightDialogFragment.show(getParentFragmentManager(), "editFlightDialog");
    }
}
