package com.buzzardparking.buzzard.util;

import android.content.Context;
import android.os.Vibrator;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

/**
 * {@link AddMarkerOnLongClick} adds the long click callback to the map.
 *
 * It listens for the map ready event to
 * set an on long click listener.
 */
public class AddMarkerOnLongClick implements OnMap.Listener {

    private final Context mContext;
    private final PlaceManager mPlaceManager;

    public AddMarkerOnLongClick(Context context, PlaceManager manager) {
        mContext = context;
        mPlaceManager = manager;
    }

    @Override
    public void onMap(final GoogleMap map) {
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            @Override
            public void onMapLongClick(LatLng latLng) {
                mPlaceManager.addPlace(map, "New Space", latLng);

                // vibrates phone
                Vibrator vb = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
                vb.vibrate(50);
            }
        });
    }

}
