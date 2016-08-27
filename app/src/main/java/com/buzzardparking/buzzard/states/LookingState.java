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
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterManager;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link LookingState}: a user is looking for a parking spot.
 */
public class LookingState extends UserState implements ClusterManager.OnClusterItemClickListener<Spot>{
    private Spot spotToNavTo;

    public LookingState(Context context, PlaceManager manager, CameraManager cameraManager) {
        super(context, manager, cameraManager);
        this.spotToNavTo = null;
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

        getPlaceManager().addDestinationMarker(getContext().getMap(), googlePlace.getLatLng());
        getCameraManager().moveToLocation(getContext().getMap(), googlePlace.getLatLng());

        // TODO: set the googlePlace to spotToNavTo, so it can be sent along
        // TODO: find the closest spot to this destination, and this is what should be passed along

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

                if (spotToNavTo != null) {
                    getContext().goTo(AppState.NAVIGATING, spotToNavTo);
                } else {

                    LatLng userLoc = getCameraManager().getLastLocation();
                    ParseGeoPoint userGeoPoint = new ParseGeoPoint(userLoc.latitude, userLoc.longitude);

                    // TODO: move to its own function
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Spot");
                    query.whereNear("location", userGeoPoint);
                    query.setLimit(10);
                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {
                            ArrayList<Spot> spotsArray = Spot.fromParse(objects);
                            Spot nearestSpot = spotsArray.get(0);

                            getContext().goTo(AppState.NAVIGATING, nearestSpot);


                        }
                    });
                    Toast.makeText(getContext(), "Automatically choose closest parking space", Toast.LENGTH_SHORT).show();
                }

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
        this.spotToNavTo = spot;
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
