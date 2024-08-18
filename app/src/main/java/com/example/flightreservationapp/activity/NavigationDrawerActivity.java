package com.example.flightreservationapp.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.flightreservationapp.model.*;
import com.example.flightreservationapp.utility.*;
import com.google.android.material.navigation.NavigationView;

import com.example.flightreservationapp.R;

import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.flightreservationapp.databinding.ActivityNavigationDrawerBinding;

public class NavigationDrawerActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityNavigationDrawerBinding binding;
    private SharedPrefManager sharedPrefManager;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNavigationDrawerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarNavigationDrawer.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        sharedPrefManager = SharedPrefManager.getInstance(this);
        String savedUserJson = sharedPrefManager.readString("userJson", null);
        User savedUser = null;
        if (savedUserJson != null) {
            savedUser = JsonConverter.jsonToUser(savedUserJson);
        }

        if (savedUser != null) {
            View headerView = navigationView.getHeaderView(0);

            TextView headerTitle = headerView.findViewById(R.id.textViewForName);
            TextView headerSubtitle = headerView.findViewById(R.id.textViewForEmail);

            headerTitle.setText(savedUser.getFirstName() + " " + savedUser.getLastName());
            headerSubtitle.setText(savedUser.getEmail());
        }

        if (savedUser != null && savedUser.getRole().equals("Admin")) {
            navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_navigation_drawer);
            navController.setGraph(R.navigation.nav_admin_navigation);
            navigationView.inflateMenu(R.menu.activity_admin_drawer);

            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_admin_home, R.id.nav_create_flight, R.id.nav_edit_or_remove_flight, R.id.nav_view_flights_available,
                    R.id.nav_view_flights_not_available,R.id.nav_view_all_reservations,R.id.nav_view_flights_archive,R.id.nav_filter_flight)
                    .setOpenableLayout(drawer)
                    .build();
        } else if (savedUser != null && savedUser.getRole().equals("Passenger")) {
            navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_navigation_drawer);
            navController.setGraph(R.navigation.nav_passenger_navigation);
            navigationView.inflateMenu(R.menu.activity_passenger_drawer);

            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_passenger_home,R.id.nav_search_flights,R.id.nav_view_current_reservations,R.id.nav_view_previous_reservations)
                    .setOpenableLayout(drawer)
                    .build();
        }

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_logout) {
                logout();
            } else {
                NavigationUI.onNavDestinationSelected(item, navController);
            }

            drawer.closeDrawer(GravityCompat.START);
            return true;
        });
    }


    private void logout() {
        // Handle the logout logic here (e.g., clear user session, etc.)

        // Start the MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // Finish this activity
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


}
