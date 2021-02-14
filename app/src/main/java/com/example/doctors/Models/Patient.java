package com.example.doctors.Models;

public class Patient {
    private String id;
    private String name;
    private long timeStartAppointment;

    public Patient() {}

    public Patient(String id, String name, long timeStartAppointment)
    {
        this.id = id;
        this.name = name;
        this.timeStartAppointment = timeStartAppointment;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String username) {
        this.name = username;
    }

    public void setTimeStartAppointment(long timeStartAppointment) {
        this.timeStartAppointment = timeStartAppointment;
    }

    public long getTimeStartAppointment() {
        return timeStartAppointment;
    }

}
