package com.buzzardparking.buzzard.states;

import android.content.Context;
import android.view.View;

import com.buzzardparking.buzzard.R;
import com.buzzardparking.buzzard.models.AppState;
import com.buzzardparking.buzzard.models.DynamicSpot;
import com.buzzardparking.buzzard.util.BottomSheetManager;
import com.buzzardparking.buzzard.util.CameraManager;
import com.buzzardparking.buzzard.util.GeofenceController;
import com.buzzardparking.buzzard.util.PlaceManager;
import com.buzzardparking.buzzard.util.PolylineManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

/**
 * {@link NavigatingState}: a user is navigating to a parking dynamicSpot.
 */
public class NavigatingState extends UserState {

    private PolylineManager lineManager = new PolylineManager(getContext());

    public NavigatingState(
            Context context,
            PlaceManager placeManager,
            CameraManager cameraManager) {
        super(context, placeManager, cameraManager);
        this.dynamicSpot = DynamicSpot.loadLockedSpot(getContext().user);
        appState = AppState.NAVIGATING;
    }

    public NavigatingState(
            Context context,
            PlaceManager placeManager,
            CameraManager cameraManager,
            DynamicSpot spot) {
        super(context, placeManager, cameraManager);
        this.dynamicSpot = spot;
        appState = AppState.NAVIGATING;
    }

    @Override
    public void start() {
        getContext().user.setCurrentState(AppState.NAVIGATING);
        dynamicSpot.lockedBy(getContext().user);

        getContext().showProgressBar();
        if (isReady() || isReadyCache()) {
            updateUI();
            GeofenceController.getInstance().init(getContext(), getCameraManager().getClient());
            GeofenceController.getInstance().addGeofence(dynamicSpot.getLatLng());
        }
    }

    public void updateUI() {

        // TODO:
        // 1. hide other unrelated parking spaces during navigation
        // 2. draw poly line of navigation path from current location to the destination parking dynamicSpot
        // 3. stop button to stop navigating, and back to looking state
        // 4. button to go to the parked state
        // 5. Show the parking space as well as the destination
        // ...

        getContext().prepareView();

        getContext().tvBottomSheetHeading.setText(getContext().getString(R.string.tv_optimizing_navigation));
        // Temporary marker to show the parking dynamicSpot location
        getPlaceManager().addParkingSpotMarker(getContext().getMap(), dynamicSpot.getLatLng());

        getCameraManager().moveToLocation(getContext().getMap(), dynamicSpot.getLatLng());

        setBackButtonListener();

        LatLng currentLocation = getCameraManager().getLastLocation();

        lineManager.createAndDisplay(getContext().getMap(), currentLocation, dynamicSpot.getLatLng());

        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(currentLocation)
                .include(dynamicSpot.getLatLng())
                .build();
        getContext().getMap().moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200));

        bottomSheet.expand();
        bottomSheet.setFabIcon(R.drawable.ic_parking);
        bottomSheet.setFabListener(new BottomSheetManager.FabListener() {
            @Override
            public void onClick() {
                getContext().goTo(AppState.PARKED, dynamicSpot);
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
    public void stop() {
        lineManager.remove();
        super.stop();
        getPlaceManager().removeDestinationMarker();
        GeofenceController.getInstance().removeGeofence();
    }

    private void setBackButtonListener() {
        getContext().fabBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getContext().goTo(appState.LOOKING);
            }
        });
    }
}
