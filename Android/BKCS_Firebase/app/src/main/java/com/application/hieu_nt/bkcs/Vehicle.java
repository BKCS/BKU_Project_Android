package com.application.hieu_nt.bkcs;

/**
 * Created by HIEU_NT on 16/12/2016.
 */
public class Vehicle {
    public Double Latitude;
    public Double Longitude;
    public String Type;
    public String Time;

    public Vehicle() {
    }

    public Vehicle(Double Latitude, Double Longitude, String Type, String Time) {
        this.Latitude = Latitude;
        this.Longitude = Longitude;
        this.Type = Type;
        this.Time = Time;
    }
}
