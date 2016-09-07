package com.buzzardparking.buzzard.util;

import android.content.Context;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.ImageView;

import com.buzzardparking.buzzard.R;
import com.buzzardparking.buzzard.activities.MapActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

/**
 * {@link AddMarkerManager} adds the long click callback to the map.
 * <p/>
 * It listens for the map ready event to
 * set an on long click listener.
 */
public class AddMarkerManager implements OnMap.Listener {

    private final MapActivity context;
    private final PlaceManager placeManager;
    private boolean addingMode = false;

    private CameraManager cameraManager;

    protected BottomSheetManager bottomSheet;

    private FloatingActionButton fabPositiveAddMarker;
    private ImageView ivCancelAddMarker;

    public AddMarkerManager(MapActivity context, PlaceManager placeManager, CameraManager cameraManager) {
        this.context = context;
        this.placeManager = placeManager;
        this.cameraManager = cameraManager;
        this.bottomSheet = new BottomSheetManager(getContext(), getContext().getBottomSheetBehavior());
        getUIElements();
    }

    private void getUIElements() {
        fabPositiveAddMarker = (FloatingActionButton) context.findViewById(R.id.fabPositiveAddMarker);
        ivCancelAddMarker = (ImageView) context.findViewById(R.id.ivCancelAddMarker);
    }

    @Override
    public void onMap(final GoogleMap map) {
        map.setOnMapLongClickListener(mapLongClickListener);

        getContext().fabBtnSecondary.setOnClickListener(new View.OnClickListener() { // TODO: maybe this should be in bottom sheet manager
            @Override
            public void onClick(View view) {
                Vibrator vb = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                vb.vibrate(50);

                handleBeginAdding(null);
            }
        });

    }

    private void handleEndAdding(boolean isSuccess) {
        getContext().rlAddMarker.setVisibility(View.GONE);
        getContext().fabBtnSecondary.setVisibility(View.VISIBLE);

        if (isSuccess) {
            placeManager.addPlace(cameraManager.getMapCenter());
        }

        fabPositiveAddMarker.setOnClickListener(null);
        ivCancelAddMarker.setOnClickListener(null);

        addingMode = false;

        bottomSheet.expand();
    }

    public void handleBeginAdding(LatLng latLng) {
        bottomSheet.hide();
        getContext().rlAddMarker.setVisibility(View.VISIBLE);
        getContext().fabBtnSecondary.setVisibility(View.INVISIBLE);

        if (latLng != null) {
            getCameraManager().moveToLocation(getContext().getMap(), latLng);
        }

        fabPositiveAddMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleEndAdding(true);
            }
        });

        ivCancelAddMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleEndAdding(false);
            }
        });

        addingMode = true;
    }

    private GoogleMap.OnMapLongClickListener mapLongClickListener = new GoogleMap.OnMapLongClickListener() {

        @Override
        public void onMapLongClick(LatLng latLng) {
            Vibrator vb = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
            vb.vibrate(50);

            if (addingMode) {
               handleEndAdding(true);
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
