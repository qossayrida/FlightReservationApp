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


        db.execSQL("CREATE TABLE NOTIFICATIONS(" +
                "NOTIFICATION_ID TEXT PRIMARY KEY, " +
                "PASSPORT_NUMBER TEXT," +
                "FLIGHT_NUMBER TEXT, " +
                "MESSAGE TEXT, " +
                "TIMESTAMP DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "IS_READ INTEGER DEFAULT 0, " +
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
        db.beginTransaction(); // Start a transaction for atomic operation

        try {
            // Retrieve all reservations for the given flight
            Cursor cursor = getReservationsForFlight(flightNumber);

            // Loop through all reservations
            while (cursor.moveToNext()) {
                String passportNumber = cursor.getString(cursor.getColumnIndexOrThrow("PASSPORT_NUMBER"));
                String reservationId = cursor.getString(cursor.getColumnIndexOrThrow("RESERVATION_ID"));

                // Create a notification message
                String message = "Your flight with number " + flightNumber + " has been canceled.";

                // Generate a unique notification ID (this is just an example, you might want to implement a more sophisticated ID generation)
                String notificationId = reservationId + "_DELETED";

                // Insert the notification into the NOTIFICATIONS table
                ContentValues notificationValues = new ContentValues();
                notificationValues.put("NOTIFICATION_ID", notificationId);
                notificationValues.put("PASSPORT_NUMBER", passportNumber);
                notificationValues.put("FLIGHT_NUMBER", flightNumber);
                notificationValues.put("MESSAGE", message);
                notificationValues.put("TIMESTAMP", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
                notificationValues.put("IS_READ", 0);

                db.insert("NOTIFICATIONS", null, notificationValues);

                // Delete the reservation
                db.delete("RESERVATIONS", "RESERVATION_ID = ?", new String[]{reservationId});
            }
            cursor.close();

            // Finally, delete the flight itself
            db.delete("FLIGHTS", "FLIGHT_NUMBER = ?", new String[]{flightNumber});

            db.setTransactionSuccessful(); // Commit the transaction
        } catch (Exception e) {
            Log.e("DatabaseError", "Error deleting flight and associated reservations", e);
        } finally {
            db.endTransaction(); // End the transaction
        }
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


        // If the flight was successfully updated, add notifications
        if (rowsAffected > 0) {
            // Retrieve all reservations for the given flight
            Cursor cursor = getReservationsForFlight(oldFlightNumber);

            // Loop through all reservations
            while (cursor.moveToNext()) {
                String passportNumber = cursor.getString(cursor.getColumnIndexOrThrow("PASSPORT_NUMBER"));
                String reservationId = cursor.getString(cursor.getColumnIndexOrThrow("RESERVATION_ID"));

                // Create a notification message
                String message = "Your flight with number " + newFlightNumber + " has been updated.";

                // Generate a unique notification ID (this is just an example, you might want to implement a more sophisticated ID generation)
                String notificationId = reservationId + "_EDITED";

                // Insert the notification into the NOTIFICATIONS table
                ContentValues notificationValues = new ContentValues();
                notificationValues.put("NOTIFICATION_ID", notificationId);
                notificationValues.put("PASSPORT_NUMBER", passportNumber);
                notificationValues.put("FLIGHT_NUMBER", oldFlightNumber);
                notificationValues.put("MESSAGE", message);
                notificationValues.put("TIMESTAMP", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
                notificationValues.put("IS_READ", 0);

                db.insert("NOTIFICATIONS", null, notificationValues);
            }
            cursor.close();
        }

        return rowsAffected > 0;
    }


    public void insertReservation(String reservationId, String passportNumber, String flightNumber, String flightClass, int extraBags, double totalCost) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction(); // Start a transaction for atomic operation

        try {
            // Insert the reservation
            ContentValues values = new ContentValues();
            values.put("RESERVATION_ID", reservationId);
            values.put("PASSPORT_NUMBER", passportNumber);
            values.put("FLIGHT_NUMBER", flightNumber);
            values.put("FLIGHT_CLASS", flightClass);
            values.put("EXTRA_BAGS", extraBags);
            values.put("TOTAL_COST", totalCost);
            db.insert("RESERVATIONS", null, values);

            // Increment CURRENT_RESERVATIONS
            Cursor cursor = db.rawQuery("SELECT CURRENT_RESERVATIONS FROM FLIGHTS WHERE FLIGHT_NUMBER = ?", new String[]{flightNumber});
            if (cursor.moveToFirst()) {
                int currentReservations = cursor.getInt(cursor.getColumnIndexOrThrow("CURRENT_RESERVATIONS"));
                currentReservations++; // Increment the value

                ContentValues flightValues = new ContentValues();
                flightValues.put("CURRENT_RESERVATIONS", currentReservations);

                // Update the flight's CURRENT_RESERVATIONS
                db.update("FLIGHTS", flightValues, "FLIGHT_NUMBER = ?", new String[]{flightNumber});
            }
            cursor.close();

            db.setTransactionSuccessful(); // Commit the transaction
        } catch (Exception e) {
            Log.e("DatabaseError", "Error inserting reservation", e);
        } finally {
            db.endTransaction(); // End the transaction
        }
    }


    public Cursor getClosestFiveFlights() {
        SQLiteDatabase db = this.getReadableDatabase();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        // Query to get the five flights with the closest departure dates greater than the current date
        String query = "SELECT * FROM FLIGHTS WHERE DEPARTURE_DATE > ? " +
                "ORDER BY DEPARTURE_DATE ASC " +
                "LIMIT 5";

        return db.rawQuery(query, new String[]{currentDate});
    }


    public Cursor getArchive() {
        SQLiteDatabase db = this.getReadableDatabase();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        // Query to retrieve flights and their corresponding reservations where the flight has already occurred
        String query = "SELECT *" +
                "FROM FLIGHTS FL " +
                "WHERE FL.DEPARTURE_DATE <?";

        // Execute the query and return the Cursor containing the result set
        return db.rawQuery(query, new String[]{currentDate});
    }

    public Cursor getUpcomingFlights() {
        SQLiteDatabase db = this.getReadableDatabase();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        // Query to retrieve flights where the booking open date is in the future
        String query = "SELECT * " +
                "FROM FLIGHTS " +
                "WHERE BOOKING_OPEN_DATE > ?";

        // Execute the query and return the Cursor containing the result set
        return db.rawQuery(query, new String[]{currentDate});
    }


    public Cursor getAvailableFlights() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        SQLiteDatabase db = this.getReadableDatabase();

        // Query to retrieve flights where the booking open date has arrived,
        // the departure date is in the future, and the flight is not fully booked
        String query = "SELECT * " +
                "FROM FLIGHTS " +
                "WHERE BOOKING_OPEN_DATE <= ? " + // Booking is open
                "AND DEPARTURE_DATE > ? " +       // Flight hasn't departed yet
                "AND CURRENT_RESERVATIONS < MAX_SEATS";     // Flight is not fully booked

        // Execute the query and return the Cursor containing the result set
        return db.rawQuery(query, new String[]{currentDate,currentDate});
    }

    public Cursor getUnreadNotificationsByPassportNumber(String passportNumber) {
        SQLiteDatabase db = getReadableDatabase();

        // Query to select unread notifications for the given passport number
        String query = "SELECT * FROM NOTIFICATIONS WHERE PASSPORT_NUMBER = ? AND IS_READ = 0";

        // Execute the query and return the Cursor containing the result set
        Cursor cursor = db.rawQuery(query, new String[]{passportNumber});

        if (cursor != null && cursor.moveToFirst()) {
            // Mark the notifications as read
            ContentValues contentValues = new ContentValues();
            contentValues.put("IS_READ", 1); // Assuming '1' represents 'read'

            // Update the IS_READ status for all matching rows
            db.update("NOTIFICATIONS", contentValues, "PASSPORT_NUMBER = ? AND IS_READ = 0", new String[]{passportNumber});
        }

        return cursor;
    }



}
