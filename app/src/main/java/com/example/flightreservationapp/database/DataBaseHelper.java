package com.example.flightreservationapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.flightreservationapp.model.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "flight_reservation.db";
    private static final int DATABASE_VERSION = 1;

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create USERS table
        db.execSQL("CREATE TABLE USERS(" +
                "EMAIL TEXT PRIMARY KEY, " +
                "PASSWORD TEXT, " +
                "PHONE TEXT, " +
                "FIRST_NAME TEXT, " +
                "LAST_NAME TEXT, " +
                "ROLE TEXT, " +
                "PASSPORT_NUMBER TEXT, " +
                "PASSPORT_ISSUE_DATE TEXT, " +
                "PASSPORT_ISSUE_PLACE TEXT, " +
                "PASSPORT_EXPIRATION_DATE TEXT, " +
                "FOOD_PREFERENCE TEXT, " +
                "DATE_OF_BIRTH TEXT, " +
                "NATIONALITY TEXT)");

        // Create FLIGHTS table
        db.execSQL("CREATE TABLE FLIGHTS(" +
                "FLIGHT_NUMBER TEXT PRIMARY KEY, " +
                "DEPARTURE_PLACE TEXT, " +
                "DESTINATION TEXT, " +
                "DEPARTURE_DATE DATE, " +
                "DEPARTURE_TIME TEXT, " +
                "ARRIVAL_DATE DATE, " +
                "ARRIVAL_TIME TEXT, " +
                "DURATION INTEGER, " +
                "AIRCRAFT_MODEL TEXT, " +
                "MAX_SEATS INTEGER, " +
                "BOOKING_OPEN_DATE DATE, " +
                "ECONOMY_CLASS_PRICE REAL, " +
                "BUSINESS_CLASS_PRICE REAL, " +
                "EXTRA_BAGGAGE_PRICE REAL, " +
                "RECURRENT TEXT, " +
                "CURRENT_RESERVATIONS INTEGER, " +
                "MISSED_FLIGHTS INTEGER)");

        // Create RESERVATIONS table
        db.execSQL("CREATE TABLE RESERVATIONS(" +
                "RESERVATION_ID TEXT PRIMARY KEY, " +
                "PASSPORT_NUMBER TEXT, " +
                "FLIGHT_NUMBER TEXT, " +
                "FLIGHT_CLASS TEXT, " +
                "EXTRA_BAGS INTEGER, " +
                "TOTAL_COST REAL, " +
                "UNIQUE(PASSPORT_NUMBER, FLIGHT_NUMBER), " + // Unique constraint on passportNumber and flightNumber
                "FOREIGN KEY(PASSPORT_NUMBER) REFERENCES USERS(PASSPORT_NUMBER), " +
                "FOREIGN KEY(FLIGHT_NUMBER) REFERENCES FLIGHTS(FLIGHT_NUMBER))");
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS USERS");
        db.execSQL("DROP TABLE IF EXISTS FLIGHTS");
        onCreate(db);
    }

    public void insertUser(User user) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("EMAIL", user.getEmail());
        values.put("PASSWORD", user.getPassword());
        values.put("PHONE", user.getPhone());
        values.put("FIRST_NAME", user.getFirstName());
        values.put("LAST_NAME", user.getLastName());
        values.put("ROLE", user.getRole());
        if (user.getRole().equals("Passenger")) {
            values.put("PASSPORT_NUMBER", user.getPassportNumber());
            values.put("PASSPORT_ISSUE_DATE", user.getPassportIssueDate());
            values.put("PASSPORT_ISSUE_PLACE", user.getPassportIssuePlace());
            values.put("PASSPORT_EXPIRATION_DATE", user.getPassportExpirationDate());
            values.put("FOOD_PREFERENCE", user.getFoodPreference());
            values.put("DATE_OF_BIRTH", user.getDateOfBirth());
            values.put("NATIONALITY", user.getNationality());
        }
        db.insert("USERS", null, values);
    }

    public Cursor getUser(String email, String password) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM USERS WHERE EMAIL = ? AND PASSWORD = ?", new String[]{email, password});
    }

    public Cursor getAllFlights() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM FLIGHTS", null);
    }

    public void deleteFlight(String flightNumber) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("FLIGHTS", "FLIGHT_NUMBER = ?", new String[]{flightNumber});
    }

    public Cursor getFlightsNotOpenForReservation() {
        SQLiteDatabase db = getReadableDatabase();

        // Get current date in the format yyyy-MM-dd
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        return db.rawQuery("SELECT * FROM FLIGHTS WHERE BOOKING_OPEN_DATE > ?", new String[]{currentDate});
    }



    public Cursor searchFlights(String departureCity, String arrivalCity, String departureDate, String sortingOption, String passportNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        String query = "SELECT * FROM FLIGHTS f " +
                "WHERE f.DEPARTURE_PLACE = ? " +
                "AND f.DESTINATION = ? " +
                "AND f.DEPARTURE_DATE = ? " +
                "AND f.BOOKING_OPEN_DATE <=? " + // Booking is open
                "AND f.MAX_SEATS > f.CURRENT_RESERVATIONS " + // Flight is not full
                "AND NOT EXISTS (SELECT 1 FROM RESERVATIONS r WHERE r.FLIGHT_NUMBER = f.FLIGHT_NUMBER AND r.PASSPORT_NUMBER = ?)"; // Not already reserved

        // Sorting by lowest cost or shortest duration
        if (sortingOption.equals("Lowest Cost")) {
            query += " ORDER BY f.ECONOMY_CLASS_PRICE ASC";
        } else if (sortingOption.equals("Shortest Duration")) {
            query += " ORDER BY f.DURATION ASC";
        }
        Log.d("=============>Dubug",query);
        return db.rawQuery(query, new String[]{departureCity, arrivalCity, departureDate,currentDate, passportNumber});
    }


    public Cursor searchReturnFlights(String departureCity, String arrivalCity, String returnDate, String sortingOption,String passportNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        String query = "SELECT * FROM FLIGHTS f " +
                "WHERE f.DEPARTURE_PLACE = ? " +
                "AND f.DESTINATION = ? " +
                "AND f.DEPARTURE_DATE = ? " +
                "AND f.BOOKING_OPEN_DATE <= ? " + // Booking is open
                "AND f.MAX_SEATS > f.CURRENT_RESERVATIONS " + // Flight is not full
                "AND NOT EXISTS (SELECT 1 FROM RESERVATIONS r WHERE r.FLIGHT_NUMBER = f.FLIGHT_NUMBER AND r.PASSPORT_NUMBER = ?)";
        // Sorting by lowest cost or shortest duration
        if (sortingOption.equals("Lowest Cost")) {
            query += " ORDER BY ECONOMY_CLASS_PRICE ASC";
        } else {
            query += " ORDER BY DURATION ASC";
        }

        return db.rawQuery(query, new String[]{arrivalCity,departureCity, returnDate,currentDate,passportNumber});
    }

    public Cursor getReservationsForFlight(String flightNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM RESERVATIONS WHERE FLIGHT_NUMBER = ?", new String[]{flightNumber});
    }

    public Cursor getPastReservationsForUser(String passportNumber) {
        SQLiteDatabase db = getReadableDatabase();

        // Get current date in the format yyyy-MM-dd
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        // Query to find all reservations for the given passport number with departure date less than current date
        String query = "SELECT * FROM RESERVATIONS " +
                "JOIN FLIGHTS ON RESERVATIONS.FLIGHT_NUMBER = FLIGHTS.FLIGHT_NUMBER " +
                "WHERE RESERVATIONS.PASSPORT_NUMBER = ? " +
                "AND FLIGHTS.DEPARTURE_DATE < ?";

        return db.rawQuery(query, new String[]{passportNumber, currentDate});
    }

    public Cursor getCurrentReservationsForUser(String passportNumber) {
        SQLiteDatabase db = getReadableDatabase();

        // Get current date in the format yyyy-MM-dd
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        // Query to find all reservations for the given passport number with departure date greater than or equal to the current date
        String query = "SELECT * FROM RESERVATIONS " +
                "JOIN FLIGHTS ON RESERVATIONS.FLIGHT_NUMBER = FLIGHTS.FLIGHT_NUMBER " +
                "WHERE RESERVATIONS.PASSPORT_NUMBER = ? " +
                "AND FLIGHTS.DEPARTURE_DATE >= ?";

        return db.rawQuery(query, new String[]{passportNumber, currentDate});
    }

    public boolean deleteReservation(String reservationId) {
        SQLiteDatabase db = getWritableDatabase();
        int rowsDeleted = db.delete("RESERVATIONS", "RESERVATION_ID = ?", new String[]{reservationId});

        // Check if any rows were deleted
        return rowsDeleted > 0;
    }

    public Cursor getFlightDetailsByFlightNumber(String flightNumber) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM FLIGHTS WHERE FLIGHT_NUMBER = ?", new String[]{flightNumber});
    }

    public void updateReservation(String reservationId, String flightClass, int extraBags, double totalCost) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("FLIGHT_CLASS", flightClass);
        values.put("EXTRA_BAGS", extraBags);
        values.put("TOTAL_COST", totalCost);

        db.update("RESERVATIONS", values, "RESERVATION_ID = ?", new String[]{reservationId});
    }

    public boolean updateFlight(String oldFlightNumber, String newFlightNumber, String departurePlace, String destination,
                                String departureDate, String departureTime, String arrivalDate, String arrivalTime,
                                int duration, String aircraftModel, int maxSeats, String bookingOpenDate,
                                double priceEconomy, double priceBusiness, double priceExtraBaggage, String recurrent) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        // Update the flight details
        values.put("FLIGHT_NUMBER", newFlightNumber);
        values.put("DEPARTURE_PLACE", departurePlace);
        values.put("DESTINATION", destination);
        values.put("DEPARTURE_DATE", departureDate);
        values.put("DEPARTURE_TIME", departureTime);
        values.put("ARRIVAL_DATE", arrivalDate);
        values.put("ARRIVAL_TIME", arrivalTime);
        values.put("DURATION", duration);
        values.put("AIRCRAFT_MODEL", aircraftModel);
        values.put("MAX_SEATS", maxSeats);
        values.put("BOOKING_OPEN_DATE", bookingOpenDate);
        values.put("ECONOMY_CLASS_PRICE", priceEconomy);
        values.put("BUSINESS_CLASS_PRICE", priceBusiness);
        values.put("EXTRA_BAGGAGE_PRICE", priceExtraBaggage);
        values.put("RECURRENT", recurrent);

        // Execute the update operation and check the number of rows affected
        int rowsAffected = db.update("FLIGHTS", values, "FLIGHT_NUMBER = ?", new String[]{oldFlightNumber});

        return rowsAffected > 0;
    }




    public void insertReservation(String reservationId, String passportNumber, String flightNumber, String flightClass, int extraBags, double totalCost) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("RESERVATION_ID", reservationId);
        values.put("PASSPORT_NUMBER", passportNumber);
        values.put("FLIGHT_NUMBER", flightNumber);
        values.put("FLIGHT_CLASS", flightClass);
        values.put("EXTRA_BAGS", extraBags);
        values.put("TOTAL_COST", totalCost);
        db.insert("RESERVATIONS", null, values);
    }

}
