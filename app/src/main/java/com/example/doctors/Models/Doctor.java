package com.example.doctors.Models;

public class Doctor {
    private String id;
    private String name;
    private String status;
    private long timeLastAppointment;

    public Doctor (){}

    public Doctor(String id, String name, String status, long time_last_appointment)
    {
        this.id = id;
        this.name = name;
        this.status = status;
        this.timeLastAppointment = time_last_appointment;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String username) {
        this.name = username;
    }

    public String getName() {
        return name;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setTimeLastAppointment(long timeLastAppointment) {
        this.timeLastAppointment = timeLastAppointment;
    }

    public long getTimeLastAppointment() {
        return timeLastAppointment;
    }

}