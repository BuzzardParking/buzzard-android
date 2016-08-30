package com.buzzardparking.buzzard.states;

import android.content.Context;

import com.buzzardparking.buzzard.R;
import com.buzzardparking.buzzard.models.AppState;
import com.buzzardparking.buzzard.util.BottomSheetManager;
import com.buzzardparking.buzzard.util.CameraManager;
import com.buzzardparking.buzzard.util.PlaceManager;
import com.google.android.gms.maps.GoogleMap;

/**
 * Created by nathansass on 8/30/16.
 */
public class OverviewState extends UserState {

    public OverviewState(Context context, PlaceManager placeManager, CameraManager cameraManager) {
        super(context, placeManager, cameraManager);
        appState = AppState.OVERVIEW;
    }
    @Override
    public void start() {
        if (getContext().buzzardMap.isLoaded()) {
            updateUI();
        }
    }

    @Override
    public void stop() {
        super.stop();
        getPlaceManager().clearMap();
    }


    private void updateUI() {

        getPlaceManager().loadPlaces(getContext().getMap());

        getCameraManager().moveToUserLocation(12, 0); // This must be coordinated with the callbacks at the bottom of cameraManager


        getContext().tvBottomSheetHeading.setText(getContext().getString(R.string.btn_navigating));

        bottomSheet.expand();
        bottomSheet.setFabIcon(R.drawable.ic_parking);
        bottomSheet.setFabListener(new BottomSheetManager.FabListener() {
            @Override
            public void onClick() {
                getContext().goTo(AppState.LOOKING);
            }
        });

    }


    @Override
    public void onMap(GoogleMap map) {
        updateUI();
    }
}
