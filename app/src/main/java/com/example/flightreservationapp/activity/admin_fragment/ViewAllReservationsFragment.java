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

    private EditText etFlightNumber;
    private Button btnSearchReservations;
    private ListView lvReservations;
    private DataBaseHelper dbHelper;
    private ArrayList<Reservation> reservationList;
    private ReservationAdapter reservationAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_all_reservations, container, false);

        etFlightNumber = view.findViewById(R.id.et_flight_number);
        btnSearchReservations = view.findViewById(R.id.btn_search_reservations);
        lvReservations = view.findViewById(R.id.lv_reservations);
        dbHelper = new DataBaseHelper(getContext());
        reservationList = new ArrayList<>();

        reservationAdapter = new ReservationAdapter(getContext(), reservationList);
        lvReservations.setAdapter(reservationAdapter);

        btnSearchReservations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String flightNumber = etFlightNumber.getText().toString().trim();
                if (!flightNumber.isEmpty()) {
                    loadReservations(flightNumber);
                } else {
                    Toast.makeText(getContext(), "Please enter a flight number", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void loadReservations(String flightNumber) {
        Cursor cursor = dbHelper.getReservationsForFlight(flightNumber);
        reservationList.clear();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String reservationId = cursor.getString(cursor.getColumnIndexOrThrow("RESERVATION_ID"));
                String passportNumber = cursor.getString(cursor.getColumnIndexOrThrow("PASSPORT_NUMBER"));
                String flightClass = cursor.getString(cursor.getColumnIndexOrThrow("FLIGHT_CLASS"));
                int extraBags = cursor.getInt(cursor.getColumnIndexOrThrow("EXTRA_BAGS"));
                String foodPreference = cursor.getString(cursor.getColumnIndexOrThrow("FOOD_PREFERENCE"));
                double totalCost = cursor.getDouble(cursor.getColumnIndexOrThrow("TOTAL_COST"));

                Reservation reservation = new Reservation(reservationId, passportNumber, flightNumber, flightClass, extraBags, foodPreference, totalCost);
                reservationList.add(reservation);
            } while (cursor.moveToNext());
            cursor.close();
        }

        reservationAdapter.notifyDataSetChanged();

        if (reservationList.isEmpty()) {
            Toast.makeText(getContext(), "No reservations found for this flight", Toast.LENGTH_SHORT).show();
        }
    }
}
