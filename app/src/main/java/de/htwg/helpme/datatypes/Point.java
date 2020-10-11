package de.htwg.helpme.datatypes;

import androidx.annotation.NonNull;

public class Point {
    private float latitude;
    private float longitude;

    public Point(float latitude, float longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public static Point getPoint(float latitude, float longitude) {
        return new Point(latitude, longitude);
    }

    @NonNull
    @Override
    public String toString() {
        return getLatitude() + "," + getLongitude();
    }
}
