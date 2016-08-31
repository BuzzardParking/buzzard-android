package com.buzzardparking.buzzard.states;

import android.content.Context;

import com.buzzardparking.buzzard.R;
import com.buzzardparking.buzzard.models.AppState;
import com.buzzardparking.buzzard.models.Spot;
import com.buzzardparking.buzzard.util.BottomSheetManager;
import com.buzzardparking.buzzard.util.CameraManager;
import com.buzzardparking.buzzard.util.PlaceManager;

/**
 * {@link ParkedState}: a user's car is parked at a parking spot.
 */
public class ParkedState extends UserState {

    private Spot spot;

    public ParkedState(Context context, PlaceManager placeManager, CameraManager cameraManager, Spot spot) {
        super(context, placeManager, cameraManager);
        appState = AppState.PARKED;
        this.spot = spot;
    }

    @Override
    public void start() {
        // TODO:
        // 1. show only your car location and your current location on the map
        // 2. a timer hovers above the car starting counting the time
        // 3. a button to switch to leaving state
        // 4. a evaluation modal to ask user to give a thumb up/down about its parking experience
        // 5. able to set up an alarm clock to remind the parking duration
        // 6. able to fav the parking location, and revisit your parking history


        // Temporary marker to show the car location
        getPlaceManager().addCarParkedMarker(getContext().getMap(), spot.getLatLng());
        getCameraManager().moveToLocation(getContext().getMap(), spot.getLatLng());

        // TODO: use real user id, dedup,
        getPlaceManager().addIntoParkingHistory("fake-user-id", spot);

        getContext().tvBottomSheetHeading.setText(getContext().getString(R.string.tv_parked));
        getContext().tvBottomSheetSubHeading.setText(getContext().getString(R.string.tv_parked_subtitle));

        bottomSheet.setFabIcon(R.drawable.ic_parked);
        bottomSheet.setFabListener(new BottomSheetManager.FabListener() {
            @Override
            public void onClick() {
                getContext().goTo(AppState.LEAVING);
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
        getPlaceManager().removeDestinationMarker();
    }
}
