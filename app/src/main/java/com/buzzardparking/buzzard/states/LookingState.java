package com.buzzardparking.buzzard.states;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.buzzardparking.buzzard.R;
import com.buzzardparking.buzzard.gateways.RouteGateway;
import com.buzzardparking.buzzard.models.AppState;
import com.buzzardparking.buzzard.models.Route;
import com.buzzardparking.buzzard.models.Spot;
import com.buzzardparking.buzzard.util.BottomSheetManager;
import com.buzzardparking.buzzard.util.CameraManager;
import com.buzzardparking.buzzard.util.PlaceManager;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterManager;
import com.parse.ParseGeoPoint;

/**
 * {@link LookingState}: a user is looking for a parking spot.
 */
public class LookingState extends UserState implements ClusterManager.OnClusterItemClickListener<Spot>{
    private Spot spotToNavTo;

    public LookingState(Context context, PlaceManager manager, CameraManager cameraManager) {
        super(context, manager, cameraManager);
        this.spotToNavTo = null;
        appState = AppState.LOOKING;
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
        getContext().tvBottomSheetSubHeading.setText(googlePlace.getAddress());

        LatLng userLoc = getCameraManager().getLastLocation();

        RouteGateway.getRoute(userLoc, googlePlace.getLatLng(), new RouteGateway.RouteGatewayListener() {
            @Override
            public void onReturn(Route returnedRoute) {
                getContext().tvBottomSheetSubheadingRight.setText(returnedRoute.getDuration());
            }
        });

        getPlaceManager().addDestinationMarker(getContext().getMap(), googlePlace.getLatLng());
        getCameraManager().moveToLocation(getContext().getMap(), googlePlace.getLatLng());

        ParseGeoPoint googlePlaceParsePoint = new ParseGeoPoint(googlePlace.getLatLng().latitude, googlePlace.getLatLng().longitude);


        getPlaceManager().getNearestSpot(googlePlaceParsePoint, new PlaceManager.NearestSpotListener() {
            @Override
            public void onReturn(Spot nearestSpot) {
                spotToNavTo = nearestSpot;
                // TODO: Also pass along the google place
            }
        });

        Toast.makeText(getContext(), "Chose closest parking space to " + googlePlace.getName(), Toast.LENGTH_SHORT).show();

    }

    public void showParkingSpaceDetails(Spot spot) {
        // TODO: load google map street view as part of the details
        getContext().tvBottomSheetHeading.setText("User parking space");
        getContext().tvBottomSheetSubHeading.setText("details");
        getContext().tvBottomSheetSubheadingRight.setText("...");

        LatLng userLoc = getCameraManager().getLastLocation();
        RouteGateway.getRoute(userLoc, spot.getLatLng(), new RouteGateway.RouteGatewayListener() {
            @Override
            public void onReturn(Route returnedRoute) {
                getContext().tvBottomSheetSubheadingRight.setText(returnedRoute.getDuration());
            }
        });
    }

    private void updateUI() {

        getPlaceManager().loadPlaces(getContext().getMap());

        getCameraManager().moveToUserLocation();

        getContext().tvBottomSheetHeading.setText(getContext().getString(R.string.btn_navigating));

        bottomSheet.expand();
        bottomSheet.setFabIcon(R.drawable.ic_navigation);
        bottomSheet.setFabListener(new BottomSheetManager.FabListener() {
            @Override
            public void onClick() {

                if (spotToNavTo != null) {
                    startNavigating();
                } else {
                    LatLng userLoc = getCameraManager().getLastLocation();
                    ParseGeoPoint userGeoPoint = new ParseGeoPoint(userLoc.latitude, userLoc.longitude);

                    getPlaceManager().getNearestSpot(userGeoPoint, new PlaceManager.NearestSpotListener() {
                        @Override
                        public void onReturn(Spot nearestSpot) {
                            spotToNavTo = nearestSpot;
                            startNavigating();
                        }
                    });

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

    private void startNavigating() {
        // launch in-app navigation
        getContext().goTo(AppState.NAVIGATING, spotToNavTo);

        // launch external navigation as well
        LatLng latLng = spotToNavTo.getLatLng();
        Uri gmmIntentUri = Uri.parse(String.format("google.navigation:q=%s,%s", latLng.latitude, latLng.longitude));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        getContext().startActivity(mapIntent);
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
