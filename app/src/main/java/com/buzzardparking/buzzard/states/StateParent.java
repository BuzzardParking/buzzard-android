package com.buzzardparking.buzzard.states;

import com.buzzardparking.buzzard.activities.MainActivity;
import com.buzzardparking.buzzard.interfaces.Controller;
import com.buzzardparking.buzzard.util.OnMap;
import com.buzzardparking.buzzard.util.PlaceManager;
import com.google.android.gms.maps.GoogleMap;

/**
 * StateParent
 */
public abstract class StateParent implements Controller, OnMap.Listener {
    private MainActivity context;

    private PlaceManager manager;

    GoogleMap googleMap;

    public StateParent(MainActivity appContext, PlaceManager manager) {
        this.context = appContext;
        this.manager = manager;
    }

    public Boolean mapIsLoaded() {
        // This may need to be more sophisticated

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
    @Override
    public void start() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Stops the state machine
     */
    @Override
    public void stop() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public abstract void updateUI();

    public MainActivity getContext() {
        return context;
    }

    public PlaceManager getManager() {
        return manager;
    }
}
