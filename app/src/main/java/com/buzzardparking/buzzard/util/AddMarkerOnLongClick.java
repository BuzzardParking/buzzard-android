package com.buzzardparking.buzzard.util;

import android.content.Context;
import android.os.Vibrator;
import android.view.LayoutInflater;

import com.buzzardparking.buzzard.R;
import com.buzzardparking.buzzard.activities.MainActivity;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * {@link AddMarkerOnLongClick} adds the long click callback to the map.
 *
 * It listens for the map ready event to
 * set an on long click listener.
 */
public class AddMarkerOnLongClick implements OnMap.Listener, GoogleMap.OnMarkerClickListener {

    private final Context mContext;
    private final PlaceManager mPlaceManager;

    public AddMarkerOnLongClick(Context context, PlaceManager manager) {
        mContext = context;
        mPlaceManager = manager;
    }

    @Override
    public void onMap(final GoogleMap map) {
        map.setOnMarkerClickListener(this);
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

    @Override
    public boolean onMarkerClick(Marker marker) {
        BottomSheetLayout bottomSheet = ((MainActivity) mContext).bottomSheet;
        bottomSheet.showWithSheetView(
                LayoutInflater
                        .from(mContext)
                        .inflate(R.layout.image_card, bottomSheet, false)
        );
        return true;
    }
}
