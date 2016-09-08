package com.buzzardparking.buzzard.util;

import android.util.Log;

import com.buzzardparking.buzzard.activities.MapActivity;
import com.buzzardparking.buzzard.models.DynamicSpot;
import com.buzzardparking.buzzard.models.User;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterManager;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * {@link PlaceManager} listens for the activity's saved instance state, to saveParse and restore
 * any places on rotation.
 */
public class PlaceManager implements
        OnMap.Listener {

    private static final String KEY = "places";
    private final MarkerManager mMarkerManager;
    private ArrayList<DynamicSpot> mSpots; // May not need this. Need to know more about screen rotation
    private MapActivity context;

    public PlaceManager(MarkerManager markerManager, MapActivity context) {
        this.mMarkerManager = markerManager;
        this.context = context;
        mSpots = new ArrayList<>();
    }

    public void addPlace(LatLng latLng) {
        DynamicSpot newSpot = new DynamicSpot(latLng, User.getInstance());
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

//    public void addParkingSpotMarker(GoogleMap map, LatLng latLng) {
//        mMarkerManager.addParkingSpotMarker(map, latLng);
//    }

    public void addParkingSpotMarker(GoogleMap map, DynamicSpot spot) {
        if (spot.isNew()) {
            mMarkerManager.addParkingSpotMarkerNew(map, spot.getLatLng());
        } else {
            mMarkerManager.addParkingSpotMarkerOld(map, spot.getLatLng());
        }
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
    }

//    public void loadFromLocal(GoogleMap map) {
//        mSpots.addAll(Spot.getAll());
//        mMarkerManager.addAll(mSpots);
//    }

    public void loadNearestSpotsOnMap(ParseGeoPoint point, GoogleMap map) {
        loadNearestSpots(point, 3, new NearestSpotListener() {
            @Override
            public void onReturn(ArrayList<DynamicSpot> nearestSpots) {
                mSpots.clear();
                mMarkerManager.removeMarkers();

                mSpots.addAll(nearestSpots);
                mMarkerManager.addAll(mSpots);
            }
        });

    }

    /**
     * Only load open spots -- exclude locked and taken spots
     * @param geoPoint
     * @param limit
     * @param nearestSpotListener
     */
    public void loadNearestSpots(ParseGeoPoint geoPoint, final int limit, final NearestSpotListener nearestSpotListener) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("DynamicSpot");
        query
                .include("staticSpot")
                .include("producer")
                .include("consumer")
                .whereEqualTo("takenAt", null)
                .whereEqualTo("lockedAt", null)
                .whereEqualTo("expiredAt", null)
                .whereNear("location", geoPoint)
                .setLimit(limit)
                .findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        ArrayList<DynamicSpot> spots = DynamicSpot.fromParseDynamicSpots(objects);
                        filterOutExpiredSpots(spots);
                        nearestSpotListener.onReturn(spots);

                        context.hideProgressBar();

                    }
                });
    }


    /**
     * Only load open spots -- exclude locked and taken spots
     * @param map
     */
    public void loadFromParse(final GoogleMap map) {
        ParseQuery query = new ParseQuery("DynamicSpot");
        query
                .include("producer")
                .include("staticSpot")
                .include("consumer")
                .whereEqualTo("takenAt", null)
                .whereEqualTo("lockedAt", null)
                .whereEqualTo("expiredAt", null)
                .orderByDescending("updatedAt")
                .findInBackground(new FindCallback() {
                    @Override
                    public void done(Object places, Throwable throwable) {
                        mSpots.clear();
                        mMarkerManager.removeMarkers();

                        List<DynamicSpot> spots = DynamicSpot.fromParseDynamicSpots(places);

                        filterOutExpiredSpots(spots);

                        mSpots.addAll(spots);

                        mMarkerManager.addAll(mSpots);

                        context.hideProgressBar();
                    }

            @Override
            public void done(List objects, ParseException e) {
                Log.v("DEBUG", objects.toString());
            }
        });
    }

    private void filterOutExpiredSpots(List<DynamicSpot> spots) {
        // TODO: do batch operation in the future
        Iterator<DynamicSpot> iter = spots.iterator();

        while (iter.hasNext()) {
            DynamicSpot spot = iter.next();
            if (spot.isExpired()) {
                spot.expireSpot();
                iter.remove();
            }
        }
    }

    public ClusterManager getClusterManager() {
        return mMarkerManager.getClusterManager();
    }

    @Override
    public void onMap(GoogleMap map) {
        mMarkerManager.setUpClusterer(map, context);
    }

    public interface NearestSpotListener {
        void onReturn(ArrayList<DynamicSpot> nearestSpots);
    }

}