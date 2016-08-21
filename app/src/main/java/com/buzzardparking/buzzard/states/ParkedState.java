package com.buzzardparking.buzzard.states;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.buzzardparking.buzzard.R;
import com.buzzardparking.buzzard.models.AppState;
import com.buzzardparking.buzzard.util.PlaceManager;

/**
 * {@link ParkedState}: a user's car is parked at a parking spot.
 */
public class ParkedState extends UserState {

    public ParkedState(Context context, PlaceManager placeManager) {
        super(context, placeManager);
    }

    @Override
    public void start() {
        // TODO:
        // 1. show only your car location and your current location on the map
        // 2. a timer hovers above the car starting counting the time
        // 3. a button to switch to leaving state
        // 4. a evaluation modal to ask user to give a thumb up/down about its parking experience
        // 5. able to set up an alarm clock to remind the parking duration
        // 6. able to fav the parking location, and revisit your parking history

        Toast.makeText(getContext(), "In parked state.", Toast.LENGTH_SHORT).show();
        actionButton.setText(getContext().getString(R.string.btn_leaving));

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getContext().goTo(AppState.LEAVING);
            }
        });
    }

    @Override
    public void stop() {
        super.stop();
    }
}
