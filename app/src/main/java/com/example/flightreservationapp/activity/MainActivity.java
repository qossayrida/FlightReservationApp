package com.example.flightreservationapp.activity;

import android.content.Intent;
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
import com.example.flightreservationapp.model.Flight;
import com.example.flightreservationapp.service.APIService;
import com.example.flightreservationapp.utility.SharedPrefManager;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private CheckBox rememberMeCheckBox;
    private Button signInButton;
    private Button signUpButton;
    private SharedPrefManager sharedPrefManager;
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
        // Handle sign-in logic
        if (rememberMeCheckBox.isChecked()) {
            sharedPrefManager.writeString("userName", emailEditText.getText().toString());
            sharedPrefManager.writeString("password", passwordEditText.getText().toString());
            Toast.makeText(MainActivity.this, "Values written to Shared Preferences", Toast.LENGTH_SHORT).show();
        }
    }

    private void signUp() {
        // Redirect to sign-up activity
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }
}
