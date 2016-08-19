package com.buzzardparking.buzzard.states;

import android.view.View;

import com.buzzardparking.buzzard.activities.MainActivity;
import com.buzzardparking.buzzard.util.OnMap;
import com.buzzardparking.buzzard.util.PlaceManager;
import com.google.android.gms.maps.GoogleMap;

/**
 * Created by nathansass on 8/17/16.
 */
public class Looking extends StateParent implements OnMap.Listener {

    public Looking(MainActivity appContext, PlaceManager manager) {
        super(appContext, manager);
    }

    @Override
    public void start() {
        if ( mapIsLoaded() ) { /* If the map isn't loaded then onMap will be used */
            updateUI();
        }
    }

    @Override
    public void stop() {
        getContext().mainButton.setText("Not in Looking state");
        getContext().mainButton.setOnClickListener(null); // remove button functionality so next state can reset it

        /*
            After tearing down the customization
            for that app go to the next app state

            getContext().gotTo(NEXT_APP_STATE);
         */
    }

    @Override
    public void updateUI() {
        getContext().mainButton.setText("In Looking state");

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
        Boolean bool = mapIsLoaded();
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
