package com.example.flightreservationapp.activity;


import android.content.Intent;
import android.database.Cursor;
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

        loadFlightData();

        // Load and start the animation
        Animation rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        rotatingImageView.startAnimation(rotateAnimation);
    }

    private void loadFlightData() {
        try {
            List<Flight> flights = APIService.getFlights(new Random().nextBoolean());
        } catch (Exception e) {
            // Redirect to "Failed to Login" activity
            Intent intent = new Intent(MainActivity.this, FailedLoginActivity.class);
            startActivity(intent);
        }
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
        } else {
            Toast.makeText(MainActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
        }
    }


    private void signUp() {
        Intent intent = new Intent(this, ChooseRoleActivity.class);
        startActivity(intent);
    }
}

