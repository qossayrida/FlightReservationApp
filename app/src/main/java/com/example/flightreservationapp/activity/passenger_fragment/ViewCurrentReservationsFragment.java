package com.example.flightreservationapp.activity.passenger_fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.flightreservationapp.R;
import com.example.flightreservationapp.database.DataBaseHelper;
import com.example.flightreservationapp.model.Flight;
import com.example.flightreservationapp.model.Reservation;
import com.example.flightreservationapp.model.User;
import com.example.flightreservationapp.utility.JsonConverter;
import com.example.flightreservationapp.utility.ReservationAdapter;
import com.example.flightreservationapp.utility.SharedPrefManager;

import java.util.ArrayList;

public class ViewCurrentReservationsFragment extends Fragment implements ReservationDialogFragment.OnReservationUpdatedListener{

    private ListView lvReservations;
    private DataBaseHelper dbHelper;
    private ArrayList<Reservation> reservationList;
    private ReservationAdapter reservationAdapter;
    private SharedPrefManager sharedPrefManager;
    User savedUser = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_current_reservations, container, false);

        lvReservations = view.findViewById(R.id.lv_current_reservations);
        dbHelper = new DataBaseHelper(getContext());
        reservationList = new ArrayList<>();

        reservationAdapter = new ReservationAdapter(getContext(), reservationList);
        lvReservations.setAdapter(reservationAdapter);

        sharedPrefManager = SharedPrefManager.getInstance(requireContext());
        String savedUserJson = sharedPrefManager.readString("userJson", null);
        if (savedUserJson != null)
            savedUser = JsonConverter.jsonToUser(savedUserJson);

        loadReservations(savedUser.getPassportNumber());

        lvReservations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Reservation selectedReservation = reservationList.get(position);
                showReservationDialog(selectedReservation);
            }
        });

        return view;
    }



    private void loadReservations(String passportNumber) {
        Cursor cursor = dbHelper.getCurrentReservationsForUser(passportNumber);
        reservationList.clear();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String reservationId = cursor.getString(cursor.getColumnIndexOrThrow("RESERVATION_ID"));
                String flightNumber = cursor.getString(cursor.getColumnIndexOrThrow("FLIGHT_NUMBER"));
                String flightClass = cursor.getString(cursor.getColumnIndexOrThrow("FLIGHT_CLASS"));
                int extraBags = cursor.getInt(cursor.getColumnIndexOrThrow("EXTRA_BAGS"));
                double totalCost = cursor.getDouble(cursor.getColumnIndexOrThrow("TOTAL_COST"));

                Reservation reservation = new Reservation(reservationId, passportNumber, flightNumber, flightClass, extraBags, totalCost);
                reservationList.add(reservation);
            } while (cursor.moveToNext());
            cursor.close();
        }

        reservationAdapter.notifyDataSetChanged();

        if (reservationList.isEmpty()) {
            Toast.makeText(getContext(), "No reservations found for this user", Toast.LENGTH_SHORT).show();
        }
    }

    private void showReservationDialog(Reservation reservation) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Reservation Details");

        // Customize the message to show the details of the selected reservation
        String message = "Reservation ID: " + reservation.getReservationId() + "\n" +
                "Flight Number: " + reservation.getFlightNumber() + "\n" +
                "Flight Class: " + reservation.getFlightClass() + "\n" +
                "Extra Bags: " + reservation.getExtraBags() + "\n" +
                "Total Cost: $" + reservation.getTotalCost();

        builder.setMessage(message);

        // Set up the "Delete" button
        builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle reservation deletion
                deleteReservation(reservation);
            }
        });

        // Set up the "Edit" button
        builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle reservation editing
                editReservation(reservation);
            }
        });

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteReservation(Reservation reservation) {
        boolean success = dbHelper.deleteReservation(reservation.getReservationId());
        if (success) {
            Toast.makeText(getContext(), "Reservation deleted", Toast.LENGTH_SHORT).show();
            // Reload reservations after deletion
            loadReservations(reservation.getPassportNumber());
        } else {
            Toast.makeText(getContext(), "Failed to delete reservation", Toast.LENGTH_SHORT).show();
        }
    }

    private void editReservation(Reservation reservation) {
        Cursor cursor =  dbHelper.getFlightDetailsByFlightNumber(reservation.getFlightNumber());
        Flight flight = null;
        if(cursor != null && cursor.moveToFirst()){
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

            flight = new Flight( destination,  departureDate,  arrivalDate,
                    duration,  flightNumber,  departurePlace,  departureTime,  arrivalTime,
                    aircraftModel,  currentReservations,  maxSeats,  missedFlights,  bookingOpenDate,
                    economyClassPrice ,businessClassPrice,extraBaggagePrice,recurrent);
        }
        
        ReservationDialogFragment reservationDialogFragment =
                ReservationDialogFragment.newInstance(flight, savedUser, true, reservation);
        reservationDialogFragment.setOnReservationUpdatedListener(this);
        reservationDialogFragment.show(getParentFragmentManager(), "editReservationDialog");

    }

    @Override
    public void onReservationUpdated() {
        loadReservations(savedUser.getPassportNumber());
    }

}
