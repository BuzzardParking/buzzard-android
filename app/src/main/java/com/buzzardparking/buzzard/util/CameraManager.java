package com.buzzardparking.buzzard.util;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

/**
 * {@link CameraManager} moves the camera to current user's location in the first time.
 *
 * It listens for the map ready, the connected GoogleApiClient, and the
 * location permission before it moves the map to the user's current location.
 */
public class CameraManager implements
        OnMap.Listener,
        OnPermission.Listener,
        OnClient.Listener {

    private final Bundle mSavedInstanceState;

    private GoogleApiClient mClient;
    private GoogleMap mGoogleMap;
    private OnPermission.Result mPermissionResult;

    public CameraManager(@Nullable Bundle savedInstanceState) {
        mSavedInstanceState = savedInstanceState;
    }

    @SuppressWarnings("MissingPermission")
    public void moveToUserLocation(GoogleApiClient client, GoogleMap map) {
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(
                client);

        if (lastLocation != null) {
            LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
            map.moveCamera(CameraUpdateFactory.newCameraPosition(getCameraPosition(latLng)));
        } else {
            Log.v("DEBUG", "Location null");
        }
    }

    // Set target, zoom, and tilt (for 3d effect).
    private CameraPosition getCameraPosition(LatLng latLng) {
        return new CameraPosition.Builder().target(latLng)
                .tilt(90)
                .zoom(18)
                .build();
    }

    public void check() {
        if (mSavedInstanceState == null &&
                mClient != null && mClient.isConnected() &&
                mGoogleMap != null &&
                mPermissionResult == OnPermission.Result.GRANTED) {
            moveToUserLocation(mClient, mGoogleMap);
        }
    }

    public GoogleApiClient getClient() {
        return mClient;
    }

    @Override
    public void onClient(@Nullable GoogleApiClient client) {
        mClient = client;
        check();
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
