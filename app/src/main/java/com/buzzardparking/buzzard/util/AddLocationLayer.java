package com.buzzardparking.buzzard.util;

import com.google.android.gms.maps.GoogleMap;

/**
 * {@link AddLocationLayer} adds the current device location to the map.
 *
 * It listens for the map ready and the location permission event to
 * enable the map's location layer.
 */
public class AddLocationLayer implements
        OnMap.Listener,
        OnPermission.Listener {

    private GoogleMap mGoogleMap;
    private OnPermission.Result mPermissionResult;

    @SuppressWarnings("MissingPermission")
    private void addLayer(GoogleMap map) {
        // shows the default blue location dot
        map.setMyLocationEnabled(true);
    }

    private void check() {
        if (mGoogleMap != null &&
                mPermissionResult == OnPermission.Result.GRANTED) {
            addLayer(mGoogleMap);
        }
    }

    @Override
    public void onMap(GoogleMap map) {
        mGoogleMap = map;
        check();
    }

    @Override
    public void onResult(int requestCode, OnPermission.Result result) {
        mPermissionResult = result;
        check();
    }

}