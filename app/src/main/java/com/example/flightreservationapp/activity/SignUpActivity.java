package com.example.flightreservationapp.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.flightreservationapp.R;
import com.example.flightreservationapp.database.DataBaseHelper;
import com.example.flightreservationapp.model.User;

import java.util.Calendar;

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

    private TextView titleLabel;
    private TextView passportNumberLabel;
    private TextView passportIssueDateLabel;
    private TextView passportIssuePlaceLabel;
    private TextView passportExpirationDateLabel;
    private TextView foodPreferenceLabel;
    private TextView dateOfBirthLabel;
    private TextView nationalityLabel;

    LinearLayout mainLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initializing views
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

        // Initializing labels
        titleLabel = findViewById(R.id.titleLabel);
        passportNumberLabel = findViewById(R.id.passportNumberLabel);
        passportIssueDateLabel = findViewById(R.id.passportIssueDateLabel);
        passportIssuePlaceLabel = findViewById(R.id.passportIssuePlaceLabel);
        passportExpirationDateLabel = findViewById(R.id.passportExpirationDateLabel);
        foodPreferenceLabel = findViewById(R.id.foodPreferenceLabel);
        dateOfBirthLabel = findViewById(R.id.dateOfBirthLabel);
        nationalityLabel = findViewById(R.id.nationalityLabel);

        mainLinearLayout = findViewById(R.id.mainLinearLayout);

        dataBaseHelper = new DataBaseHelper(this);

        role = getIntent().getStringExtra("role");

        // Set title based on role
        if (role.equals("Admin")) {
            titleLabel.setText("Sign In as Admin");
            hidePassengerFields();
            centerLinearLayoutVertically(); // Center vertically if Admin
        } else {
            titleLabel.setText("Sign In as Passenger");
            centerLinearLayoutHorizontally(); // Center horizontally if Passenger
        }

        setUpDatePickers();
        signUpButton.setOnClickListener(view -> signUp());
    }

    private void centerLinearLayoutVertically() {
        // Check parent layout
        ViewGroup parent = (ViewGroup) mainLinearLayout.getParent();
        if (parent instanceof FrameLayout) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    800,
                    FrameLayout.LayoutParams.WRAP_CONTENT
            );
            params.gravity = Gravity.CENTER;
            mainLinearLayout.setLayoutParams(params);
        } else if (parent instanceof LinearLayout) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    800,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.gravity = Gravity.CENTER;
            mainLinearLayout.setLayoutParams(params);
        }
    }

    private void centerLinearLayoutHorizontally() {
        // Check parent layout
        ViewGroup parent = (ViewGroup) mainLinearLayout.getParent();
        if (parent instanceof FrameLayout) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    800,
                    FrameLayout.LayoutParams.WRAP_CONTENT
            );
            params.gravity = Gravity.CENTER_HORIZONTAL;
            mainLinearLayout.setLayoutParams(params);
        } else if (parent instanceof LinearLayout) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    800,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.gravity = Gravity.CENTER_HORIZONTAL;
            mainLinearLayout.setLayoutParams(params);
        }
    }

    private void hidePassengerFields() {
        // Hiding EditTexts and corresponding labels
        passportNumberEditText.setVisibility(EditText.GONE);
        passportIssueDateEditText.setVisibility(EditText.GONE);
        passportIssuePlaceEditText.setVisibility(EditText.GONE);
        passportExpirationDateEditText.setVisibility(EditText.GONE);
        foodPreferenceEditText.setVisibility(EditText.GONE);
        dateOfBirthEditText.setVisibility(EditText.GONE);
        nationalityEditText.setVisibility(EditText.GONE);

        passportNumberLabel.setVisibility(TextView.GONE);
        passportIssueDateLabel.setVisibility(TextView.GONE);
        passportIssuePlaceLabel.setVisibility(TextView.GONE);
        passportExpirationDateLabel.setVisibility(TextView.GONE);
        foodPreferenceLabel.setVisibility(TextView.GONE);
        dateOfBirthLabel.setVisibility(TextView.GONE);
        nationalityLabel.setVisibility(TextView.GONE);
    }

    private void signUp() {
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        // Validate email
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Please enter a valid email address");
            emailEditText.requestFocus();
            return;
        }

        // Validate phone number (assuming a 10-digit format)
        if (phone.isEmpty() || !phone.matches("\\d{10}")) {
            phoneEditText.setError("Please enter a valid 10-digit phone number");
            phoneEditText.requestFocus();
            return;
        }

        // Validate first name length
        if (firstName.isEmpty() || firstName.length() < 3 || firstName.length() > 20) {
            firstNameEditText.setError("First name must be between 3 and 20 characters");
            firstNameEditText.requestFocus();
            return;
        }

        // Validate last name length
        if (lastName.isEmpty() || lastName.length() < 3 || lastName.length() > 20) {
            lastNameEditText.setError("Last name must be between 3 and 20 characters");
            lastNameEditText.requestFocus();
            return;
        }

        // Validate password
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#%$!^&+=]).{8,15}$";
        if (password.isEmpty() || !password.matches(passwordPattern)) {
            passwordEditText.setError("Password must be 8-15 characters long and include at least one number, one uppercase letter, one lowercase letter, and one special character.");
            passwordEditText.requestFocus();
            return;
        }

        // Validate confirm password
        if (confirmPassword.isEmpty() || !confirmPassword.equals(password)) {
            confirmPasswordEditText.setError("Passwords do not match");
            confirmPasswordEditText.requestFocus();
            return;
        }

        // Create a new user object
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setPhone(phone);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRole(role);

        if (role.equals("Passenger")) {
            user.setPassportNumber(passportNumberEditText.getText().toString().trim());
            user.setPassportIssueDate(passportIssueDateEditText.getText().toString().trim());
            user.setPassportIssuePlace(passportIssuePlaceEditText.getText().toString().trim());
            user.setPassportExpirationDate(passportExpirationDateEditText.getText().toString().trim());
            user.setFoodPreference(foodPreferenceEditText.getText().toString().trim());
            user.setDateOfBirth(dateOfBirthEditText.getText().toString().trim());
            user.setNationality(nationalityEditText.getText().toString().trim());
        }

        // Save user to database
        dataBaseHelper.insertUser(user);
        Toast.makeText(SignUpActivity.this, "Sign up successful", Toast.LENGTH_SHORT).show();
        finish();
    }


    private void setUpDatePickers() {
        setDatePicker(passportIssueDateEditText);
        setDatePicker(passportExpirationDateEditText);
        setDatePicker(dateOfBirthEditText);
    }


    private void setDatePicker(EditText editText) {
        editText.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    SignUpActivity.this,
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        // Format the month and day to always be two digits
                        String formattedDate = String.format("%04d-%02d-%02d", year1, monthOfYear + 1, dayOfMonth);
                        editText.setText(formattedDate);
                    },
                    year, month, day);
            datePickerDialog.show();
        });
    }

}
