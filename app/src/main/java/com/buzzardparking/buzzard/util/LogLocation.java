package com.buzzardparking.buzzard.util;

import android.util.Log;

import com.buzzardparking.buzzard.activities.MapActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

/**
 * LogLocation listens for a location from TrackLocation before it logs the location.
 */
public class LogLocation implements TrackLocation.Listener {

    // Use MapsActivity.TAG
    // Use the map too and get creative.
    // See CircleLocation for an example.
    @Override
    public void accept(GoogleMap map, LatLng location) {
        Log.d(MapActivity.TAG, "Location update: " + location);
    }

}