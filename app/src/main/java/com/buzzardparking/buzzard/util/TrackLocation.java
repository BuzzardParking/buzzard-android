package com.buzzardparking.buzzard.util;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.buzzardparking.buzzard.activities.MainActivity;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by nathansass on 8/18/16.
 */
public class TrackLocation implements
        LocationListener,
        OnActivity.Listener,
        OnClient.Listener,
        OnMap.Listener,
        OnPermission.Listener {

    private final LocationRequest mLocationRequest;
    private final Listener[] mListeners;

    private OnActivity.Status mActivityStatus;
    private GoogleApiClient mClient;
    private GoogleMap mGoogleMap;
    private OnPermission.Result mPermissionResult;
    private boolean mIsTracking;

    public TrackLocation(LocationRequest request, Listener... listeners) {
        mLocationRequest = request;
        mListeners = listeners;
    }

    // TODO Request location updates
    // Use LocationServices' FusedLocationApi.
    // Pass mClient, mLocationRequest, and this TrackLocation.
    // TrackLocation implements LocationListener.
    // Look at preconditions in check method.
    @SuppressWarnings("MissingPermission")
    private void startLocationUpdates() {
        Log.d(MainActivity.TAG, "Requested location updates");
    }

    // TODO Remove location updates
    // Use LocationServices' FusedLocationApi.
    // Pass mClient and this TrackLocation
    // Look at preconditions in check method.
    private void stopLocationUpdates() {
        Log.d(MainActivity.TAG, "Removed location updates");
    }

    private void check() {
        if (mActivityStatus == OnActivity.Status.RESUMED &&
                mClient != null && mClient.isConnected() &&
                mGoogleMap != null &&
                mPermissionResult == OnPermission.Result.GRANTED &&
                !mIsTracking) {
            startLocationUpdates();
            mIsTracking = true;
        }

        if (mActivityStatus == OnActivity.Status.PAUSED &&
                mClient != null && mClient.isConnected() &&
                mIsTracking) {
            stopLocationUpdates();
            mIsTracking = false;
        }

        if (mClient == null) {
            // Disconnected
            mIsTracking = false;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        for (Listener listener : mListeners) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            listener.accept(mGoogleMap, latLng);
        }
    }

    @Override
    public void onStatus(OnActivity.Status status) {
        mActivityStatus = status;
        check();
    }

    @Override
    public void onSave(Bundle state) {

    }

    @Override
    public void onRestore(Bundle state) {

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

    public interface Listener {
        void accept(GoogleMap map, LatLng location);
    }

}