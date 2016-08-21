package com.buzzardparking.buzzard.util;

import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import java.util.Random;


/**
 * Circle location listens to
 */
public class CircleLocation implements TrackLocation.Listener {

    private Circle mCircle;
    private Random mRandom = new Random();

    @Override
    public void accept(GoogleMap map, LatLng location) {
        if (mCircle != null) {
            mCircle.remove();
        }

        int radiusInMeters = 500 + mRandom.nextInt(4500);

        CircleOptions opts = new CircleOptions()
                .fillColor(Color.YELLOW)
                .strokeWidth(0)
                .radius(radiusInMeters)
                .center(location);
        mCircle = map.addCircle(opts);
    }
}