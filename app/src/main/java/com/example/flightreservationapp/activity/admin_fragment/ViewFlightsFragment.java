package com.example.flightreservationapp.activity.admin_fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.flightreservationapp.R;
import com.example.flightreservationapp.database.DataBaseHelper;
import com.example.flightreservationapp.model.Flight;
import com.example.flightreservationapp.utility.FlightAdapter;

import java.util.ArrayList;
import java.util.Date;

public class ViewFlightsFragment extends Fragment {

    private ListView lvOpenFlights;
    private DataBaseHelper dbHelper;
    private ArrayList<Flight> openFlightList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_flights, container, false);

        lvOpenFlights = view.findViewById(R.id.lv_open_flights);
        dbHelper = new DataBaseHelper(getContext());
        openFlightList = new ArrayList<>();

        // Load all open flights
        loadOpenFlights();

        // Set item click listener
        lvOpenFlights.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Show dialog with flight information
                showFlightDetailsDialog(openFlightList.get(position));
            }
        });

        return view;
    }

    private void loadOpenFlights() {
        Cursor cursor = dbHelper.getAllFlights();
        openFlightList.clear();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String flightNumber = cursor.getString(cursor.getColumnIndexOrThrow("FLIGHT_NUMBER"));
                String departurePlace = cursor.getString(cursor.getColumnIndexOrThrow("DEPARTURE_PLACE"));
                String destination = cursor.getString(cursor.getColumnIndexOrThrow("DESTINATION"));
                String aircraftModel = cursor.getString(cursor.getColumnIndexOrThrow("AIRCRAFT_MODEL"));
                String departureDate = cursor.getString(cursor.getColumnIndexOrThrow("DEPARTURE_DATE"));
                String departureTime = cursor.getString(cursor.getColumnIndexOrThrow("DEPARTURE_TIME"));
                String arrivalDate = cursor.getString(cursor.getColumnIndexOrThrow("ARRIVAL_DATE"));
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

                Log.d("=====>>>",cursor.getString(cursor.getColumnIndexOrThrow("DEPARTURE_DATE")));

                Flight flight = new Flight( destination,  departureDate,  arrivalDate,
                        duration,  flightNumber,  departurePlace,  departureTime,  arrivalTime,
                        aircraftModel,  currentReservations,  maxSeats,  missedFlights,  bookingOpenDate,
                        economyClassPrice ,businessClassPrice,extraBaggagePrice,recurrent);;
                openFlightList.add(flight);

            } while (cursor.moveToNext());
            cursor.close();
        }

        FlightAdapter adapter = new FlightAdapter(getContext(), openFlightList);
        lvOpenFlights.setAdapter(adapter);

        if (openFlightList.isEmpty()) {
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
}
