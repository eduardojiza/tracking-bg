package com.inffinix.plugins;

import java.io.Serializable;

/**
 * Created by eduardo on 29/06/16.
 */
public class ConfigurationTracking implements Serializable{
    private String serverLocation;
    private String password;
    private String login;
    private double latitude;
    private double longitude;

    public ConfigurationTracking() {
        serverLocation = null;
        password = null;
        login = null;
        latitude = 0;
        longitude = 0;
    }

    public ConfigurationTracking(String serverLocation, String password, String login, double latitude, double longitude) {
        this.serverLocation = serverLocation;
        this.password = password;
        this.login = login;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "ConfigurationTracking{" + "serverLocation=" + serverLocation + ", password=" + password + ", login=" + login + ", latitude=" + latitude + ", longitude=" + longitude + '}';
    }

    public String getServerLocation() {
        return serverLocation;
    }

    public void setServerLocation(String serverLocation) {
        this.serverLocation = serverLocation;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
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

}
