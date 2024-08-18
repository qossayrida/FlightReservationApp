package com.example.flightreservationapp.utility;


import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.flightreservationapp.R;
import com.example.flightreservationapp.database.DataBaseHelper;
import com.example.flightreservationapp.model.Reservation;

import java.util.ArrayList;

public class ReservationAdapter extends ArrayAdapter<Reservation> {

    private DataBaseHelper dbHelper;

    public ReservationAdapter(Context context, ArrayList<Reservation> reservations) {
        super(context, 0, reservations);
        dbHelper = new DataBaseHelper(context); // Initialize the database helper
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Reservation reservation = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_reservation, parent, false);
        }

        TextView tvReservationId = convertView.findViewById(R.id.tv_reservation_id);
        TextView tvPassportNumber = convertView.findViewById(R.id.tv_passport_number);
        TextView tvFlightClass = convertView.findViewById(R.id.tv_flight_class);
        TextView tvExtraBags = convertView.findViewById(R.id.tv_extra_bags);
        TextView tvTotalCost = convertView.findViewById(R.id.tv_total_cost);
        TextView tvDepartureDestination = convertView.findViewById(R.id.tv_departure_destination); // New TextView for Departure and Destination

        tvReservationId.setText("Reservation ID: " + reservation.getReservationId());
        tvPassportNumber.setText("Passport Number: " + reservation.getPassportNumber());
        tvFlightClass.setText("Class: " + reservation.getFlightClass());
        tvExtraBags.setText("Extra Bags: " + reservation.getExtraBags());
        tvTotalCost.setText("Total Cost: $" + reservation.getTotalCost());

        // Fetch the flight details using the flight number
        Cursor flightCursor = dbHelper.getFlightDetailsByFlightNumber(reservation.getFlightNumber());
        if (flightCursor != null && flightCursor.moveToFirst()) {
            String departurePlace = flightCursor.getString(flightCursor.getColumnIndexOrThrow("DEPARTURE_PLACE"));
            String destination = flightCursor.getString(flightCursor.getColumnIndexOrThrow("DESTINATION"));

            tvDepartureDestination.setText("Flight: " + departurePlace + " -> " + destination);

            flightCursor.close();
        }

        return convertView;
    }
}
