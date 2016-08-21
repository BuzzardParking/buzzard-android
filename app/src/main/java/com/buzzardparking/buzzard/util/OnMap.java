package com.buzzardparking.buzzard.util;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

/**
 * OnMap is an OnMapReadyCallback that is evoked when a {@link GoogleMap} is ready.
 *
 * It also serves as a proxy observable that broadcasts to its listeners that the
 * map is ready.
 */
public class OnMap implements OnMapReadyCallback {

    private final Listener[] mListeners;

    /**
     * Creates an {@link OnMapReadyCallback}
     *
     * @param listeners  A list of listens that will receive a callback when the map is loaded
     */
    public OnMap(Listener... listeners) {
        mListeners = listeners;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        for (Listener listener : mListeners) {
            listener.onMap(googleMap);
        }
    }

    public interface Listener {
        void onMap(GoogleMap map);
    }

}