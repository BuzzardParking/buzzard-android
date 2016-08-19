package com.buzzardparking.buzzard.util;

import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;

public class AddToMap {

    private IconGenerator mIconGenerator;

    private ArrayList<Marker> markers;

    public AddToMap(IconGenerator generator) {
        mIconGenerator = generator;

        markers = new ArrayList<>();
    }

    // Use IconGenerator, MarkerOptions, and GoogleMap.
    // Call animate method if animate flag is true.
    public void addTo(GoogleMap map, String title, LatLng latLng, boolean animate) {
        Bitmap bitmap = mIconGenerator.makeIcon(title);
        MarkerOptions opts = new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromBitmap(bitmap));
        Marker marker = map.addMarker(opts);
        //Save reference to marker here for future clearing
        markers.add(marker);

        if (animate) {
            animate(map, marker);
        }
    }

    public void removeMarkers() {
        for (int i = 0; i < markers.size(); i++) {
            markers.get(i).remove();
        }
        markers = new ArrayList<>();
    }

    // Get the marker's position and the map's projection.
    // Create start and stop LatLng's to animate with.
    // Create a ValueAnimator and add an update listener.
    // Use SphericalUtil to calculate interpolated LatLng.
    // Set the marker's position to this LatLng.
    // Set the animator's interpolator and duration.
    // Start animator.
    private void animate(GoogleMap map, final Marker marker) {
        final LatLng target = marker.getPosition();
        Projection projection = map.getProjection();
        Point endPoint = projection.toScreenLocation(target);
        Point startPoint = new Point(endPoint.x, 0);
        final LatLng offscreen = projection.fromScreenLocation(startPoint);
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator an) {
                float value = (float) an.getAnimatedValue();
                double fraction = Float.valueOf(value).doubleValue();
                LatLng latLng = SphericalUtil.interpolate(offscreen, target, fraction);
                marker.setPosition(latLng);
            }
        });

        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(2500);
        animator.start();


    }

}
