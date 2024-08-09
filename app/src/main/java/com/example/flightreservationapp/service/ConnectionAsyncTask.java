package com.example.flightreservationapp.service;


import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.example.flightreservationapp.activity.MainActivity;
import com.example.flightreservationapp.model.Flight;
import com.example.flightreservationapp.utility.FlightJsonParser;

import java.util.List;

public class ConnectionAsyncTask extends AsyncTask<String, String, String> {
    Activity activity;
    String result;

    public ConnectionAsyncTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            result = HttpManager.getData(params[0]);
        } catch (Exception e) {
            result = "ERROR"; // Use a special value to indicate an error
        }
        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        if ("ERROR".equals(s) || s == null || s.isEmpty()) {
            // Handle the error and redirect
            ((MainActivity) activity).redirectToFailedLogin();
        } else {
            Log.d("TAG", s);
            List<Flight> flights = FlightJsonParser.getObjectFromJson(s);
            ((MainActivity) activity).addToDataBase(flights);
            ((MainActivity) activity).enableButtons();
        }
    }
}
