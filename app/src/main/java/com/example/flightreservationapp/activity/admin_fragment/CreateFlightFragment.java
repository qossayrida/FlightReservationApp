package com.example.flightreservationapp.activity.admin_fragment;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.flightreservationapp.R;
import com.example.flightreservationapp.database.DataBaseHelper;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CreateFlightFragment extends Fragment {

    // Declare all UI components for flight creation form
    private EditText etFlightNumber, etDepartureDate,
            etDepartureTime, etArrivalDate, etArrivalTime, etDuration, etAircraftModel, etMaxSeats,
            etBookingOpenDate, etPriceEconomy, etPriceBusiness, etPriceExtraBaggage;
    private Spinner spDeparturePlace, spDestination, spinnerRecurrent;
    private Button btnSubmit;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_flight, container, false);

        // Initialize UI components
        etFlightNumber = view.findViewById(R.id.et_flight_number);
        spDeparturePlace = view.findViewById(R.id.sp_departure_place);
        spDestination = view.findViewById(R.id.sp_destination);
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

        // Set up date and time pickers for relevant fields
        setUpDateAndTimePickers();

        // Set up button click listener to handle form submission
        btnSubmit.setOnClickListener(v -> {
            // Collect the data from input fields
            String flightNumber = etFlightNumber.getText().toString().trim();
            String departurePlace = spDeparturePlace.getSelectedItem().toString().trim();
            String destination = spDestination.getSelectedItem().toString().trim();
            String departureDate = etDepartureDate.getText().toString().trim();
            String departureTime = etDepartureTime.getText().toString().trim();
            String arrivalDate = etArrivalDate.getText().toString().trim();
            String arrivalTime = etArrivalTime.getText().toString().trim();
            String durationStr = etDuration.getText().toString().trim();
            String aircraftModel = etAircraftModel.getText().toString().trim();
            String maxSeatsStr = etMaxSeats.getText().toString().trim();
            String bookingOpenDate = etBookingOpenDate.getText().toString().trim();
            String priceEconomyStr = etPriceEconomy.getText().toString().trim();
            String priceBusinessStr = etPriceBusiness.getText().toString().trim();
            String priceExtraBaggageStr = etPriceExtraBaggage.getText().toString().trim();
            String recurrent = spinnerRecurrent.getSelectedItem().toString();

            // Validate the input data to ensure all fields are filled
            if (flightNumber.isEmpty() || departurePlace.isEmpty() || destination.isEmpty() || departureDate.isEmpty() ||
                    departureTime.isEmpty() || arrivalDate.isEmpty() || arrivalTime.isEmpty() || durationStr.isEmpty() ||
                    aircraftModel.isEmpty() || maxSeatsStr.isEmpty() || bookingOpenDate.isEmpty() || priceEconomyStr.isEmpty() ||
                    priceBusinessStr.isEmpty() || priceExtraBaggageStr.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Convert string inputs to appropriate types
            int duration = Integer.parseInt(durationStr);
            int maxSeats = Integer.parseInt(maxSeatsStr);
            double priceEconomy = Double.parseDouble(priceEconomyStr);
            double priceBusiness = Double.parseDouble(priceBusinessStr);
            double priceExtraBaggage = Double.parseDouble(priceExtraBaggageStr);

            // Insert data into the database
            DataBaseHelper dbHelper = new DataBaseHelper(getContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("FLIGHT_NUMBER", flightNumber);
            values.put("DEPARTURE_PLACE", departurePlace);
            values.put("DESTINATION", destination);
            values.put("DEPARTURE_DATE", departureDate);
            values.put("DEPARTURE_TIME", departureTime);
            values.put("ARRIVAL_DATE", arrivalDate);
            values.put("ARRIVAL_TIME", arrivalTime);
            values.put("DURATION", duration);
            values.put("AIRCRAFT_MODEL", aircraftModel);
            values.put("MAX_SEATS", maxSeats);
            values.put("BOOKING_OPEN_DATE", bookingOpenDate);
            values.put("ECONOMY_CLASS_PRICE", priceEconomy);
            values.put("BUSINESS_CLASS_PRICE", priceBusiness);
            values.put("EXTRA_BAGGAGE_PRICE", priceExtraBaggage);
            values.put("RECURRENT", recurrent);
            values.put("CURRENT_RESERVATIONS", 0); // Initial reservations set to 0
            values.put("MISSED_FLIGHTS", 0); // Initial missed flights set to 0

            // Check if the data was successfully inserted
            long result = db.insert("FLIGHTS", null, values);

            // Display appropriate message based on the result
            if (result != -1) {
                Toast.makeText(getContext(), "Flight created successfully", Toast.LENGTH_SHORT).show();
                clearFields(); // Clear the form fields after successful submission
            } else {
                Toast.makeText(getContext(), "Failed to create flight", Toast.LENGTH_SHORT).show();
            }

            db.close(); // Close the database after the operation
        });

        return view;
    }

    // Clear the input fields after successful submission
    private void clearFields() {
        etFlightNumber.setText("");
        spDeparturePlace.setSelection(0);
        spDestination.setSelection(0);
        etDepartureDate.setText("");
        etDepartureTime.setText("");
        etArrivalDate.setText("");
        etArrivalTime.setText("");
        etDuration.setText("");
        etAircraftModel.setText("");
        etMaxSeats.setText("");
        etBookingOpenDate.setText("");
        etPriceEconomy.setText("");
        etPriceBusiness.setText("");
        etPriceExtraBaggage.setText("");
        spinnerRecurrent.setSelection(0);
    }

    // Set up the date and time pickers for relevant fields
    private void setUpDateAndTimePickers() {
        setDatePicker(etDepartureDate);
        setTimePicker(etDepartureTime);
        setDatePicker(etArrivalDate);
        setTimePicker(etArrivalTime);
        setDatePicker(etBookingOpenDate);
    }

    // Set up date picker dialog for a specific EditText field
    private void setDatePicker(EditText editText) {
        editText.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getContext(),
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        // Format the selected date as yyyy-MM-dd
                        String formattedDate = String.format("%04d-%02d-%02d", year1, monthOfYear + 1, dayOfMonth);

                        // Validate against current date for etBookingOpenDate and etDepartureDate
                        if (editText == etBookingOpenDate || editText == etDepartureDate) {
                            if (!isDateValid(formattedDate)) {
                                Toast.makeText(getContext(), "Date cannot be earlier than today", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }

                        // Validate booking open date against departure date
                        if (editText == etBookingOpenDate) {
                            String departureDate = etDepartureDate.getText().toString().trim();
                            if (!departureDate.isEmpty() && !isBookingOpenDateValid(formattedDate, departureDate)) {
                                Toast.makeText(getContext(), "Booking open date must be on or before the departure date", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }

                        // Validate arrival date against departure date
                        if (editText == etArrivalDate) {
                            String departureDate = etDepartureDate.getText().toString().trim();
                            if (!departureDate.isEmpty() && !isArrivalDateValid(departureDate, formattedDate)) {
                                Toast.makeText(getContext(), "Arrival date must be on or after the departure date", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }

                        editText.setText(formattedDate);
                    },
                    year, month, day);
            datePickerDialog.show();
        });
    }

    // Set up time picker dialog for a specific EditText field
    private void setTimePicker(EditText editText) {
        editText.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    getContext(),
                    (view, hourOfDay, minute1) -> {
                        // Format the selected time as HH:mm
                        String formattedTime = String.format("%02d:%02d", hourOfDay, minute1);
                        editText.setText(formattedTime);
                    },
                    hour, minute, true);
            timePickerDialog.show();
        });
    }

    // Validate that a date is not earlier than today's date
    private boolean isDateValid(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date selectedDate = sdf.parse(dateStr);
            Date currentDate = new Date();
            return selectedDate != null && !selectedDate.before(currentDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Validate that the booking open date is on or before the departure date
    private boolean isBookingOpenDateValid(String bookingOpenDate, String departureDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date bookingDate = sdf.parse(bookingOpenDate);
            Date depDate = sdf.parse(departureDate);
            return bookingDate != null && depDate != null && !bookingDate.after(depDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Validate that the arrival date is on or after the departure date
    private boolean isArrivalDateValid(String departureDate, String arrivalDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date depDate = sdf.parse(departureDate);
            Date arrDate = sdf.parse(arrivalDate);
            return depDate != null && arrDate != null && !arrDate.before(depDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
}
