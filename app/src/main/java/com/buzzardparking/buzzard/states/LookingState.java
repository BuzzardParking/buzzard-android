package com.buzzardparking.buzzard.states;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.buzzardparking.buzzard.R;
import com.buzzardparking.buzzard.models.AppState;
import com.buzzardparking.buzzard.util.PlaceManager;
import com.google.android.gms.maps.GoogleMap;

/**
 * {@link LookingState}: a user is looking for a parking spot.
 */
public class LookingState extends UserState {

    public LookingState(Context context, PlaceManager manager) {
        super(context, manager);
    }

    @Override
    public void start() {
        updateUI();
    }

    @Override
    public void stop() {
        super.stop();
        getManager().clearPlaces();
    }

    private void updateUI() {
        Toast.makeText(getContext(), "In looking state.", Toast.LENGTH_SHORT).show();
        actionButton.setText(getContext().getString(R.string.btn_navigating));

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getContext().goTo(AppState.NAVIGATING);
            }
        });
    }

    @Override
    public void onMap(GoogleMap map) {
//        getManager().loadPlaces(map);
//        updateUI();
    }
}

/*
    Roadmap:

    - Load in data from a DB
    - Display it on the map
    - Allow users to add more pins with a longclick
    - These pins will persist in the DB

 */
