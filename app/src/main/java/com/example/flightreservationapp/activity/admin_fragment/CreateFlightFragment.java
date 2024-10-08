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

    private EditText etFlightNumber, etDepartureDate,
            etDepartureTime, etArrivalDate, etArrivalTime, etDuration, etAircraftModel, etMaxSeats,
            etBookingOpenDate, etPriceEconomy, etPriceBusiness, etPriceExtraBaggage;
    private Spinner spDeparturePlace,spDestination ,spinnerRecurrent;
    private Button btnSubmit;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_flight, container, false);

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

        // Date & Time Pickers
        setUpDateAndTimePickers();

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

            // Validate the input data
            if (flightNumber.isEmpty() || departurePlace.isEmpty() || destination.isEmpty() || departureDate.isEmpty() ||
                    departureTime.isEmpty() || arrivalDate.isEmpty() || arrivalTime.isEmpty() || durationStr.isEmpty() ||
                    aircraftModel.isEmpty() || maxSeatsStr.isEmpty() || bookingOpenDate.isEmpty() || priceEconomyStr.isEmpty() ||
                    priceBusinessStr.isEmpty() || priceExtraBaggageStr.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
                return;
            }

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

            long result = db.insert("FLIGHTS", null, values);

            if (result != -1) {
                Toast.makeText(getContext(), "Flight created successfully", Toast.LENGTH_SHORT).show();
                clearFields();
            } else {
                Toast.makeText(getContext(), "Failed to create flight", Toast.LENGTH_SHORT).show();
            }

            db.close(); // Close the database after operation
        });

        return view;
    }

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
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        // Format the month and day to always be two digits
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
                                Toast.makeText(getContext(), "Arrival date must be later than departure date", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }

                        // Validate departure date against arrival date
                        if (editText == etDepartureDate) {
                            String arrivalDate = etArrivalDate.getText().toString().trim();
                            if (!arrivalDate.isEmpty() && !isArrivalDateValid(formattedDate, arrivalDate)) {
                                Toast.makeText(getContext(), "Departure date cannot be later than arrival date", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // Also validate against booking open date
                            String bookingOpenDate = etBookingOpenDate.getText().toString().trim();
                            if (!bookingOpenDate.isEmpty() && !isBookingOpenDateValid(bookingOpenDate, formattedDate)) {
                                Toast.makeText(getContext(), "Departure date cannot be earlier than the booking open date", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }

                        editText.setText(formattedDate);

                        // After the date is set, calculate the duration
                        int duration = calculateDuration(
                                etDepartureDate.getText().toString().trim(),
                                etDepartureTime.getText().toString().trim(),
                                etArrivalDate.getText().toString().trim(),
                                etArrivalTime.getText().toString().trim()
                        );

                        // Check if duration calculation was successful
                        if (duration != -1) {
                            etDuration.setText(String.valueOf(duration)); // Set duration in minutes
                        } else {
                            etDuration.setText(""); // Clear duration if calculation failed
                        }
                    },
                    year, month, day);
            datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis()); // Set minimum date to current date
            datePickerDialog.show();
        });
    }

    private boolean isDateValid(String selectedDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = format.parse(selectedDate);
            Date currentDate = new Date();
            return !date.before(currentDate); // Date should not be before today
        } catch (ParseException e) {
            return false;
        }
    }

    private boolean isArrivalDateValid(String departureDate, String arrivalDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date depDate = format.parse(departureDate);
            Date arrDate = format.parse(arrivalDate);
            return !arrDate.before(depDate); // Arrival date should not be before departure date
        } catch (ParseException e) {
            return false;
        }
    }

    private boolean isBookingOpenDateValid(String bookingOpenDate, String departureDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date bookOpenDate = format.parse(bookingOpenDate);
            Date depDate = format.parse(departureDate);
            return !bookOpenDate.after(depDate); // Booking open date should not be after departure date
        } catch (ParseException e) {
            return false;
        }
    }

    private void setTimePicker(EditText editText) {
        editText.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    getContext(),
                    (view, hourOfDay, minute1) -> {
                        // Format the time to always be two digits
                        String formattedTime = String.format("%02d:%02d", hourOfDay, minute1);



                        // Additional check if the departure and arrival dates are the same
                        if (editText == etArrivalTime) {
                            String departureDate = etDepartureDate.getText().toString().trim();
                            String arrivalDate = etArrivalDate.getText().toString().trim();
                            String departureTime = etDepartureTime.getText().toString().trim();
                            if (!departureDate.isEmpty() && !arrivalDate.isEmpty() && departureDate.equals(arrivalDate)) {
                                if (!departureTime.isEmpty() && !isArrivalTimeValid(departureTime, formattedTime)) {
                                    Toast.makeText(getContext(), "Arrival time must be later than departure time", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                        }

                        editText.setText(formattedTime);
                        // After the time is set, calculate the duration
                        int duration = calculateDuration(
                                etDepartureDate.getText().toString().trim(),
                                etDepartureTime.getText().toString().trim(),
                                etArrivalDate.getText().toString().trim(),
                                etArrivalTime.getText().toString().trim()
                        );

                        // Check if duration calculation was successful
                        if (duration != -1) {
                            etDuration.setText(String.valueOf(duration)); // Set duration in minutes
                        } else {
                            etDuration.setText(""); // Clear duration if calculation failed
                        }
                    },
                    hour, minute, false);
            timePickerDialog.show();
        });
    }


    private int calculateDuration(String departureDate, String departureTime, String arrivalDate, String arrivalTime) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date depDateTime = format.parse(departureDate + " " + departureTime);
            Date arrDateTime = format.parse(arrivalDate + " " + arrivalTime);

            long difference = arrDateTime.getTime() - depDateTime.getTime();

            long diffMinutes = difference / (60 * 1000);

            return (int) diffMinutes;

        } catch (ParseException e) {
            return -1; // Return -1 to indicate an error in calculation
        }
    }

    private boolean isArrivalTimeValid(String departureTime, String arrivalTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        try {
            Date depTime = sdf.parse(departureTime);
            Date arrTime = sdf.parse(arrivalTime);
            return !arrTime.before(depTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

}
