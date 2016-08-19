package com.buzzardparking.buzzard.states;

import android.view.View;

import com.buzzardparking.buzzard.activities.MainActivity;
import com.buzzardparking.buzzard.util.PlaceManager;

/**
 * Created by nathansass on 8/17/16.
 */
public class Looking extends StateParent {

    public Looking(MainActivity appContext, PlaceManager manager) {
        super(appContext, manager);
    }

    @Override
    public void start() {
        updateUI();
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

}
