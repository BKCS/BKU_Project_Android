package com.application.hieu_nt.bkcs;

/**
 * Created by HIEU_NT on 14/11/2016.
 */
public class User {
    public Double Latitude;
    public Double Longitude;
    public Double Radius;
    public String Type;

    public User() {
    }

    public User(Double Latitude, Double Longitude) {
        this.Latitude = Latitude;
        this.Longitude = Longitude;
    }
}
