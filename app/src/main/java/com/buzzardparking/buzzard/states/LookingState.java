package com.buzzardparking.buzzard.states;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.view.View;

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
    private Handler handler = new Handler();
    private Runnable loadPlacesRunnable = new Runnable() {
        @Override
        public void run() {
            LatLng userLoc = getCameraManager().getLastLocation();
            if (userLoc == null) {
                return;
            }

            ParseGeoPoint userGeoPoint = new ParseGeoPoint(userLoc.latitude, userLoc.longitude);
            getPlaceManager().loadNearestSpotsOnMap(userGeoPoint, getContext().getMap()); // Loads 3 closest places
            getPlaceManager().loadPlaces(getContext().getMap());

            handler.postDelayed(loadPlacesRunnable, POLLING_INTERVAL);
        }
    };

    public LookingState(Context context, PlaceManager manager, CameraManager cameraManager) {
        super(context, manager, cameraManager);
        appState = AppState.LOOKING;
    }

    public LookingState(Context context,
                        PlaceManager manager,
                        CameraManager cameraManager,
                        DynamicSpot dynamicSpot) {
        super(context, manager, cameraManager);
        appState = AppState.LOOKING;
        this.dynamicSpot = dynamicSpot;
    }


    @Override
    public void start() {
        getContext().user.setCurrentState(AppState.LOOKING);

        // for back button
        if (dynamicSpot != null) {
            dynamicSpot.unlock();
        }

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
        handler.removeCallbacks(loadPlacesRunnable);
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
    }


    public void showParkingSpaceDetails(DynamicSpot spot) {
        // TODO: load google map street view as part of the details
        isDestinationDetails = true;
        getContext().tvBottomSheetHeading.setText(getContext().getResources().getString(R.string.default_address));
        getContext().tvBottomSheetSubHeading.setText("Right Here, USA");
        getContext().tvBottomSheetSubheadingRight.setText("...");
        getContext().tvBottomSheetReporter.setText(
                "Reported by: " + spot.getReporterFirstName() + "\nReported at: " + spot.getCreatedAtTimestamp());

        displayAddress(spot.getLatLng());

        LatLng userLoc = getCameraManager().getLastLocation();
        RouteGateway.getRoute(userLoc, spot.getLatLng(), new RouteGateway.RouteGatewayListener() {
            @Override
            public void onReturn(Route returnedRoute) {
                getContext().tvBottomSheetSubheadingRight.setText(returnedRoute.getDuration());
            }
        });

        displayPlaceImage(spot.getLatLng());

    }

    public void displayAddress(LatLng latLng) {
        ReverseGeocodingGateway geocodingGateway = new ReverseGeocodingGateway();
        geocodingGateway.FetchAddressInBackground(latLng, new ReverseGeocodingGateway.GetAddressCallback() {
            @Override
            public void done(JSONObject jsonObject) throws JSONException {
                try {
                    JSONArray resultsArr = jsonObject.getJSONArray("results");
                    JSONObject first = resultsArr.getJSONObject(0);
                    JSONArray addressComponents = first.getJSONArray("address_components");

                    String streetNum = addressComponents.getJSONObject(0).getString("long_name");
                    String streetName = addressComponents.getJSONObject(1).getString("short_name");
                    String city = addressComponents.getJSONObject(3).getString("long_name");
                    String state = addressComponents.getJSONObject(5).getString("short_name");

                    String address = streetNum + " " + streetName;

                    if (streetName == "null") {
                        address = getContext().getResources().getString(R.string.default_address);
                    }

                    if (city == "null") {
                        city = getContext().getResources().getString(R.string.default_city);
                        state = "USA";
                    }

                    getContext().tvBottomSheetHeading.setText(address);
                    getContext().tvBottomSheetSubHeading.setText(city + ", " + state);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void displayPlaceImage(LatLng latLng) {
        String imageUrl = ImageGateway.getPlaceImage(latLng);
        Glide.with(getContext())
                .load(imageUrl)
                .into(getContext().ivStreetView);
        getContext().ivStreetView.setVisibility(View.VISIBLE);
    }

    private void updateUI() {
        initialUI();

        handler.post(loadPlacesRunnable);

        getCameraManager().moveToUserLocation();


        bottomSheet.expand();
        bottomSheet.viewRendered(new BottomSheetManager.SheetRendering() {
            @Override
            public void done() {
                bottomSheet.expand();
            }
        });

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

                    bottomSheet.viewRendered(new BottomSheetManager.SheetRendering() {
                        @Override
                        public void done() {
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
