package com.buzzardparking.buzzard.states;

import android.content.Context;

import com.buzzardparking.buzzard.util.PlaceManager;

/**
 * {@link NavigatingState}: a user is navigating to a parking spot.
 */
public class NavigatingState extends UserState {

    public NavigatingState(Context context, PlaceManager placeManager) {
        super(context, placeManager);
    }

    @Override
    public void start() {
        // TODO:
        // 1. hide other unrelated parking spaces during navigation
        // 2. draw poly line of navigation path from current location to the destination parking spot
        // 3. stop button to stop navigating, and back to looking state
        // 4. button to go to the parked state
        // ...
    }

    @Override
    public void stop() {

    }
}
