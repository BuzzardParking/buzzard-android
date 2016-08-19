package com.buzzardparking.buzzard.util;

import android.util.Log;

import com.buzzardparking.buzzard.activities.MainActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public class LogLocation implements TrackLocation.Listener {

    // Use MapsActivity.TAG
    // Use the map too and get creative.
    // See CircleLocation for an example.
    @Override
    public void accept(GoogleMap map, LatLng location) {
        Log.d(MainActivity.TAG, "Location update: " + location);
    }

}