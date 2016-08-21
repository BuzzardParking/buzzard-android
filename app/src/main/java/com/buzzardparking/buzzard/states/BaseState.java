package com.buzzardparking.buzzard.states;

import android.content.Context;

import com.buzzardparking.buzzard.activities.MainActivity;
import com.buzzardparking.buzzard.util.OnMap;
import com.buzzardparking.buzzard.util.PlaceManager;
import com.google.android.gms.maps.GoogleMap;

/**
 * BaseState
 */
public abstract class BaseState implements OnMap.Listener {
    private Context context;
    private PlaceManager manager;

    GoogleMap googleMap;

    public BaseState(Context context, PlaceManager manager) {
        this.context = context;
        this.manager = manager;
    }

    public Boolean mapIsLoaded() {
        // TODO: This may need to be more sophisticated
        return googleMap != null;
    }

    /**
     * This will get called when the map is ready to be manipulated
     * @param map  {@link GoogleMap}
     */
    @Override
    public void onMap(GoogleMap map) {
        this.googleMap = map;
    }

    /**
     * Starts the state machine
     */
    public abstract void start();

    /**
     * Stops the state machine
     */
    public abstract void stop();

    public abstract void updateUI();

    public MainActivity getContext() {
        return (MainActivity)context;
    }

    public PlaceManager getManager() {
        return manager;
    }
}
