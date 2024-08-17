package com.example.flightreservationapp.activity.admin_fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.flightreservationapp.R;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Calendar;

public class CreateFlightFragment extends Fragment {

    private EditText etFlightNumber, etDeparturePlace, etDestination, etDepartureDate,
            etDepartureTime, etArrivalDate, etArrivalTime, etDuration, etAircraftModel, etMaxSeats,
            etBookingOpenDate, etPriceEconomy, etPriceBusiness, etPriceExtraBaggage;
    private Spinner spinnerRecurrent;
    private Button btnSubmit;

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
        etAircraftModel = view.findViewById(R.id.et_aircraft_model);
        etMaxSeats = view.findViewById(R.id.et_max_seats);
        etBookingOpenDate = view.findViewById(R.id.et_booking_open_date);
        etPriceEconomy = view.findViewById(R.id.et_price_economy);
        etPriceBusiness = view.findViewById(R.id.et_price_business);
        etPriceExtraBaggage = view.findViewById(R.id.et_price_extra_baggage);
        spinnerRecurrent = view.findViewById(R.id.spinner_recurrent);
        btnSubmit = view.findViewById(R.id.btn_submit);

        // Date & Time Pickers
        setUpDateAndTimePickers();

        btnSubmit.setOnClickListener(v -> {
            // Handle the logic to create a new flight
            // Collect data from the input fields and store them in the database
        });

        return view;
    }

    private void setUpDateAndTimePickers() {
        setDatePicker(etDepartureDate);
        setTimePicker(etDepartureTime);
        setDatePicker(etArrivalDate);
        setTimePicker(etArrivalTime);
        setDatePicker(etBookingOpenDate);
    }

    private void setDatePicker(EditText editText) {
        editText.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getContext(),
                    (view, year1, monthOfYear, dayOfMonth) ->
                            editText.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year1),
                    year, month, day);
            datePickerDialog.show();
        });
    }

    private void setTimePicker(EditText editText) {
        editText.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    getContext(),
                    (view, hourOfDay, minute1) ->
                            editText.setText(hourOfDay + ":" + minute1),
                    hour, minute, false);
            timePickerDialog.show();
        });
    }
}
