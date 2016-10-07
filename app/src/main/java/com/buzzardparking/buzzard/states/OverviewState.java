package com.buzzardparking.buzzard.states;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;

import com.buzzardparking.buzzard.R;
import com.buzzardparking.buzzard.models.AppState;
import com.buzzardparking.buzzard.util.BottomSheetManager;
import com.buzzardparking.buzzard.util.CameraManager;
import com.buzzardparking.buzzard.util.PlaceManager;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.Random;

/**
 * {@link OverviewState}: a user is overviewing over a range.
 */
public class OverviewState extends UserState {

    private Handler handler = new Handler();
    private Runnable loadPlacesRunnable = new Runnable() {
        @Override
        public void run() {
            getPlaceManager().loadPlaces(getContext().getMap());
            handler.postDelayed(loadPlacesRunnable, POLLING_INTERVAL);
        }
    };

    public OverviewState(Context context, PlaceManager placeManager, CameraManager cameraManager) {
        super(context, placeManager, cameraManager);
        appState = AppState.OVERVIEW;
    }
    @Override
    public void start() {
        getContext().user.setCurrentState(AppState.OVERVIEW);
        getContext().showProgressBar();
        if (isReady() || isReadyCache()) {
            updateUI();
        }
    }

    @Override
    public void stop() {
        super.stop();

        getPlaceManager().clearMap();
        handler.removeCallbacks(loadPlacesRunnable);
        getContext().btnFindParking.setOnClickListener(null);
        getContext().fabBack.setVisibility(View.VISIBLE);
    }

    private void updateUI() {
        ///
        getContext().rlTopPieceContainer.setVisibility(View.GONE);
        getContext().btnFindParking.setVisibility(View.VISIBLE);
        getContext().fabBack.setVisibility(View.GONE);
        bottomSheet.hideFab();
        getContext().getMap().getUiSettings().setMapToolbarEnabled(false);
        ///

        bottomSheet.expand();

        bottomSheet.viewRendered(new BottomSheetManager.SheetRendering() {
            @Override
            public void done() {
                bottomSheet.expand();
            }
        });

        // start periodically polling
        handler.post(loadPlacesRunnable);

        getCameraManager().moveToUserLocation(12, 0); // This must be coordinated with the callbacks at the bottom of cameraManager

        getContext().tvBottomSheetHeading.setText(getContext().getString(R.string.btn_navigating));

        getContext().btnFindParking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getContext().goTo(AppState.LOOKING);
            }
        });

        bottomSheet.setBottomSheetStateListeners(new BottomSheetManager.BottomSheetListeners() {
            @Override
            public void onCollapsed() {
            }

            @Override
            public void onDragging() {
                bottomSheet.expand();
            }

            @Override
            public void onExpanded() {

            }

            @Override
            public void onHidden() {
                bottomSheet.expand();
            }

            @Override
            public void onSettling() {

            }
        });

    }

    @Override
    public void onClient(@Nullable GoogleApiClient client) {
        super.onClient(client);
        makeFakeParkingPlaces();
    }

    @Override
    public void onMap(GoogleMap map) {
        updateUI();
    }

    private void makeFakeParkingPlaces() {
        LatLng userLoc = getCameraManager().getLastLocation();
        for (int i = 0; i <= 2; i++) {
            double lat = userLoc.latitude;
            double lng = userLoc.longitude;
            double newLat = lat;
            double newLng = lng;

            double changer = Math.round(new Random().nextDouble() * 100.0) / 100.0;
            double changerVal = (changer / 2) / 10;
            if (changer > 0.75) {
                newLat += changerVal;
                newLng += changerVal;
            } else if (changer  > 0.5) {
                newLat -= changerVal;
                newLng -= changerVal;
            } else if (changer > 0.25 ) {
                newLat += changerVal;
                newLng -= changerVal;
            } else {
                newLat -= changerVal;
                newLng += changerVal;
            }

            LatLng newLatlng = new LatLng(newLat, newLng);
            getPlaceManager().addPlace(newLatlng);
        }
    }
}
