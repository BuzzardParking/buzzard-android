package com.buzzardparking.buzzard.util;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.buzzardparking.buzzard.activities.MainActivity;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * {@link OnClient} is a connection call back that receives connection events from Google API client.
 *
 * It is also a proxy observable that signals its listeners that the
 * google api client is ready.
 */
public class OnClient implements GoogleApiClient.ConnectionCallbacks {

    private final GoogleApiClient mGoogleApiClient;
    private final Listener[] mListeners;

    public OnClient(GoogleApiClient client, Listener... listeners) {
        mGoogleApiClient = client;
        mListeners = listeners;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(MainActivity.TAG, "Client connected!");
        for (Listener listener : mListeners) {
            listener.onClient(mGoogleApiClient);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        for (Listener listener : mListeners) {
            listener.onClient(null);
        }
    }

    public interface Listener {
        void onClient(@Nullable GoogleApiClient client);
    }

}
