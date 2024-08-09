package com.example.flightreservationapp.utility;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.flightreservationapp.R;
import com.example.flightreservationapp.model.Flight;

import java.util.ArrayList;

public class FlightAdapter extends ArrayAdapter<Flight> {
    public FlightAdapter(Context context, ArrayList<Flight> flights) {
        super(context, 0, flights);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Flight flight = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_flight, parent, false);
        }

        // Lookup view for data population
        TextView tvFlightNumber = convertView.findViewById(R.id.tv_flight_number);
        TextView tvDepartureDestination = convertView.findViewById(R.id.tv_departure_destination);
        TextView tvAircraftModel = convertView.findViewById(R.id.tv_aircraft_model);

        // Populate the data into the template view using the data object
        tvFlightNumber.setText(flight.getFlightNumber());
        tvDepartureDestination.setText(flight.getDeparturePlace() + " -> " + flight.getDestination());
        tvAircraftModel.setText(flight.getAircraftModel());

        // Return the completed view to render on screen
        return convertView;
    }
}
