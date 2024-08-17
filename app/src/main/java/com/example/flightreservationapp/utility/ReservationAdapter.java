package com.example.flightreservationapp.utility;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.flightreservationapp.R;
import com.example.flightreservationapp.model.Reservation;

import java.util.ArrayList;

public class ReservationAdapter extends ArrayAdapter<Reservation> {

    public ReservationAdapter(Context context, ArrayList<Reservation> reservations) {
        super(context, 0, reservations);
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
        TextView tvFoodPreference = convertView.findViewById(R.id.tv_food_preference);
        TextView tvTotalCost = convertView.findViewById(R.id.tv_total_cost);

        tvReservationId.setText("Reservation ID: " + reservation.getReservationId());
        tvPassportNumber.setText("Passport Number: " + reservation.getPassportNumber());
        tvFlightClass.setText("Class: " + reservation.getFlightClass());
        tvExtraBags.setText("Extra Bags: " + reservation.getExtraBags());
        tvFoodPreference.setText("Food Preference: " + reservation.getFoodPreference());
        tvTotalCost.setText("Total Cost: $" + reservation.getTotalCost());

        return convertView;
    }
}

