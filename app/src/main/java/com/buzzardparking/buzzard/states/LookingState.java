package com.buzzardparking.buzzard.states;

import android.content.Context;
import android.view.View;

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
        if (mapIsLoaded()) {
            updateUI();
        }
    }

    @Override
    public void stop() {
        getContext().mainButton.setText("Not in LookingState state");
        getContext().mainButton.setOnClickListener(null); // remove button functionality so next state can reset it

        /*
            After tearing down the customization
            for that app go to the next app state

            getContext().goTo(NEXT_APP_STATE);
         */
    }

    private void updateUI() {
        getContext().mainButton.setText("In LookingState state");

        getContext().mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getManager().clearPlaces();
            }
        });
    }

    @Override
    public void onMap(GoogleMap map) {
        super.onMap(map);
        getManager().loadPlaces(map);
        updateUI();
    }
}

/*
    Roadmap:

    - Load in data from a DB
    - Display it on the map
    - Allow users to add more pins with a longclick
    - These pins will persist in the DB

 */
