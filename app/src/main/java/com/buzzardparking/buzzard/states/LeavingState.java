package com.buzzardparking.buzzard.states;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.buzzardparking.buzzard.R;
import com.buzzardparking.buzzard.models.AppState;
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
        Toast.makeText(getContext(), "In leaving state.", Toast.LENGTH_SHORT).show();
        actionButton.setText(getContext().getString(R.string.btn_reset));

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getContext().goTo(AppState.LOOKING);
            }
        });
    }

    @Override
    public void stop() {
        super.stop();
    }
}
