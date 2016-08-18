package com.buzzardparking.buzzard.util;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.ui.IconGenerator;

public class AddToMap {

    private IconGenerator mIconGenerator;

    public AddToMap(IconGenerator generator) {
        mIconGenerator = generator;
    }

    // TODO Add marker
    // Use IconGenerator, MarkerOptions, and GoogleMap.
    // Call animate method if animate flag is true.
    public void addTo(GoogleMap map, String title, LatLng latLng, boolean animate) {

    }

    // TODO Animate marker
    // Get the marker's position and the map's projection.
    // Create start and stop LatLng's to animate with.
    // Create a ValueAnimator and add an update listener.
    // Use SphericalUtil to calculate interpolated LatLng.
    // Set the marker's position to this LatLng.
    // Set the animator's interpolator and duration.
    // Start animator.
    private void animate(GoogleMap map, final Marker marker) {

    }

}
