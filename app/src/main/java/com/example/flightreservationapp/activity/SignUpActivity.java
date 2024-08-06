package com.example.flightreservationapp.activity;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.flightreservationapp.R;
import com.example.flightreservationapp.database.DataBaseHelper;
import com.example.flightreservationapp.model.User;

public class SignUpActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText phoneEditText;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private EditText passportNumberEditText;
    private EditText passportIssueDateEditText;
    private EditText passportIssuePlaceEditText;
    private EditText passportExpirationDateEditText;
    private EditText foodPreferenceEditText;
    private EditText dateOfBirthEditText;
    private EditText nationalityEditText;
    private Button signUpButton;
    private String role;
    private DataBaseHelper dataBaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        passportNumberEditText = findViewById(R.id.passportNumberEditText);
        passportIssueDateEditText = findViewById(R.id.passportIssueDateEditText);
        passportIssuePlaceEditText = findViewById(R.id.passportIssuePlaceEditText);
        passportExpirationDateEditText = findViewById(R.id.passportExpirationDateEditText);
        foodPreferenceEditText = findViewById(R.id.foodPreferenceEditText);
        dateOfBirthEditText = findViewById(R.id.dateOfBirthEditText);
        nationalityEditText = findViewById(R.id.nationalityEditText);
        signUpButton = findViewById(R.id.signUpButton);

        dataBaseHelper = new DataBaseHelper(this);

        role = getIntent().getStringExtra("role");
        if (role.equals("Admin")) {
            hidePassengerFields();
        }

        signUpButton.setOnClickListener(view -> signUp());
    }

    private void hidePassengerFields() {
        passportNumberEditText.setVisibility(EditText.GONE);
        passportIssueDateEditText.setVisibility(EditText.GONE);
        passportIssuePlaceEditText.setVisibility(EditText.GONE);
        passportExpirationDateEditText.setVisibility(EditText.GONE);
        foodPreferenceEditText.setVisibility(EditText.GONE);
        dateOfBirthEditText.setVisibility(EditText.GONE);
        nationalityEditText.setVisibility(EditText.GONE);
    }

    private void signUp() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();
        String phone = phoneEditText.getText().toString();
        String firstName = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || phone.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
            Toast.makeText(SignUpActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(SignUpActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setPhone(phone);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRole(role);

        if (role.equals("Passenger")) {
            user.setPassportNumber(passportNumberEditText.getText().toString());
            user.setPassportIssueDate(passportIssueDateEditText.getText().toString());
            user.setPassportIssuePlace(passportIssuePlaceEditText.getText().toString());
            user.setPassportExpirationDate(passportExpirationDateEditText.getText().toString());
            user.setFoodPreference(foodPreferenceEditText.getText().toString());
            user.setDateOfBirth(dateOfBirthEditText.getText().toString());
            user.setNationality(nationalityEditText.getText().toString());
        }

        dataBaseHelper.insertUser(user);
        Toast.makeText(SignUpActivity.this, "Sign up successful", Toast.LENGTH_SHORT).show();
        finish();
    }
}
