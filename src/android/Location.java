package com.inffinix.plugins;

import java.util.Date;

/**
 * Created by eduardo on 13/07/16.
 */
public class Location {
    private int id;
    private double latitude;
    private double longitude;
    private Date date;
    private int type;

    public Location() {
    }

    public Location(int id, double latitude, double longitude, Date date, int type) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Location{" +
                "id=" + id +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", date=" + date +
                ", type=" + type +
                '}';
    }
}
