package com.buzzardparking.buzzard.models;

import android.support.annotation.Nullable;

import com.buzzardparking.buzzard.util.OnClient;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by nathansass on 9/1/16.
 */
public class Client implements OnClient.Listener {
    private GoogleApiClient googleApiClient;

    public boolean isConnected() {
        if (googleApiClient != null) {
            return googleApiClient.isConnected();
        } else {
            return false;
        }
    }

    @Override
    public void onClient(@Nullable GoogleApiClient client) {
        this.googleApiClient = client;
    }
}
