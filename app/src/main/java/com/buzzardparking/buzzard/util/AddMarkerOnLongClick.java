package com.buzzardparking.buzzard.util;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

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
            }
        });
    }

}
