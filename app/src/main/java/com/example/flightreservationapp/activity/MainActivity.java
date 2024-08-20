package com.example.flightreservationapp.activity;


import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

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

    private static final String CHANNEL_ID = "unread_notifications_channel";
    private static final int NOTIFICATION_ID = 1001;


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
            connectionAsyncTask.execute("https://mocki.io/v1/69fa4c06-0359-4427-9a86-dc840d6071c1");
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

            // Show unread notifications for the user
            if(!user.getRole().equals("Admin"))
                showUnreadNotifications(user.getPassportNumber());

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
                values.put("DEPARTURE_DATE", flight.getDepartureDate());
                values.put("DEPARTURE_TIME", flight.getDepartureTime());
                values.put("ARRIVAL_DATE", flight.getArrivalDate());
                values.put("ARRIVAL_TIME", flight.getArrivalTime());
                values.put("DURATION", flight.getDuration());
                values.put("AIRCRAFT_MODEL", flight.getAircraftModel());
                values.put("MAX_SEATS", flight.getMaxSeats());
                values.put("BOOKING_OPEN_DATE", flight.getBookingOpenDate());
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


    private void showUnreadNotifications(String passportNumber) {
        Cursor notificationsCursor = dataBaseHelper.getUnreadNotificationsByPassportNumber(passportNumber);

        if (notificationsCursor != null && notificationsCursor.moveToFirst()) {
            StringBuilder notificationsMessage = new StringBuilder();

            do {
                // Fetch notification details safely
                int messageIndex = notificationsCursor.getColumnIndex("MESSAGE");
                int timestampIndex = notificationsCursor.getColumnIndex("TIMESTAMP");

                String message = messageIndex != -1 ? notificationsCursor.getString(messageIndex) : "No message";
                String timestamp = timestampIndex != -1 ? notificationsCursor.getString(timestampIndex) : "No timestamp";

                notificationsMessage.append("Message: ").append(message).append("\n")
                        .append("Timestamp: ").append(timestamp).append("\n\n");
            } while (notificationsCursor.moveToNext());

            // Create a notification for the unread notifications
            createNotification("Unread Notifications", notificationsMessage.toString());
        } else {
            // No unread notifications
            Toast.makeText(MainActivity.this, "No unread notifications.", Toast.LENGTH_SHORT).show();
        }

        if (notificationsCursor != null) {
            notificationsCursor.close(); // Close the cursor when done
        }
    }

    private void createNotificationChannel() {
        // Notification channels are only available in Android 8.0 (API level 26) and above.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Unread Notifications";
            String description = "Channel for unread notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            // Register the channel with the system
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void createNotification(String title, String body) {
        createNotificationChannel();  // Ensure the channel is created

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_flight)  // Replace with your app's notification icon
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // Display the notification
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}

