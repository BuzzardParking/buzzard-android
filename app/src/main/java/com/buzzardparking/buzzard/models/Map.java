package com.buzzardparking.buzzard.models;

import android.util.Log;

import com.buzzardparking.buzzard.util.OnMap;
import com.google.android.gms.maps.GoogleMap;

/*
 * Instantiated as singleton to hold reference to the map
 */
public class Map implements OnMap.Listener {
    GoogleMap googleMap;

    @Override
    public void onMap(GoogleMap map) {
        this.googleMap = map;
    }

    public Boolean isLoaded() {
        // TODO: This may need to be more sophisticated
        return googleMap != null;
    }

    public GoogleMap get() {
        if (isLoaded()) {
            return googleMap;
        } else {
            //TODO: throw some more sophisticated error
            Log.v("DEBUG", "Map is not yet loaded");
            return googleMap;
        }
    }
}
