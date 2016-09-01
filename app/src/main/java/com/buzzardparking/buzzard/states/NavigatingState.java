package com.buzzardparking.buzzard.states;

import android.content.Context;

import com.buzzardparking.buzzard.R;
import com.buzzardparking.buzzard.models.AppState;
import com.buzzardparking.buzzard.models.Spot;
import com.buzzardparking.buzzard.util.BottomSheetManager;
import com.buzzardparking.buzzard.util.CameraManager;
import com.buzzardparking.buzzard.util.PlaceManager;
import com.buzzardparking.buzzard.util.PolylineManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

/**
 * {@link NavigatingState}: a user is navigating to a parking spot.
 */
public class NavigatingState extends UserState {

    private Spot spot;
    private PolylineManager lineManager = new PolylineManager(getContext());

    public NavigatingState(Context context, PlaceManager placeManager, CameraManager cameraManager, Spot spot) {
        super(context, placeManager, cameraManager);
        this.spot = spot;
        appState = AppState.NAVIGATING;
    }

    @Override
    public void start() {
        // TODO:
        // 1. hide other unrelated parking spaces during navigation
        // 2. draw poly line of navigation path from current location to the destination parking spot
        // 3. stop button to stop navigating, and back to looking state
        // 4. button to go to the parked state
        // 5. Show the parking space as well as the destination
        // ...

        getContext().tvBottomSheetHeading.setText(getContext().getString(R.string.tv_optimizing_navigation));
        // Temporary marker to show the parking spot location
        getPlaceManager().addParkingSpotMarker(getContext().getMap(), spot.getLatLng());

        getCameraManager().moveToLocation(getContext().getMap(), spot.getLatLng()); // Maybe zoom out to show both
        LatLng currentLocation = getCameraManager().getLastLocation();

        lineManager.createAndDisplay(getContext().getMap(), currentLocation, spot.getLatLng());

        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(currentLocation)
                .include(spot.getLatLng())
                .build();
        getContext().getMap().moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200));

        bottomSheet.expand();
        bottomSheet.setFabIcon(R.drawable.ic_parking);
        bottomSheet.setFabListener(new BottomSheetManager.FabListener() {
            @Override
            public void onClick() {
                getContext().goTo(AppState.PARKED, spot);
            }
        });

        bottomSheet.setBottomSheetStateListeners(new BottomSheetManager.BottomSheetListeners() {
            @Override
            public void onCollapsed() {

            }

            @Override
            public void onDragging() {

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
    }
}
