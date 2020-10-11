package de.htwg.helpme.helpers;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import de.htwg.helpme.datatypes.Point;

public class LocationHandler {
    private Context context;
    LocationManager locationManager;
    LocationListener locationListener;
    Point point;

    public LocationHandler(Context context) {
        this.context = context;
        getLocation();
    }


    public Point getLocation() {

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                point = new Point((float) location.getLatitude(), (float) location.getLongitude());

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        return point;

    }


}
