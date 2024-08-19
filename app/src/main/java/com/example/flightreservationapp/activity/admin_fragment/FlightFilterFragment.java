package com.example.flightreservationapp.activity.admin_fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

import com.example.flightreservationapp.R;
import com.example.flightreservationapp.activity.SignUpActivity;
import com.example.flightreservationapp.database.DataBaseHelper;
import com.example.flightreservationapp.model.Flight;
import com.example.flightreservationapp.utility.FlightAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class FlightFilterFragment extends Fragment {

    private EditText etDepartureDate,etArrivalDate;
    private Spinner spDepartureCity, spArrivalCity;
    private Button btnSearchFlights;
    private ListView lvFlightResults;
    private DataBaseHelper databaseHelper;
    ArrayList<Flight> flightList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_flight_filter, container, false);

        // Initialize views
        spDepartureCity = view.findViewById(R.id.spDepartureCity);
        spArrivalCity = view.findViewById(R.id.spArrivalCity);
        etDepartureDate = view.findViewById(R.id.etDepartureDate);
        etArrivalDate = view.findViewById(R.id.etArrivalDate);
        btnSearchFlights = view.findViewById(R.id.btnSearchFlights);
        lvFlightResults = view.findViewById(R.id.lvFlightResults);

        setDatePicker(etDepartureDate);
        setDatePicker(etArrivalDate);

        // Initialize database helper
        databaseHelper = new DataBaseHelper(getContext());

        btnSearchFlights.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String departureCity = spDepartureCity.getSelectedItem().toString().trim();
                String arrivalCity = spArrivalCity.getSelectedItem().toString().trim();
                String departureDate = etDepartureDate.getText().toString().trim();
                String arrivalDate = etArrivalDate.getText().toString().trim();

                if (departureCity.isEmpty() || arrivalCity.isEmpty() || departureDate.isEmpty()) {
                    Toast.makeText(getContext(), "Please enter all fields", Toast.LENGTH_SHORT).show();
                } else {
                    searchFlights(departureCity, arrivalCity, departureDate,arrivalDate);
                }
            }
        });

        // Set item click listener
        lvFlightResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Show dialog with flight information
                showFlightDetailsDialog(flightList.get(position));
            }
        });

        return view;
    }

    private void searchFlights(String departureCity, String arrivalCity, String departureDate,String arrivalDate) {
        Cursor cursor = databaseHelper.getAllFlights();  // Retrieve all flights or use custom query
        flightList = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String flightNumber = cursor.getString(cursor.getColumnIndexOrThrow("FLIGHT_NUMBER"));
                String depPlace = cursor.getString(cursor.getColumnIndexOrThrow("DEPARTURE_PLACE"));
                String destPlace = cursor.getString(cursor.getColumnIndexOrThrow("DESTINATION"));
                String depDate = cursor.getString(cursor.getColumnIndexOrThrow("DEPARTURE_DATE"));
                String destDate = cursor.getString(cursor.getColumnIndexOrThrow("DEPARTURE_DATE"));

                // Apply filtering
                if (depPlace.equalsIgnoreCase(departureCity) && destPlace.equalsIgnoreCase(arrivalCity) && depDate.equals(departureDate) && destDate.equals(arrivalDate)) {
                    String aircraftModel = cursor.getString(cursor.getColumnIndexOrThrow("AIRCRAFT_MODEL"));
                    String departureTime = cursor.getString(cursor.getColumnIndexOrThrow("DEPARTURE_TIME"));
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

                    Flight flight = new Flight( destPlace,  depDate,  destDate,
                            duration,  flightNumber,  depPlace,  departureTime,  arrivalTime,
                            aircraftModel,  currentReservations,  maxSeats,  missedFlights,  bookingOpenDate,
                            economyClassPrice ,businessClassPrice,extraBaggagePrice,recurrent);
                    flightList.add(flight);
                }
            } while (cursor.moveToNext());
        }

        // Set the adapter to the ListView
        FlightAdapter adapter = new FlightAdapter(getContext(), flightList);
        lvFlightResults.setAdapter(adapter);

        if (flightList.isEmpty()) {
            Toast.makeText(getContext(), "No flights found", Toast.LENGTH_SHORT).show();
        }
    }


    private void showFlightDetailsDialog(Flight flight) {
        String flightDetails = "Flight Number: " + flight.getFlightNumber() + "\n" +
                "Departure Place: " + flight.getDeparturePlace() + "\n" +
                "Destination: " + flight.getDestination() + "\n" +
                "Aircraft Model: " + flight.getAircraftModel() + "\n" +
                "Departure Date: " + flight.getDepartureDate() + "\n" +
                "Departure Time: " + flight.getDepartureTime() + "\n" +
                "Arrival Date: " + flight.getArrivalDate() + "\n" +
                "Arrival Time: " + flight.getArrivalTime() + "\n" +
                "Duration: " + flight.getDuration() + " minutes\n" +
                "Max Seats: " + flight.getMaxSeats() + "\n" +
                "Booking Open Date: " + flight.getBookingOpenDate() + "\n" +
                "Economy Class Price: " + flight.getEconomyClassPrice() + "\n" +
                "Business Class Price: " + flight.getBusinessClassPrice() + "\n" +
                "Extra Baggage Price: " + flight.getExtraBaggagePrice() + "\n" +
                "Recurrent: " + flight.getRecurrent()+"\n" +
                "Current Reservations: " + flight.getCurrentReservations()+"\n" +
                "Missed Flights: " + flight.getMissedFlights();

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Flight Details")
                .setMessage(flightDetails)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();
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


                        // Validate against etDepartureDate
                        if (editText == etDepartureDate) {
                            if (!isDateValid(formattedDate)) {
                                Toast.makeText(getContext(), "Date cannot be earlier than today", Toast.LENGTH_SHORT).show();
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
                        }
                        editText.setText(formattedDate);
                    },
                    year, month, day);
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
}