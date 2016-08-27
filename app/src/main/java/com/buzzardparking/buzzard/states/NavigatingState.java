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

/**
 * {@link NavigatingState}: a user is navigating to a parking spot.
 */
public class NavigatingState extends UserState {

    private Spot spot;

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
        // ...
        Toast.makeText(getContext(), "In navigating state.", Toast.LENGTH_SHORT).show();

        getContext().tvBottomSheetHeading.setText(getContext().getString(R.string.btn_parked));
        getContext().tvBottomSheetSubHeading.setVisibility(View.GONE);

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
    }
}
