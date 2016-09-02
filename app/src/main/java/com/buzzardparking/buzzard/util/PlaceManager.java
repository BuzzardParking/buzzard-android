package com.buzzardparking.buzzard.util;

import android.util.Log;

import com.buzzardparking.buzzard.activities.MapActivity;
import com.buzzardparking.buzzard.models.Spot;
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
 * {@link PlaceManager} listens for the activity's saved instance state, to save and restore
 * any places on rotation.
 */
public class PlaceManager implements
        OnMap.Listener {

    private static final String KEY = "places";
    private final MarkerManager mMarkerManager;
    private ArrayList<Spot> mSpots; // May not need this. Need to know more about screen rotation
    private MapActivity context;

    public PlaceManager(MarkerManager markerManager, MapActivity context) {
        this.mMarkerManager = markerManager;
        this.context = context;
        mSpots = new ArrayList<>();
    }

    public void addPlace(LatLng latLng) {
        Spot newSpot = new Spot(latLng);
        newSpot.save();
        newSpot.saveParse();
        mSpots.add(newSpot);

        mMarkerManager.addMarker(newSpot);
    }

    public void addDestinationMarker(GoogleMap map, LatLng latLng) {
        mMarkerManager.addDestinationMarker(map, latLng);
    }

    public void addCarParkedMarker(GoogleMap map, LatLng latLng) {
        mMarkerManager.addCarParkedMarker(map, latLng);
    }

    public void addParkingSpotMarker(GoogleMap map, LatLng latLng) {
        mMarkerManager.addParkingSpotMarker(map, latLng);
    }

    public void removeDestinationMarker() {
        mMarkerManager.removeDestinationMarker();
    }

    public void clearMap() {
        mMarkerManager.removeMarkers();
    }

    public void loadPlaces(GoogleMap map) {
//        loadFromLocal(map); // If you uncomment this, you must reinstall the app first
        loadFromParse(map);
//        deleteFromParse();
    }

    public void loadFromLocal(GoogleMap map) {
        mSpots.addAll(Spot.getAll());
        mMarkerManager.addAll(mSpots);
    }

    public void loadNearestSpotsOnMap(ParseGeoPoint point, GoogleMap map) {
        loadNearestSpots(point, 3, new NearestSpotListener() {
            @Override
            public void onReturn(ArrayList<Spot> nearestSpots) {
                mSpots.clear();
                mMarkerManager.removeMarkers();

                mSpots.addAll(nearestSpots);
                mMarkerManager.addAll(mSpots);
            }
        });

    }

    public void loadNearestSpots(ParseGeoPoint geoPoint, final int limit, final NearestSpotListener nearestSpotListener) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Spot");
        query.whereNear("location", geoPoint);
        query.setLimit(limit);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                ArrayList<Spot> spotsArray = Spot.fromParse(objects);

                nearestSpotListener.onReturn(spotsArray);

                context.hideProgressBar();

            }
        });
    }


    public void loadFromParse(final GoogleMap map) {
        ParseQuery query = new ParseQuery("Spot");
        query.findInBackground(new FindCallback() {
            @Override
            public void done(Object places, Throwable throwable) {
                mSpots.clear();
                mMarkerManager.removeMarkers();

                mSpots.addAll(Spot.fromParse(places));

                mMarkerManager.addAll(mSpots);

                context.hideProgressBar();
            }

            @Override
            public void done(List objects, ParseException e) {
                Log.v("DEBUG", objects.toString());
            }
        });
    }

    public void deleteFromParse(){
        ParseQuery query = new ParseQuery("Spot");
        query.findInBackground(new FindCallback() {
            @Override
            public void done(List objects, ParseException e) {

            }

            @Override
            public void done(Object places, Throwable throwable) {
                ArrayList<ParseObject> placesToDelete = (ArrayList<ParseObject>) places;
                for (ParseObject place: placesToDelete) {
                    place.deleteInBackground();
                }
            }
        });
    }


    public void addIntoParkingHistory(String userId, Spot spot) {
        spot.saveParkedSpot(userId);
    }

    public ClusterManager getClusterManager() {
        return mMarkerManager.getClusterManager();
    }

    @Override
    public void onMap(GoogleMap map) {
        mMarkerManager.setUpClusterer(map, context);
    }

    public interface NearestSpotListener {
        void onReturn(ArrayList<Spot> nearestSpots);
    }

}