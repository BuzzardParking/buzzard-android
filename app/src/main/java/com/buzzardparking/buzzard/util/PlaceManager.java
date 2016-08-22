package com.buzzardparking.buzzard.util;

import android.os.Bundle;
import android.util.Log;

import com.buzzardparking.buzzard.models.Place;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.ParseException;
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
    private ArrayList<Place> mPlaces;

    public PlaceManager(MarkerManager markerManager) {
        this.mMarkerManager = markerManager;
        mPlaces = new ArrayList<>();
    }

    public void addPlace(GoogleMap map, String title, LatLng latLng) {
        Place newPlace = new Place(title, latLng);
        newPlace.save();
        newPlace.saveParse();
        mPlaces.add(newPlace);
        mMarkerManager.addMarker(map, title, latLng, true);
    }

    public void clearPlaces() {
        mMarkerManager.removeMarkers();
    }

    public void loadPlaces(GoogleMap map) {
        loadFromLocal(map);
        loadFromParse(map);
    }

    public void loadFromLocal(GoogleMap map) {
        mPlaces.addAll(Place.getAll());
        placesToMarkers(mPlaces, map);
    }

    private void placesToMarkers(ArrayList<Place> places, GoogleMap map) {
        for (Place place: places) {
            mMarkerManager.addMarker(map, place.getTitle(), place.getLatLng(), false);
        }
    }

    public void loadFromParse(final GoogleMap map) {
        ParseQuery query = new ParseQuery("Place");
        query.findInBackground(new FindCallback() {
            @Override
            public void done(Object places, Throwable throwable) {
                mPlaces.clear();
                clearPlaces();
                mPlaces.addAll(Place.fromParse(places));
                placesToMarkers(mPlaces, map);
            }

            @Override
            public void done(List objects, ParseException e) {
                Log.v("DEBUG", objects.toString());
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
        for (Place place : mPlaces) {
            mMarkerManager.addMarker(map, place.getTitle(), place.getLatLng(), false);
        }
    }

}