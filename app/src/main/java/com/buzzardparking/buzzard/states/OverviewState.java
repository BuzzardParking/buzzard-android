package com.buzzardparking.buzzard.states;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.buzzardparking.buzzard.R;
import com.buzzardparking.buzzard.models.AppState;
import com.buzzardparking.buzzard.util.PlaceManager;

/**
 * {@link OverviewState}: default state when a user starts using the app.
 *
 * In this state, we'll show an aggregated view of available parking spaces on the map.
 * The action button can navigate the user to next state: {@link LookingState}.
 */
public class OverviewState extends UserState {

    public OverviewState(Context context, PlaceManager placeManager) {
        super(context, placeManager);
    }

    @Override
    public void start() {
        Toast.makeText(getContext(), "In overview state.", Toast.LENGTH_SHORT).show();
        actionButton.setText(getContext().getString(R.string.btn_looking));

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
