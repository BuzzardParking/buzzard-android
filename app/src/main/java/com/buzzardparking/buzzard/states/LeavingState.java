package com.buzzardparking.buzzard.states;

import android.content.Context;
import android.view.View;

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
        if (isReady() || isReadyCache()) {
            updateUI();
        }
    }

    public void updateUI() {
        // TODO:
        // 1. Able to mark the space as to-be-available
        // 2. Able to mark the space as available when the device sensor detects the car is leaving
        // 3. Able to send time information to the server, so the server could keep track of the time elapsedA
        // ...

        getContext().prepareView();

        bottomSheet.setFabIcon(R.drawable.ic_parked);

        getContext().tvBottomSheetHeading.setText(getContext().getString(R.string.btn_reset));

        getContext().fabBack.setVisibility(View.INVISIBLE);

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
                bottomSheet.expand();
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
        super.stop();
    }

}
