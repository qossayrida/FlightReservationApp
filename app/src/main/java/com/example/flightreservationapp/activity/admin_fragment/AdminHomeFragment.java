package com.example.flightreservationapp.activity.admin_fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import androidx.fragment.app.Fragment;
import com.example.flightreservationapp.R;
import com.example.flightreservationapp.activity.shared_fragment.ClosestDepartureDatesFragment;
import com.example.flightreservationapp.database.DataBaseHelper;
import com.example.flightreservationapp.model.Flight;
import com.example.flightreservationapp.utility.FlightAdapter;

import java.util.ArrayList;

public class AdminHomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);

        // Load ClosestDepartureDatesFragment into the container inside AdminHomeFragment
        getChildFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_admin, new ClosestDepartureDatesFragment()) // Replace the fragment container with ClosestDepartureDatesFragment
                .commit();
        // Commit the transaction to display the fragment

        return view; // Return the inflated view
    }
}
