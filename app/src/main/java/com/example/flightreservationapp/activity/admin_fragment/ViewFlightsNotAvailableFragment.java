package com.example.flightreservationapp.activity.admin_fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.example.flightreservationapp.R;
import com.example.flightreservationapp.database.DataBaseHelper;
import com.example.flightreservationapp.model.Flight;
import com.example.flightreservationapp.utility.FlightAdapter;

import java.util.ArrayList;
import java.util.Date;

public class ViewFlightsNotAvailableFragment extends Fragment {

    private ListView lvFlights;
    private DataBaseHelper dbHelper;
    private ArrayList<Flight> flightList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_flights_not_available, container, false);

        lvFlights = view.findViewById(R.id.lv_flights_not_available);
        dbHelper = new DataBaseHelper(getContext());
        flightList = new ArrayList<>();

        // Load all flights not yet open for reservation
        loadFlightsNotOpenForReservation();

        // Set item click listener
        lvFlights.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Show dialog with flight information
                showFlightDetailsDialog(flightList.get(position));
            }
        });

        return view;
    }

    private void loadFlightsNotOpenForReservation() {
        Cursor cursor = dbHelper.getFlightsNotOpenForReservation();
        flightList.clear();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String flightNumber = cursor.getString(cursor.getColumnIndexOrThrow("FLIGHT_NUMBER"));
                String departurePlace = cursor.getString(cursor.getColumnIndexOrThrow("DEPARTURE_PLACE"));
                String destination = cursor.getString(cursor.getColumnIndexOrThrow("DESTINATION"));
                String aircraftModel = cursor.getString(cursor.getColumnIndexOrThrow("AIRCRAFT_MODEL"));
                Date departureDate = new Date(cursor.getLong(cursor.getColumnIndexOrThrow("DEPARTURE_DATE")));
                Date departureTime = new Date(cursor.getLong(cursor.getColumnIndexOrThrow("DEPARTURE_TIME")));
                Date arrivalDate = new Date(cursor.getLong(cursor.getColumnIndexOrThrow("ARRIVAL_DATE")));
                Date arrivalTime = new Date(cursor.getLong(cursor.getColumnIndexOrThrow("ARRIVAL_TIME")));
                int duration = cursor.getInt(cursor.getColumnIndexOrThrow("DURATION"));
                int maxSeats = cursor.getInt(cursor.getColumnIndexOrThrow("MAX_SEATS"));
                Date bookingOpenDate = new Date(cursor.getLong(cursor.getColumnIndexOrThrow("BOOKING_OPEN_DATE")));
                double economyClassPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("ECONOMY_CLASS_PRICE"));
                double businessClassPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("BUSINESS_CLASS_PRICE"));
                double extraBaggagePrice = cursor.getDouble(cursor.getColumnIndexOrThrow("EXTRA_BAGGAGE_PRICE"));
                Flight.RecurrentType recurrent = Flight.RecurrentType.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("RECURRENT")));

                Flight flight = new Flight(flightNumber, departurePlace, destination, departureDate, departureTime,
                        arrivalDate, arrivalTime, duration, aircraftModel, maxSeats,
                        bookingOpenDate, economyClassPrice, businessClassPrice, extraBaggagePrice, recurrent);
                flightList.add(flight);

            } while (cursor.moveToNext());
            cursor.close();
        }

        FlightAdapter adapter = new FlightAdapter(getContext(), flightList);
        lvFlights.setAdapter(adapter);

        if (flightList.isEmpty()) {
            Toast.makeText(getContext(), "No flights available for reservation", Toast.LENGTH_SHORT).show();
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
                "Recurrent: " + flight.getRecurrent();

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Flight Details")
                .setMessage(flightDetails)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}
