package com.example.flightreservationapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.flightreservationapp.model.*;

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "flight_reservation.db";
    private static final int DATABASE_VERSION = 1;

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE USERS(EMAIL TEXT PRIMARY KEY, PASSWORD TEXT, PHONE TEXT, FIRST_NAME TEXT, LAST_NAME TEXT, ROLE TEXT, PASSPORT_NUMBER TEXT, PASSPORT_ISSUE_DATE TEXT, PASSPORT_ISSUE_PLACE TEXT, PASSPORT_EXPIRATION_DATE TEXT, FOOD_PREFERENCE TEXT, DATE_OF_BIRTH TEXT, NATIONALITY TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS USERS");
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
}
