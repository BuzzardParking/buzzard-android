package com.buzzardparking.buzzard.states;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

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
    PolylineManager lineManager = new PolylineManager();

    public NavigatingState(Context context, PlaceManager placeManager, CameraManager cameraManager, Spot spot) {
        super(context, placeManager, cameraManager);
        this.spot = spot;
        APP_STATE = AppState.NAVIGATING;
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
        Toast.makeText(getContext(), "In navigating state.", Toast.LENGTH_SHORT).show();

        getContext().tvBottomSheetHeading.setText(getContext().getString(R.string.btn_parked));
        getContext().tvBottomSheetSubHeading.setVisibility(View.GONE);
        // Temporary marker to show the parking spot location
        getPlaceManager().addParkingSpotMarker(getContext().getMap(), spot.getLatLng());

        getCameraManager().moveToLocation(getContext().getMap(), spot.getLatLng()); // Maybe zoom out to show both
        LatLng currentLocation = getCameraManager().getLastLocation();

        lineManager.createAndDisplay(getContext().getMap(), currentLocation, spot.getLatLng());

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        boundsBuilder.include(currentLocation);
        boundsBuilder.include(spot.getLatLng());
        LatLngBounds bounds = boundsBuilder.build();
        getContext().getMap().moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200));

        bottomSheet.expand();
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
                bottomSheet.expand();
            }

            @Override
            public void onExpanded() {

            }

            @Override
            public void onHidden() {

            }

            @Override
            public void onSettling() {

            }
        });
    }

    @Override
    public void stop() {
        super.stop();
        lineManager.remove();
        getPlaceManager().removeDestinationMarker();
    }
}
