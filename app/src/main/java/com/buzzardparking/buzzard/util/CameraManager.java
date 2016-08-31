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
    private int defaultZoom = 17;
    private int defaultTilt = 90;

    public CameraManager(@Nullable Bundle savedInstanceState) {
        mSavedInstanceState = savedInstanceState;
    }

    @SuppressWarnings("MissingPermission")
    public LatLng getLastLocation() {
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mClient);

        return new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
    }

    @SuppressWarnings("MissingPermission")
    private void moveToUserLocation(GoogleApiClient client, GoogleMap map, int zoom, int tilt) {
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(
                client);

        if (lastLocation != null) {
            LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
            map.moveCamera(CameraUpdateFactory.newCameraPosition(getCameraPosition(latLng, zoom, tilt)));
        } else {
            Log.v("DEBUG", "Location null");
        }
    }

    public void moveToLocation(GoogleMap map, LatLng latLng) {
        map.moveCamera(CameraUpdateFactory.newCameraPosition(getCameraPosition(latLng, defaultZoom, defaultTilt)));
    }

    public LatLng getMapCenter() {
        return mGoogleMap.getCameraPosition().target;
    }


    private CameraPosition getCameraPosition(LatLng latLng, int zoom, int tilt) {
        return new CameraPosition.Builder().target(latLng)
                .tilt(tilt)
                .zoom(zoom)
                .build();
    }

    public void moveToUserLocation(int zoom, int tilt) {
        if (mSavedInstanceState == null &&
                mClient != null && mClient.isConnected() &&
                mGoogleMap != null &&
                mPermissionResult == OnPermission.Result.GRANTED) {


            moveToUserLocation(mClient, mGoogleMap, zoom, tilt);
        }
    }


    public void moveToUserLocation() {
        if (mSavedInstanceState == null &&
                mClient != null && mClient.isConnected() &&
                mGoogleMap != null &&
                mPermissionResult == OnPermission.Result.GRANTED) {

            int zoom = 17;
            int tilt = 0;
            moveToUserLocation(mClient, mGoogleMap, zoom, tilt);
        }
    }

    public GoogleApiClient getClient() {
        return mClient;
    }

    @Override
    public void onClient(@Nullable GoogleApiClient client) {
        mClient = client;
        moveToUserLocation(12, 0);
    }

    @Override
    public void onMap(GoogleMap map) {
        mGoogleMap = map;
        moveToUserLocation(12, 0);
    }

    @Override
    public void onResult(int requestCode, OnPermission.Result result) {
        mPermissionResult = result;
        moveToUserLocation(12, 0);
    }

}
