package tn.esprit.controllers;
public class JavaConnector {
    private String place;
    private double latitude;
    private double longitude;

    // Constructeur
    public JavaConnector(String place, double latitude, double longitude) {
        this.place = place;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters et Setters
    public String getPalce() {
        return place;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
