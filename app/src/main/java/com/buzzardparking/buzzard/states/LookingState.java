package com.buzzardparking.buzzard.states;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.buzzardparking.buzzard.R;
import com.buzzardparking.buzzard.gateways.ImageGateway;
import com.buzzardparking.buzzard.gateways.ReverseGeocodingGateway;
import com.buzzardparking.buzzard.gateways.RouteGateway;
import com.buzzardparking.buzzard.models.AppState;
import com.buzzardparking.buzzard.models.DynamicSpot;
import com.buzzardparking.buzzard.models.Route;
import com.buzzardparking.buzzard.util.BottomSheetManager;
import com.buzzardparking.buzzard.util.CameraManager;
import com.buzzardparking.buzzard.util.PlaceManager;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterManager;
import com.parse.ParseGeoPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * {@link LookingState}: a user is looking for a parking dynamicSpot.
 */
public class LookingState extends UserState
        implements ClusterManager.OnClusterItemClickListener<DynamicSpot>{

    private boolean isDestinationDetails;

    public LookingState(Context context, PlaceManager manager, CameraManager cameraManager) {
        super(context, manager, cameraManager);
        appState = AppState.LOOKING;
    }

    @Override
    public void start() {
        getContext().user.setCurrentState(AppState.LOOKING);
        getContext().showProgressBar();

        if (isReady() || isReadyCache()) {
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
        isDestinationDetails = true;

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

        getPlaceManager().loadNearestSpots(googlePlaceParsePoint,1, new PlaceManager.NearestSpotListener() {

            @Override
            public void onReturn(ArrayList<DynamicSpot> nearestSpots) {
                dynamicSpot = nearestSpots.get(0);
            }
        });

        displayPlaceImage(googlePlace.getLatLng());

        Toast.makeText(getContext(), "Choose closest parking space to " + googlePlace.getName(), Toast.LENGTH_SHORT).show();

    }


    public void showParkingSpaceDetails(DynamicSpot spot) {
        // TODO: load google map street view as part of the details
        isDestinationDetails = true;
        getContext().tvBottomSheetHeading.setText("User parking space");
        getContext().tvBottomSheetSubHeading.setText("details");
        getContext().tvBottomSheetSubheadingRight.setText("...");

        ReverseGeocodingGateway geocodingGateway = new ReverseGeocodingGateway();

        geocodingGateway.FetchAddressInBackground(spot.getLatLng(), new ReverseGeocodingGateway.GetAddressCallback() {
            @Override
            public void done(JSONObject jsonObject) throws JSONException {
                try {
                    JSONArray resultsArr = jsonObject.getJSONArray("results");
                    JSONObject first = resultsArr.getJSONObject(0);
                    JSONArray addressComponents = first.getJSONArray("address_components");
                    String streetNum = addressComponents.getJSONObject(0).getString("long_name");
                    String streetName = addressComponents.getJSONObject(1).getString("short_name");

                    String address = streetNum + " " + streetName;
                    getContext().tvBottomSheetHeading.setText(address);



                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        LatLng userLoc = getCameraManager().getLastLocation();
        RouteGateway.getRoute(userLoc, spot.getLatLng(), new RouteGateway.RouteGatewayListener() {
            @Override
            public void onReturn(Route returnedRoute) {
                getContext().tvBottomSheetSubheadingRight.setText(returnedRoute.getDuration());
            }
        });

        displayPlaceImage(spot.getLatLng());

    }

    private void displayPlaceImage(LatLng latLng) {
        String imageUrl = ImageGateway.getPlaceImage(getContext(), latLng);
        Glide.with(getContext())
                .load(imageUrl)
                .into(getContext().ivStreetView);
        getContext().ivStreetView.setVisibility(View.VISIBLE);
    }

    private void updateUI() {
        initialUI();

        LatLng userLoc = getCameraManager().getLastLocation();
        ParseGeoPoint userGeoPoint = new ParseGeoPoint(userLoc.latitude, userLoc.longitude);

        getPlaceManager().loadNearestSpotsOnMap(userGeoPoint, getContext().getMap()); // Loads 3 closest places

        getCameraManager().moveToUserLocation();


        bottomSheet.expand();

        setBackButtonListener();
        bottomSheet.setFabIcon(R.drawable.ic_navigation);
        bottomSheet.setFabListener(new BottomSheetManager.FabListener() {
            @Override
            public void onClick() {

                if (dynamicSpot != null) {
                    startNavigating();
                } else {
                    LatLng userLoc = getCameraManager().getLastLocation();
                    ParseGeoPoint userGeoPoint = new ParseGeoPoint(userLoc.latitude, userLoc.longitude);

                    getPlaceManager().loadNearestSpots(userGeoPoint, 10, new PlaceManager.NearestSpotListener() {
                        @Override
                        public void onReturn(ArrayList<DynamicSpot> nearestSpots) {
                            if (!nearestSpots.isEmpty()) {
                                dynamicSpot = nearestSpots.get(0);
                                startNavigating();
                            }
                        }
                    });

                }
            }
        });

        getPlaceManager().getClusterManager().setOnClusterItemClickListener(this);

        bottomSheet.setBottomSheetStateListeners(new BottomSheetManager.BottomSheetListeners() {
            @Override
            public void onCollapsed() {
                if (isDestinationDetails) {
                    initialUI();
                    final View myBottomSheet = getContext().findViewById(R.id.rlBottomSheet);

                    myBottomSheet.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        public void onGlobalLayout() {
                            myBottomSheet.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                            bottomSheet.expand();
                        }
                    });
                }
                bottomSheet.expand();
            }

            @Override
            public void onDragging() {



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

    private void initialUI() {
        isDestinationDetails = false;
        getContext().prepareView();
        getContext().rlTopPieceContainer.setVisibility(View.VISIBLE);
        getContext().tvBottomSheetHeading.setText(getContext().getString(R.string.btn_navigating));

    }

    private void setBackButtonListener() {
        getContext().fabBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getContext().goTo(appState.OVERVIEW);
            }
        });
    }

    private void startNavigating() {
        // launch in-app navigation
        if (dynamicSpot != null) {
            getContext().goTo(AppState.NAVIGATING, dynamicSpot);
        } else {
            getContext().goTo(AppState.NAVIGATING);
        }

        if (getContext().user.doesPreferExternalNavigation()) {
            // launch external navigation as well
            LatLng latLng = dynamicSpot.getLatLng();
            Uri gmmIntentUri = Uri.parse(String.format("google.navigation:q=%s,%s", latLng.latitude, latLng.longitude));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            getContext().startActivity(mapIntent);
        }
    }

    @Override
    public boolean onClusterItemClick(DynamicSpot dynamicSpot) {
        showParkingSpaceDetails(dynamicSpot);
        bottomSheet.expand();
        this.dynamicSpot = dynamicSpot;
        return true;
    }
}
