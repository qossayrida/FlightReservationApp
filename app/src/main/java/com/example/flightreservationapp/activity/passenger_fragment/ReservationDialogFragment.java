package com.example.flightreservationapp.activity.passenger_fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.flightreservationapp.R;
import com.example.flightreservationapp.database.DataBaseHelper;
import com.example.flightreservationapp.model.Flight;
import com.example.flightreservationapp.model.Reservation;
import com.example.flightreservationapp.model.User;
import java.util.UUID;

public class ReservationDialogFragment extends DialogFragment {

    private Flight flight; // Selected flight for the reservation
    private User user; // User making the reservation
    private Spinner spFlightClass; // Spinner for selecting flight class (Economy or Business)
    private EditText etExtraBags; // Input field for extra baggage
    private Button btnConfirmReservation; // Button to confirm or update reservation
    private boolean isEditMode; // Flag to check if it's an edit mode
    private Reservation reservation; // Existing reservation to be edited, if applicable

    private OnReservationUpdatedListener reservationUpdatedListener; // Listener for reservation updates

    // Interface for notifying when a reservation is updated
    public interface OnReservationUpdatedListener {
        void onReservationUpdated();
    }

    // Method to set the listener for reservation updates
    public void setOnReservationUpdatedListener(OnReservationUpdatedListener listener) {
        this.reservationUpdatedListener = listener;
    }

    // Static method to create a new instance of the fragment with the provided arguments
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
        // Retrieve arguments passed to the fragment
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
        // Inflate the layout for this dialog fragment
        View view = inflater.inflate(R.layout.fragment_reservation_dialog, container, false);

        // Initialize UI components
        spFlightClass = view.findViewById(R.id.sp_flight_class);
        etExtraBags = view.findViewById(R.id.et_extra_bags);
        btnConfirmReservation = view.findViewById(R.id.btn_confirm_reservation);

        if (isEditMode && reservation != null) {
            // Populate the fields with existing reservation data if in edit mode
            if (reservation.getFlightClass().equals("Business")) {
                spFlightClass.setSelection(1); // Assuming index 1 is for Business class
            } else {
                spFlightClass.setSelection(0); // Assuming index 0 is for Economy class
            }
            etExtraBags.setText(String.valueOf(reservation.getExtraBags()));
            btnConfirmReservation.setText("Update Reservation");
        }

        // Set up button click listener
        btnConfirmReservation.setOnClickListener(v -> confirmOrEditReservation());

        return view;
    }

    // Method to confirm or edit the reservation based on the mode
    private void confirmOrEditReservation() {
        // Retrieve selected flight class and extra baggage input
        String flightClass = spFlightClass.getSelectedItem().toString();
        String extraBagsStr = etExtraBags.getText().toString().trim();
        int extraBags = TextUtils.isEmpty(extraBagsStr) ? 0 : Integer.parseInt(extraBagsStr);

        // Calculate the total cost based on flight class and extra baggage
        double classPrice = flightClass.equals("Business") ? flight.getBusinessClassPrice() : flight.getEconomyClassPrice();
        double totalCost = classPrice + (extraBags * flight.getExtraBaggagePrice());

        DataBaseHelper dbHelper = new DataBaseHelper(getContext());

        if (isEditMode) {
            // Update the existing reservation
            dbHelper.updateReservation(reservation.getReservationId(), flightClass, extraBags, totalCost);
            Toast.makeText(getContext(), "Reservation Updated!", Toast.LENGTH_SHORT).show();

            // Notify the listener of the update
            if (reservationUpdatedListener != null) {
                reservationUpdatedListener.onReservationUpdated();
            }
        } else {
            // Create a new reservation
            String reservationId = UUID.randomUUID().toString();
            dbHelper.insertReservation(reservationId, user.getPassportNumber(), flight.getFlightNumber(), flightClass, extraBags, totalCost);
            Toast.makeText(getContext(), "Reservation Confirmed!", Toast.LENGTH_SHORT).show();
        }

        // Dismiss the dialog after action is completed
        dismiss();
    }
}
