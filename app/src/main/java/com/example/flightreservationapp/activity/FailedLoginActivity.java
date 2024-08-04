package com.example.flightreservationapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.flightreservationapp.R;

public class FailedLoginActivity extends AppCompatActivity {

    private Button retryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_failed_login);

        retryButton = findViewById(R.id.retryButton);
        retryButton.setOnClickListener(view -> retryConnection());
    }

    private void retryConnection() {
        Intent intent = new Intent(FailedLoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
