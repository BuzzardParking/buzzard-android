package com.buzzardparking.buzzard.states;

import android.content.Context;
import android.support.annotation.Nullable;

import com.buzzardparking.buzzard.activities.MapActivity;
import com.buzzardparking.buzzard.models.AppState;
import com.buzzardparking.buzzard.models.DynamicSpot;
import com.buzzardparking.buzzard.util.BottomSheetManager;
import com.buzzardparking.buzzard.util.CameraManager;
import com.buzzardparking.buzzard.util.OnClient;
import com.buzzardparking.buzzard.util.OnMap;
import com.buzzardparking.buzzard.util.OnPermission;
import com.buzzardparking.buzzard.util.PlaceManager;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;

/**
 * {@link UserState}: indicates the state a user is currently at.
 * TODO: revisit whether it's a good idea to implement the OnMap listener here
 */
public abstract class UserState implements OnMap.Listener, OnClient.Listener,  OnPermission.Listener {

    // polling every 10s
    protected static final int POLLING_INTERVAL = 10000;

    private Context context;
    private PlaceManager manager;
    private CameraManager cameraManager;
    // TODO: not a good idea to expose a public field from the state, we probably need refactor all UserState later
    public AppState appState;
    protected BottomSheetManager bottomSheet;


    /*
        These are used to detect the first load of a class
     */
    private GoogleApiClient googleApiClient;
    private GoogleMap googleMap;
    private OnPermission.Result permissionResult;
    //////

    public DynamicSpot dynamicSpot;

    public UserState(Context context, PlaceManager manager, CameraManager cameraManager) {
        this.context = context;
        this.manager = manager;
        this.cameraManager = cameraManager;
        this.bottomSheet = new BottomSheetManager(getContext(), getContext().getBottomSheetBehavior());
        this.dynamicSpot = null;
        appState = null;
        this.googleMap = null;
        this.googleApiClient = null;
        this.permissionResult = null;
    }

    /**
     * This will get called when the map is ready to be manipulated.
     * @param map  {@link GoogleMap}
     */
    @Override
    public void onMap(GoogleMap map) {
        this.googleMap = map;
        if (isReady())
            start();
    }

    @Override
    public void onClient(@Nullable GoogleApiClient client) {
        this.googleApiClient = client;

        if (isReady())
            start();
    }

    @Override
    public void onResult(int requestCode, OnPermission.Result result) {
        this.permissionResult = result;

        if (isReady())
            start();
    }

    /**
     * This is used for when the app loads.
     * or when a screen orientation is changed and the state reloads
     */
    public boolean isReady() {
        return (googleMap != null) &&
                (googleApiClient != null) &&
                googleApiClient.isConnected() &&
                (permissionResult == OnPermission.Result.GRANTED);
    }

    /**
     * This is used for subsequent loads of state.
     * ie. Navigating from one state to the next.
     */
    public boolean isReadyCache() {
        return getContext().buzzardMap.isLoaded() &&
                getContext().googleClient.isConnected() &&
                getContext().permissions.isGranted();
    }

    /**
     * The state starts.
     *
     * Operation about this state should happen here, e.g. update UI elements.
     */
    public abstract void start();

    /**
     * The state stops.
     *
     * Operation about this state should happen here, e.g. proper clean up.
     */


    /**
     * This is used by map activity to saveParse the state
     */
    public DynamicSpot getDynamicSpot() {
        return dynamicSpot;
    }

    public void stop() {
        bottomSheet.setFabListener(null);
        bottomSheet.setBottomSheetStateListeners(null);
        ((MapActivity) context).clearBottomSheetHeadings();
    }

    public MapActivity getContext() {
        return (MapActivity)context;
    }


    public PlaceManager getPlaceManager() {
        return manager;
    }

    public CameraManager getCameraManager() { return cameraManager; }
}
