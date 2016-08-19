package com.buzzardparking.buzzard.states;

import com.buzzardparking.buzzard.activities.MainActivity;
import com.buzzardparking.buzzard.interfaces.Controller;
import com.buzzardparking.buzzard.util.OnMap;
import com.buzzardparking.buzzard.util.PlaceManager;
import com.google.android.gms.maps.GoogleMap;

/**
 * Created by nathansass on 8/19/16.
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

    @Override
    public void start() {
        // From StateMachine
    }

    @Override
    public void onMap(GoogleMap map) {
        this.googleMap = map;
        // This will get called when the map is ready to be manipulated
    }

    @Override
    public void stop() {
        // From StateMachine
    }

    public abstract void updateUI();

    public MainActivity getContext() {
        return context;
    }

    public PlaceManager getManager() {
        return manager;
    }
}
