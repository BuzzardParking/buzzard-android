package com.buzzardparking.buzzard.states;

import android.content.Context;
import android.widget.Button;

import com.buzzardparking.buzzard.activities.MainActivity;
import com.buzzardparking.buzzard.util.BottomSheetManager;
import com.buzzardparking.buzzard.util.CameraManager;
import com.buzzardparking.buzzard.util.OnMap;
import com.buzzardparking.buzzard.util.PlaceManager;
import com.google.android.gms.maps.GoogleMap;

/**
 * {@link UserState}: indicates the state a user is currently at.
 * TODO: revisit whether it's a good idea to implement the OnMap listener here
 */
public abstract class UserState implements OnMap.Listener {
    private Context context;
    private PlaceManager manager;
    private CameraManager cameraManager;

    protected BottomSheetManager bottomSheet;

    public UserState(Context context, PlaceManager manager, CameraManager cameraManager) {
        this.context = context;
        this.manager = manager;
        this.cameraManager = cameraManager;
        this.bottomSheet = new BottomSheetManager(getContext(), getContext().getBottomSheet());
    }

    /**
     * This will get called when the map is ready to be manipulated.
     * @param map  {@link GoogleMap}
     */
    @Override
    public void onMap(GoogleMap map) {
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
    public void stop() {
        bottomSheet.setFabListener(null);
        bottomSheet.setBottomSheetStateListeners(null);
    }

    public MainActivity getContext() {
        return (MainActivity)context;
    }


    public PlaceManager getPlaceManager() {
        return manager;
    }

    public CameraManager getCameraManager() { return cameraManager; }
}
