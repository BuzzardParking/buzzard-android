package com.buzzardparking.buzzard.states;

import android.content.Context;

import com.buzzardparking.buzzard.util.PlaceManager;

/**
 * {@link LeavingState}: A user plans or is leaving its parking space.
 */
public class LeavingState extends UserState {

    public LeavingState(Context context, PlaceManager placeManager) {
        super(context, placeManager);
    }

    @Override
    public void start() {
        // TODO:
        // 1. Able to mark the space as to-be-available
        // 2. Able to mark the space as available when the device sensor detects the car is leaving
        // 3. Able to send time information to the server, so the server could keep track of the time elapsedA
        // ...
    }

    @Override
    public void stop() {

    }
}
