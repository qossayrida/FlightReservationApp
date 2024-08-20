package com.example.flightreservationapp.activity.admin_fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditFlightDialogFragment extends DialogFragment {

    // Views for input fields and buttons
    private EditText etFlightNumber, etDepartureDate,
            etDepartureTime, etArrivalDate, etArrivalTime, etDuration, etAircraftModel, etMaxSeats,
            etBookingOpenDate, etPriceEconomy, etPriceBusiness, etPriceExtraBaggage;
    private Spinner spinnerRecurrent, spDeparturePlace, spDestination;
    private Button btnUpdateFlight;
    private Flight flight; // Flight object to be edited
    private OnFlightUpdatedListener flightUpdatedListener;

    // Interface for notifying when the flight is updated
    public interface OnFlightUpdatedListener {
        void onFlightUpdated();
    }

    // Set the listener for flight updates
    public void setOnFlightUpdatedListener(OnFlightUpdatedListener listener) {
        this.flightUpdatedListener = listener;
    }

    // Create a new instance of the fragment with the given flight
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
        // Retrieve the flight object from arguments
        if (getArguments() != null) {
            flight = (Flight) getArguments().getSerializable("flight");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_flight, container, false);

        // Initialize views
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
        btnUpdateFlight = view.findViewById(R.id.btn_submit);

        // Set up date and time pickers for relevant fields
        setUpDateAndTimePickers();

        // Populate fields with current flight data if available
        if (flight != null) {
            etFlightNumber.setText(flight.getFlightNumber());
            setSpinnerSelection(spDeparturePlace, flight.getDeparturePlace());
            setSpinnerSelection(spDestination, flight.getDestination());
            etDepartureDate.setText(flight.getDepartureDate());
            etDepartureTime.setText(flight.getDepartureTime());
            etArrivalDate.setText(flight.getArrivalDate());
            etArrivalTime.setText(flight.getArrivalTime());
            etDuration.setText(String.valueOf(flight.getDuration()));
            etAircraftModel.setText(flight.getAircraftModel());
            etMaxSeats.setText(String.valueOf(flight.getMaxSeats()));
            etBookingOpenDate.setText(flight.getBookingOpenDate());
            etPriceEconomy.setText(String.valueOf(flight.getEconomyClassPrice()));
            etPriceBusiness.setText(String.valueOf(flight.getBusinessClassPrice()));
            etPriceExtraBaggage.setText(String.valueOf(flight.getExtraBaggagePrice()));

            // Set spinner selection for recurrent type
            if(flight.getRecurrent().equals(Flight.RecurrentType.NONE))
                spinnerRecurrent.setSelection(0);
            else if(flight.getRecurrent().equals(Flight.RecurrentType.DAILY))
                spinnerRecurrent.setSelection(1);
            else
                spinnerRecurrent.setSelection(2);

            // Update button text to reflect edit mode
            btnUpdateFlight.setText("Update Flight");
        }

        // Set up the update button's click listener
        btnUpdateFlight.setOnClickListener(v -> updateFlight());

        return view;
    }

    // Update the flight details in the database
    private void updateFlight() {
        // Retrieve input values from fields
        String flightNumber = etFlightNumber.getText().toString().trim();
        String departurePlace = spDeparturePlace.getSelectedItem().toString().trim();
        String destination = spDestination.getSelectedItem().toString().trim();
        String departureDate = etDepartureDate.getText().toString().trim();
        String departureTime = etDepartureTime.getText().toString().trim();
        String arrivalDate = etArrivalDate.getText().toString().trim();
        String arrivalTime = etArrivalTime.getText().toString().trim();
        int duration = Integer.parseInt(etDuration.getText().toString().trim());
        String aircraftModel = etAircraftModel.getText().toString().trim();
        int maxSeats = Integer.parseInt(etMaxSeats.getText().toString().trim());
        String bookingOpenDate = etBookingOpenDate.getText().toString().trim();
        double priceEconomy = Double.parseDouble(etPriceEconomy.getText().toString().trim());
        double priceBusiness = Double.parseDouble(etPriceBusiness.getText().toString().trim());
        double priceExtraBaggage = Double.parseDouble(etPriceExtraBaggage.getText().toString().trim());
        String recurrent = spinnerRecurrent.getSelectedItem().toString();

        // Update the flight in the database
        DataBaseHelper dbHelper = new DataBaseHelper(getContext());
        boolean success = dbHelper.updateFlight(flight.getFlightNumber(), flightNumber, departurePlace, destination,
                departureDate, departureTime, arrivalDate, arrivalTime, duration, aircraftModel, maxSeats,
                bookingOpenDate, priceEconomy, priceBusiness, priceExtraBaggage, recurrent);

        // Show success or failure message
        if (success) {
            Toast.makeText(getContext(), "Flight updated successfully!", Toast.LENGTH_SHORT).show();
            // Notify the listener about the update
            if (flightUpdatedListener != null) {
                flightUpdatedListener.onFlightUpdated();
            }
            dismiss(); // Close the dialog
        } else {
            Toast.makeText(getContext(), "Failed to update flight", Toast.LENGTH_SHORT).show();
        }
    }

    // Set up date and time pickers for fields
    private void setUpDateAndTimePickers() {
        setDatePicker(etDepartureDate);
        setTimePicker(etDepartureTime);
        setDatePicker(etArrivalDate);
        setTimePicker(etArrivalTime);
        setDatePicker(etBookingOpenDate);
    }

    // Set a date picker for the given EditText
    private void setDatePicker(EditText editText) {
        editText.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getContext(),
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        // Format the selected date
                        String formattedDate = String.format("%04d-%02d-%02d", year1, monthOfYear + 1, dayOfMonth);
                        editText.setText(formattedDate);

                        // Calculate and set the flight duration
                        int duration = calculateDuration(
                                etDepartureDate.getText().toString().trim(),
                                etDepartureTime.getText().toString().trim(),
                                etArrivalDate.getText().toString().trim(),
                                etArrivalTime.getText().toString().trim()
                        );
                        if (duration != -1) {
                            etDuration.setText(String.valueOf(duration)); // Set duration in minutes
                        } else {
                            etDuration.setText(""); // Clear duration if calculation failed
                        }
                    },
                    year, month, day);
            datePickerDialog.show();
        });
    }

    // Set a time picker for the given EditText
    private void setTimePicker(EditText editText) {
        editText.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    getContext(),
                    (view, hourOfDay, minute1) -> {
                        // Format the selected time
                        String formattedTime = String.format("%02d:%02d", hourOfDay, minute1);
                        editText.setText(formattedTime);

                        // Calculate and set the flight duration
                        int duration = calculateDuration(
                                etDepartureDate.getText().toString().trim(),
                                etDepartureTime.getText().toString().trim(),
                                etArrivalDate.getText().toString().trim(),
                                etArrivalTime.getText().toString().trim()
                        );
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

    // Calculate the flight duration in minutes based on departure and arrival date/time
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

    // Set the spinner selection based on the provided value
    private void setSpinnerSelection(Spinner spinner, String value) {
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinner.getAdapter();
        if (adapter != null) {
            int position = adapter.getPosition(value);
            spinner.setSelection(position);
        }
    }
}
