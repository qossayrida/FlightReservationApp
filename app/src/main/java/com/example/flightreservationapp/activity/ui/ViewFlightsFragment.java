package com.example.flightreservationapp.activity.ui;


import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

                Flight flight = new Flight(flightNumber, departurePlace, destination, aircraftModel);
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
}
