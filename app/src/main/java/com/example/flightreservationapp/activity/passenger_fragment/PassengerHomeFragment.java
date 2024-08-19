package com.example.flightreservationapp.activity.passenger_fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.flightreservationapp.R;
import com.example.flightreservationapp.activity.shared_fragment.ClosestDepartureDatesFragment;

public class PassengerHomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_passenger_home, container, false);

        // Load ClosestDepartureDatesFragment into PassengerHomeFragment
        getChildFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_passenger, new ClosestDepartureDatesFragment())
                .commit();

        return view;
    }
}
