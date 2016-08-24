package com.buzzardparking.buzzard.util;

import android.os.Bundle;
import android.util.Log;

import com.buzzardparking.buzzard.activities.MainActivity;
import com.buzzardparking.buzzard.models.Place;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link PlaceManager} listens for the activity's saved instance state, to save and restore
 * any places on rotation.
 */
public class PlaceManager implements
        OnActivity.Listener,
        OnMap.Listener {

    private static final String KEY = "places";
    private final MarkerManager mMarkerManager;
    private ArrayList<Place> mPlaces; // May not need this. Need to know more about screen rotation
    private MainActivity context;

    public PlaceManager(MarkerManager markerManager, MainActivity context) {
        this.mMarkerManager = markerManager;
        this.context = context;
        mPlaces = new ArrayList<>();
    }

    public void addPlace(String title, LatLng latLng) {
        Place newPlace = new Place(title, latLng);
        newPlace.save();
        newPlace.saveParse();
        mPlaces.add(newPlace);

        mMarkerManager.addMarker(newPlace);

    }

    public void clearMap() {
        mMarkerManager.removeMarkers();
    }

    public void loadPlaces(GoogleMap map) {
        loadFromLocal(map);
        loadFromParse(map);
//        deleteFromParse();
    }

    public void loadFromLocal(GoogleMap map) {
        mPlaces.addAll(Place.getAll());
        mMarkerManager.addAll(mPlaces);
    }

    public void loadFromParse(final GoogleMap map) {
        ParseQuery query = new ParseQuery("Place");
        query.findInBackground(new FindCallback() {
            @Override
            public void done(Object places, Throwable throwable) {
                mPlaces.clear();
                mMarkerManager.removeMarkers();

                mPlaces.addAll(Place.fromParse(places));

                mMarkerManager.addAll(mPlaces);
            }

            @Override
            public void done(List objects, ParseException e) {
                Log.v("DEBUG", objects.toString());
            }
        });
    }

    public void deleteFromParse(){
        ParseQuery query = new ParseQuery("Place");
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

    @Override
    public void onStatus(OnActivity.Status status) {

    }

    @Override
    public void onSave(Bundle state) {
        state.putParcelable(KEY, Parcels.wrap(mPlaces));
    }

    @Override
    public void onRestore(Bundle state) {
        ArrayList<Place> places = Parcels.unwrap(state.getParcelable(KEY));
        if (places != null) {
            mPlaces = places;
        }
    }

    @Override
    public void onMap(GoogleMap map) {
        mMarkerManager.setUpClusterer(map, context);
    }

}