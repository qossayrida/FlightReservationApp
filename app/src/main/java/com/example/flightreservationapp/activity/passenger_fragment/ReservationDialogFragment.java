package com.example.flightreservationapp.activity.passenger_fragment;

import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.*;

import android.text.TextUtils;
import android.view.*;
import android.widget.*;


import com.example.flightreservationapp.R;
import com.example.flightreservationapp.database.DataBaseHelper;
import com.example.flightreservationapp.model.*;
import java.util.UUID;


public class ReservationDialogFragment extends DialogFragment {

    private Flight flight;
    private User user;
    private Spinner spFlightClass;
    private EditText etExtraBags;
    private Button btnConfirmReservation;
    private boolean isEditMode;
    private Reservation reservation;

    private OnReservationUpdatedListener reservationUpdatedListener;

    public void setOnReservationUpdatedListener(OnReservationUpdatedListener listener) {
        this.reservationUpdatedListener = listener;
    }

    public interface OnReservationUpdatedListener {
        void onReservationUpdated();
    }


    public static ReservationDialogFragment newInstance(Flight flight, User user, boolean isEditMode, Reservation reservation) {
        ReservationDialogFragment fragment = new ReservationDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable("flight", flight);
        args.putSerializable("user", user);
        args.putBoolean("isEditMode", isEditMode);
        args.putSerializable("reservation", reservation);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            flight = (Flight) getArguments().getSerializable("flight");
            user = (User) getArguments().getSerializable("user");
            isEditMode = getArguments().getBoolean("isEditMode");
            reservation = (Reservation) getArguments().getSerializable("reservation");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reservation_dialog, container, false);

        spFlightClass = view.findViewById(R.id.sp_flight_class);
        etExtraBags = view.findViewById(R.id.et_extra_bags);
        btnConfirmReservation = view.findViewById(R.id.btn_confirm_reservation);

        if (isEditMode && reservation != null) {
            // Populate the fields with existing reservation data
            if (reservation.getFlightClass().equals("Business")) {
                spFlightClass.setSelection(1); // Assuming index 1 is for Business class
            } else {
                spFlightClass.setSelection(0); // Assuming index 0 is for Economy class
            }
            etExtraBags.setText(String.valueOf(reservation.getExtraBags()));
            btnConfirmReservation.setText("Update Reservation");
        }

        btnConfirmReservation.setOnClickListener(v -> confirmOrEditReservation());

        return view;
    }

    private void confirmOrEditReservation() {
        String flightClass = spFlightClass.getSelectedItem().toString();
        String extraBagsStr = etExtraBags.getText().toString().trim();
        int extraBags = TextUtils.isEmpty(extraBagsStr) ? 0 : Integer.parseInt(extraBagsStr);

        double classPrice = flightClass.equals("Business") ? flight.getBusinessClassPrice() : flight.getEconomyClassPrice();
        double totalCost = classPrice + (extraBags * flight.getExtraBaggagePrice());

        DataBaseHelper dbHelper = new DataBaseHelper(getContext());

        if (isEditMode) {
            // Update the existing reservation
            dbHelper.updateReservation(reservation.getReservationId(), flightClass, extraBags, totalCost);
            Toast.makeText(getContext(), "Reservation Updated!", Toast.LENGTH_SHORT).show();

            if (reservationUpdatedListener != null) {
                reservationUpdatedListener.onReservationUpdated();
            }
        } else {
            // Create a new reservation
            String reservationId = UUID.randomUUID().toString();
            dbHelper.insertReservation(reservationId, user.getPassportNumber(), flight.getFlightNumber(), flightClass, extraBags, totalCost);
            Toast.makeText(getContext(), "Reservation Confirmed!", Toast.LENGTH_SHORT).show();
        }

        dismiss();
    }
}
