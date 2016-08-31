package com.buzzardparking.buzzard.util;

import android.content.Context;
import android.os.Vibrator;
import android.view.View;

import com.buzzardparking.buzzard.activities.MapActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

/**
 * {@link AddMarkerOnLongClick} adds the long click callback to the map.
 * <p/>
 * It listens for the map ready event to
 * set an on long click listener.
 */
public class AddMarkerOnLongClick implements OnMap.Listener {

    private final MapActivity context;
    private final PlaceManager placeManager;
    private boolean isLongPressed = false;

    private CameraManager cameraManager;

    public AddMarkerOnLongClick(MapActivity context, PlaceManager placeManager, CameraManager cameraManager) {
        this.context = context;
        this.placeManager = placeManager;
        this.cameraManager = cameraManager;
    }

    @Override
    public void onMap(final GoogleMap map) {
        map.setOnMapLongClickListener(mapLongClickListener);
    }

    private GoogleMap.OnMapLongClickListener mapLongClickListener = new GoogleMap.OnMapLongClickListener() {

        @Override
        public void onMapLongClick(LatLng latLng) {
            Vibrator vb = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
            vb.vibrate(50);

            if (isLongPressed) {
                getContext().ivAddMarkerIcon.setVisibility(View.GONE);

                placeManager.addPlace(cameraManager.getMapCenter());

                isLongPressed = false;
            } else {
                getContext().ivAddMarkerIcon.setVisibility(View.VISIBLE);

                getCameraManager().moveToLocation(getContext().getMap(), latLng);

                isLongPressed = true;

            }
        }

    };

    public MapActivity getContext() {
        return context;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

}
