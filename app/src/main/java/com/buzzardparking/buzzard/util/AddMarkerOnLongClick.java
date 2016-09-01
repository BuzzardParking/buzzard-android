package com.buzzardparking.buzzard.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Toast;

import com.buzzardparking.buzzard.R;
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
    private boolean addingMode = false;

    private CameraManager cameraManager;

    protected BottomSheetManager bottomSheet;

    public AddMarkerOnLongClick(MapActivity context, PlaceManager placeManager, CameraManager cameraManager) {
        this.context = context;
        this.placeManager = placeManager;
        this.cameraManager = cameraManager;
        this.bottomSheet = new BottomSheetManager(getContext(), getContext().getBottomSheetBehavior());
    }

    @Override
    public void onMap(final GoogleMap map) {
        map.setOnMapLongClickListener(mapLongClickListener);

        getContext().fabBtnSecondary.setOnClickListener(new View.OnClickListener() { // TODO: maybe this should be in bottom sheet manager
            @Override
            public void onClick(View view) {
                Vibrator vb = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                vb.vibrate(50);

                if (addingMode) {
                    handleEndAdding();
                } else {
                    handleBeginAdding(null);
                }
            }
        });

    }

    private void handleEndAdding() {
        getContext().ivAddMarkerIcon.setVisibility(View.GONE);

        placeManager.addPlace(cameraManager.getMapCenter());

        bottomSheet.getFabBtnSecondaryBtn().setImageResource(R.drawable.ic_add);
        bottomSheet.getFabBtnSecondaryBtn().setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(),R.color.colorAccent)));

        addingMode = false;
    }

    public void handleBeginAdding(LatLng latLng) {
        getContext().ivAddMarkerIcon.setVisibility(View.VISIBLE);


        if (latLng != null) {
            getCameraManager().moveToLocation(getContext().getMap(), latLng);
        }


        bottomSheet.getFabBtnSecondaryBtn().setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(),R.color.colorPrimary)));
        bottomSheet.getFabBtnSecondaryBtn().setImageResource(R.drawable.ic_check);

        Toast.makeText(getContext(), "Long tap again to save the space", Toast.LENGTH_LONG).show();

        addingMode = true;
    }



    private GoogleMap.OnMapLongClickListener mapLongClickListener = new GoogleMap.OnMapLongClickListener() {

        @Override
        public void onMapLongClick(LatLng latLng) {
            Vibrator vb = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
            vb.vibrate(50);

            if (addingMode) {
               handleEndAdding();
            } else {
                handleBeginAdding(latLng);
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
