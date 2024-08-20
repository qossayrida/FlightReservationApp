package com.example.flightreservationapp.model;

import java.io.Serializable;

public class Notifications implements Serializable {

    private String notificationId;
    private String passportNumber;
    private String flightNumber;
    private String message;
    private String timestamp;
    private boolean isRead;


    public Notifications(String notificationId, String passportNumber, String flightNumber, String message, String timestamp, boolean isRead) {
        this.notificationId = notificationId;
        this.passportNumber = passportNumber;
        this.flightNumber = flightNumber;
        this.message = message;
        this.timestamp = timestamp;
        this.isRead = isRead;
    }


    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}
