package eu.marcocattaneo.rememberhere.business.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ProximityPOI extends RealmObject {

    @PrimaryKey
    private String guid;

    private String note;

    private double latitude;

    private double longitude;

    private int radius;

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }
}
