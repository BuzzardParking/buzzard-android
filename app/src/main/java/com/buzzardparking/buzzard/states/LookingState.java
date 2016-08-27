package com.buzzardparking.buzzard.states;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.buzzardparking.buzzard.R;
import com.buzzardparking.buzzard.models.AppState;
import com.buzzardparking.buzzard.models.Spot;
import com.buzzardparking.buzzard.util.BottomSheetManager;
import com.buzzardparking.buzzard.util.CameraManager;
import com.buzzardparking.buzzard.util.PlaceManager;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.GoogleMap;
import com.google.maps.android.clustering.ClusterManager;

/**
 * {@link LookingState}: a user is looking for a parking spot.
 */
public class LookingState extends UserState implements ClusterManager.OnClusterItemClickListener<Spot>{
    Spot spotToNavTo;

    public LookingState(Context context, PlaceManager manager, CameraManager cameraManager) {
        super(context, manager, cameraManager);
        APP_STATE = AppState.LOOKING;
    }

    @Override
    public void start() {
        if (getContext().buzzardMap.isLoaded()) {
            updateUI();
        }
    }

    @Override
    public void stop() {
        super.stop();
        getPlaceManager().clearMap();
        getPlaceManager().removeDestinationMarker();
    }

    public void showDestinationDetails(Place googlePlace) {
        getPlaceManager().removeDestinationMarker();

        getContext().tvBottomSheetHeading.setText(googlePlace.getName());
        getContext().tvBottomSheetSubHeading.setVisibility(View.VISIBLE);
        getContext().tvBottomSheetSubHeading.setText(googlePlace.getAddress());

        getPlaceManager().addDestinationMarker(getContext().getMap(), googlePlace);
        getCameraManager().moveToLocation(getContext().getMap(), googlePlace.getLatLng());

    }

    public void showParkingSpaceDetails(Spot spot) {
        getContext().tvBottomSheetHeading.setText("User parking space");
        getContext().tvBottomSheetSubHeading.setVisibility(View.VISIBLE);
        getContext().tvBottomSheetSubHeading.setText("details");
    }

    private void updateUI() {
        Toast.makeText(getContext(), "In looking state.", Toast.LENGTH_SHORT).show();

        getPlaceManager().loadPlaces(getContext().getMap());

        getCameraManager().moveToUserLocation();

        getContext().tvBottomSheetHeading.setText(getContext().getString(R.string.btn_navigating));
        getContext().tvBottomSheetSubHeading.setVisibility(View.GONE);

        bottomSheet.expand();
        bottomSheet.setFabListener(new BottomSheetManager.FabListener() {
            @Override
            public void onClick() {
                getContext().goTo(AppState.NAVIGATING);
            }
        });

        getPlaceManager().getClusterManager().setOnClusterItemClickListener(this);

        bottomSheet.setBottomSheetStateListeners(new BottomSheetManager.BottomSheetListeners() {
            @Override
            public void onCollapsed() {

            }

            @Override
            public void onDragging() {
                bottomSheet.expand();
            }

            @Override
            public void onExpanded() {

            }

            @Override
            public void onHidden() {

            }

            @Override
            public void onSettling() {

            }
        });

    }

    @Override
    public void onMap(GoogleMap map) {
        updateUI();
    }

    @Override
    public boolean onClusterItemClick(Spot spot) {
        showParkingSpaceDetails(spot);
        // TODO get ready to send this to the navigation
        return true;
    }
}

/*
    Roadmap:

    - Load in data from a DB
    - Display it on the map
    - Allow users to add more pins with a longclick
    - These pins will persist in the DB

 */
