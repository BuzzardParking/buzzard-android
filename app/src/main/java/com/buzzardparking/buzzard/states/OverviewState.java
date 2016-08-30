package com.buzzardparking.buzzard.states;

import android.content.Context;
import android.view.View;

import com.buzzardparking.buzzard.R;
import com.buzzardparking.buzzard.models.AppState;
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
        getContext().rlTopPieceContainer.setVisibility(View.VISIBLE);
        getContext().btnFindParking.setOnClickListener(null);
        getContext().btnFindParking.setVisibility(View.GONE);
        bottomSheet.showFab();
    }


    private void updateUI() {
        getContext().rlTopPieceContainer.setVisibility(View.GONE);
        getContext().btnFindParking.setVisibility(View.VISIBLE);
        bottomSheet.hideFab();


        getPlaceManager().loadPlaces(getContext().getMap());

        getCameraManager().moveToUserLocation(12, 0); // This must be coordinated with the callbacks at the bottom of cameraManager

        getContext().tvBottomSheetHeading.setText(getContext().getString(R.string.btn_navigating));

        bottomSheet.expand();

        getContext().btnFindParking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getContext().goTo(AppState.LOOKING);
            }
        });

    }


    @Override
    public void onMap(GoogleMap map) {
        updateUI();
    }
}
