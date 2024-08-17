package com.example.flightreservationapp.activity;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.flightreservationapp.R;
import com.example.flightreservationapp.database.*;
import com.example.flightreservationapp.utility.*;
import com.example.flightreservationapp.model.*;
import com.example.flightreservationapp.service.*;
import java.util.*;

public class MainActivity extends AppCompatActivity {
    //mohammad and qossay

    private EditText emailEditText;
    private EditText passwordEditText;
    private CheckBox rememberMeCheckBox;
    private Button signInButton;
    private Button signUpButton;
    private SharedPrefManager sharedPrefManager;
    private DataBaseHelper dataBaseHelper;
    private ImageView rotatingImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        rememberMeCheckBox = findViewById(R.id.rememberMeCheckBox);
        signInButton = findViewById(R.id.signInButton);
        signUpButton = findViewById(R.id.signUpButton);
        rotatingImageView = findViewById(R.id.rotatingImageView);


        signInButton.setEnabled(false);
        signUpButton.setEnabled(false);
        loadFlightData();


        sharedPrefManager = SharedPrefManager.getInstance(this);
        dataBaseHelper = new DataBaseHelper(this);

        // Load saved credentials if they exist
        String savedUsername = sharedPrefManager.readString("userName", "");
        String savedPassword = sharedPrefManager.readString("password", "");
        if (!savedUsername.isEmpty() && !savedPassword.isEmpty()) {
            emailEditText.setText(savedUsername);
            passwordEditText.setText(savedPassword);
            rememberMeCheckBox.setChecked(true);
        }

        signInButton.setOnClickListener(view -> signIn());
        signUpButton.setOnClickListener(view -> signUp());



        // Load and start the animation
        Animation rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        rotatingImageView.startAnimation(rotateAnimation);
    }

    private void loadFlightData() {
        try {
            ConnectionAsyncTask connectionAsyncTask = new ConnectionAsyncTask(MainActivity.this);
            connectionAsyncTask.execute("https://mocki.io/v1/e29b3bbe-9af1-4758-ba71-5e13ae3dc8b4");
        } catch (Exception e) {
            redirectToFailedLogin();
        }
    }

    public void enableButtons() {
        signInButton.setEnabled(true);
        signUpButton.setEnabled(true);
    }

    public void redirectToFailedLogin() {
        Intent intent = new Intent(MainActivity.this, FailedLoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void signIn() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(MainActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor cursor = dataBaseHelper.getUser(email, password);
        if (cursor != null && cursor.moveToFirst()) {
            if (rememberMeCheckBox.isChecked()) {
                sharedPrefManager.writeString("userName", emailEditText.getText().toString());
                sharedPrefManager.writeString("password", passwordEditText.getText().toString());
            }

            User user = new User();

            // Get column indices safely
            int emailIndex = cursor.getColumnIndex("EMAIL");
            int passwordIndex = cursor.getColumnIndex("PASSWORD");
            int phoneIndex = cursor.getColumnIndex("PHONE");
            int firstNameIndex = cursor.getColumnIndex("FIRST_NAME");
            int lastNameIndex = cursor.getColumnIndex("LAST_NAME");
            int roleIndex = cursor.getColumnIndex("ROLE");

            // Ensure that indices are valid (i.e., not -1) before accessing cursor data
            if (emailIndex != -1) user.setEmail(cursor.getString(emailIndex));
            if (passwordIndex != -1) user.setPassword(cursor.getString(passwordIndex));
            if (phoneIndex != -1) user.setPhone(cursor.getString(phoneIndex));
            if (firstNameIndex != -1) user.setFirstName(cursor.getString(firstNameIndex));
            if (lastNameIndex != -1) user.setLastName(cursor.getString(lastNameIndex));
            if (roleIndex != -1) user.setRole(cursor.getString(roleIndex));

            if (user.getRole().equals("Passenger")) {
                // Get additional columns safely for "Passenger"
                int passportNumberIndex = cursor.getColumnIndex("PASSPORT_NUMBER");
                int passportIssueDateIndex = cursor.getColumnIndex("PASSPORT_ISSUE_DATE");
                int passportIssuePlaceIndex = cursor.getColumnIndex("PASSPORT_ISSUE_PLACE");
                int passportExpirationDateIndex = cursor.getColumnIndex("PASSPORT_EXPIRATION_DATE");
                int foodPreferenceIndex = cursor.getColumnIndex("FOOD_PREFERENCE");
                int dateOfBirthIndex = cursor.getColumnIndex("DATE_OF_BIRTH");
                int nationalityIndex = cursor.getColumnIndex("NATIONALITY");

                // Ensure indices are valid before accessing cursor data
                if (passportNumberIndex != -1) user.setPassportNumber(cursor.getString(passportNumberIndex));
                if (passportIssueDateIndex != -1) user.setPassportIssueDate(cursor.getString(passportIssueDateIndex));
                if (passportIssuePlaceIndex != -1) user.setPassportIssuePlace(cursor.getString(passportIssuePlaceIndex));
                if (passportExpirationDateIndex != -1) user.setPassportExpirationDate(cursor.getString(passportExpirationDateIndex));
                if (foodPreferenceIndex != -1) user.setFoodPreference(cursor.getString(foodPreferenceIndex));
                if (dateOfBirthIndex != -1) user.setDateOfBirth(cursor.getString(dateOfBirthIndex));
                if (nationalityIndex != -1) user.setNationality(cursor.getString(nationalityIndex));
            }


            String userJson = JsonConverter.userToJson(user);
            sharedPrefManager.writeString("userJson", userJson);

            Intent intent = new Intent(this, NavigationDrawerActivity.class);
            startActivity(intent);
            finish(); // Close MainActivity
        } else {
            Toast.makeText(MainActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
        }
    }


    private void signUp() {
        Intent intent = new Intent(this, ChooseRoleActivity.class);
        startActivity(intent);
    }

    public void addToDataBase(List<Flight> flights) {
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

        try {
            for (Flight flight : flights) {
                ContentValues values = new ContentValues();
                values.put("FLIGHT_NUMBER", flight.getFlightNumber());
                values.put("DEPARTURE_PLACE", flight.getDeparturePlace());
                values.put("DESTINATION", flight.getDestination());
                values.put("DEPARTURE_DATE", flight.getDepartureDate().getTime());
                values.put("DEPARTURE_TIME", flight.getDepartureTime().getTime());
                values.put("ARRIVAL_DATE", flight.getArrivalDate().getTime());
                values.put("ARRIVAL_TIME", flight.getArrivalTime().getTime());
                values.put("DURATION", flight.getDuration());
                values.put("AIRCRAFT_MODEL", flight.getAircraftModel());
                values.put("MAX_SEATS", flight.getMaxSeats());
                values.put("BOOKING_OPEN_DATE", flight.getBookingOpenDate().getTime());
                values.put("ECONOMY_CLASS_PRICE", flight.getEconomyClassPrice());
                values.put("BUSINESS_CLASS_PRICE", flight.getBusinessClassPrice());
                values.put("EXTRA_BAGGAGE_PRICE", flight.getExtraBaggagePrice());
                values.put("RECURRENT", flight.getRecurrent().toString());
                values.put("CURRENT_RESERVATIONS", flight.getCurrentReservations());
                values.put("MISSED_FLIGHTS", flight.getMissedFlights());

                db.insert("FLIGHTS", null, values);
            }
        } catch (Exception e) {
        } finally {
            db.close();
        }
    }
}

