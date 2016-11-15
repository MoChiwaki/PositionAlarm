package com.example.yukiko.positionalarm;

import java.sql.Date;

/**
 * Created by yukiko on 16/11/15.
 */
public class MapModel {
    private int id;
    private String date;
    private String address;
    private String content;
    private double latitude;
    private double longitude;
    private boolean near;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean isNear() {
        return near;
    }

    public void setNear(boolean near) {
        this.near = near;
    }
}
