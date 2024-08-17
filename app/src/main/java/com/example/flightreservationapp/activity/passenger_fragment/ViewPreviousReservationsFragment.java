package com.example.flightreservationapp.activity.passenger_fragment;

import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.flightreservationapp.R;
import com.example.flightreservationapp.database.DataBaseHelper;
import com.example.flightreservationapp.model.Reservation;
import com.example.flightreservationapp.model.User;
import com.example.flightreservationapp.utility.JsonConverter;
import com.example.flightreservationapp.utility.ReservationAdapter;
import com.example.flightreservationapp.utility.SharedPrefManager;

import java.util.ArrayList;


public class ViewPreviousReservationsFragment extends Fragment {


    private ListView lvReservations;
    private DataBaseHelper dbHelper;
    private ArrayList<Reservation> reservationList;
    private ReservationAdapter reservationAdapter;
    private SharedPrefManager sharedPrefManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_previous_reservations, container, false);

        lvReservations = view.findViewById(R.id.lv_past_reservations);
        dbHelper = new DataBaseHelper(getContext());
        reservationList = new ArrayList<>();

        reservationAdapter = new ReservationAdapter(getContext(), reservationList);
        lvReservations.setAdapter(reservationAdapter);

        sharedPrefManager = SharedPrefManager.getInstance(requireContext());
        String savedUserJson = sharedPrefManager.readString("userJson", null);
        User savedUser = null;
        if (savedUserJson != null)
            savedUser = JsonConverter.jsonToUser(savedUserJson);
        loadReservations(savedUser.getPassportNumber());

        return view;
    }

    private void loadReservations(String passportNumber) {
        Cursor cursor = dbHelper.getPastReservationsForUser(passportNumber);
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
}