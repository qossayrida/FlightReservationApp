package com.example.flightreservationapp.activity.admin_fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.flightreservationapp.R;
import com.example.flightreservationapp.database.DataBaseHelper;
import com.example.flightreservationapp.model.Flight;

public class EditFlightDialogFragment extends DialogFragment {

    private EditText etFlightNumber, etDeparturePlace, etDestination, etDepartureDate, etDepartureTime, etArrivalDate, etArrivalTime, etDuration;
    private Button btnUpdateFlight;
    private Flight flight;
    private OnFlightUpdatedListener flightUpdatedListener;

    public interface OnFlightUpdatedListener {
        void onFlightUpdated();
    }

    public void setOnFlightUpdatedListener(OnFlightUpdatedListener listener) {
        this.flightUpdatedListener = listener;
    }

    public static EditFlightDialogFragment newInstance(Flight flight) {
        EditFlightDialogFragment fragment = new EditFlightDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable("flight", flight);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            flight = (Flight) getArguments().getSerializable("flight");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_flight, container, false);

        etFlightNumber = view.findViewById(R.id.et_flight_number);
        etDeparturePlace = view.findViewById(R.id.et_departure_place);
        etDestination = view.findViewById(R.id.et_destination);
        etDepartureDate = view.findViewById(R.id.et_departure_date);
        etDepartureTime = view.findViewById(R.id.et_departure_time);
        etArrivalDate = view.findViewById(R.id.et_arrival_date);
        etArrivalTime = view.findViewById(R.id.et_arrival_time);
        etDuration = view.findViewById(R.id.et_duration);
        btnUpdateFlight = view.findViewById(R.id.btn_submit);


        // Populate fields with the current flight data
        if (flight != null) {
            etFlightNumber.setText(flight.getFlightNumber());
            etDeparturePlace.setText(flight.getDeparturePlace());
            etDestination.setText(flight.getDestination());
            etDepartureDate.setText(flight.getDepartureDate());
            etDepartureTime.setText(flight.getDepartureTime());
            etArrivalDate.setText(flight.getArrivalDate());
            etArrivalTime.setText(flight.getArrivalTime());
            etDuration.setText(String.valueOf(flight.getDuration()));


            // Update button text to reflect edit mode
            btnUpdateFlight.setText("Update Flight");
        }

        btnUpdateFlight.setOnClickListener(v -> updateFlight());

        return view;
    }

    private void updateFlight() {
        String flightNumber = etFlightNumber.getText().toString().trim();
        String departurePlace = etDeparturePlace.getText().toString().trim();
        String destination = etDestination.getText().toString().trim();
        String departureDate = etDepartureDate.getText().toString().trim();
        String departureTime = etDepartureTime.getText().toString().trim();
        String arrivalDate = etArrivalDate.getText().toString().trim();
        String arrivalTime = etArrivalTime.getText().toString().trim();
        int duration = Integer.parseInt(etDuration.getText().toString().trim());

        DataBaseHelper dbHelper = new DataBaseHelper(getContext());

        boolean success = dbHelper.updateFlight(flight.getFlightNumber(), flightNumber, departurePlace, destination,
                departureDate, departureTime, arrivalDate, arrivalTime, duration);

        if (success) {
            Toast.makeText(getContext(), "Flight updated successfully!", Toast.LENGTH_SHORT).show();
            if (flightUpdatedListener != null) {
                flightUpdatedListener.onFlightUpdated();
            }
            dismiss();
        } else {
            Toast.makeText(getContext(), "Failed to update flight", Toast.LENGTH_SHORT).show();
        }
    }
}
