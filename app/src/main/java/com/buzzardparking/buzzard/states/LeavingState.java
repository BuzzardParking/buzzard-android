package com.buzzardparking.buzzard.states;

import android.content.Context;

import com.buzzardparking.buzzard.R;
import com.buzzardparking.buzzard.models.AppState;
import com.buzzardparking.buzzard.util.BottomSheetManager;
import com.buzzardparking.buzzard.util.CameraManager;
import com.buzzardparking.buzzard.util.PlaceManager;

/**
 * {@link LeavingState}: A user plans or is leaving its parking space.
 */
public class LeavingState extends UserState {

    public LeavingState(Context context, PlaceManager placeManager, CameraManager cameraManager) {
        super(context, placeManager, cameraManager);
        appState = AppState.LEAVING;
    }

    @Override
    public void start() {
        // TODO:
        // 1. Able to mark the space as to-be-available
        // 2. Able to mark the space as available when the device sensor detects the car is leaving
        // 3. Able to send time information to the server, so the server could keep track of the time elapsedA
        // ...

        getContext().tvBottomSheetHeading.setText(getContext().getString(R.string.btn_reset));

        bottomSheet.expand();
        bottomSheet.setFabListener(new BottomSheetManager.FabListener() {
            @Override
            public void onClick() {
                getContext().goTo(AppState.OVERVIEW);
            }
        });

        getCameraManager().moveToUserLocation(); //TODO: This doesn't seem to be working

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
    }
}
