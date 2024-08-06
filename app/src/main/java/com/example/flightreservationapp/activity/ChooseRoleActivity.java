package com.example.flightreservationapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.flightreservationapp.R;

public class ChooseRoleActivity extends AppCompatActivity {

    private Button adminButton;
    private Button passengerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_role);

        adminButton = findViewById(R.id.adminButton);
        passengerButton = findViewById(R.id.passengerButton);

        adminButton.setOnClickListener(view -> {
            Intent intent = new Intent(ChooseRoleActivity.this, SignUpActivity.class);
            intent.putExtra("role", "Admin");
            startActivity(intent);
        });

        passengerButton.setOnClickListener(view -> {
            Intent intent = new Intent(ChooseRoleActivity.this, SignUpActivity.class);
            intent.putExtra("role", "Passenger");
            startActivity(intent);
        });
    }
}
