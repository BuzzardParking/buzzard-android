package com.buzzardparking.buzzard.util;

import android.os.Bundle;

import com.buzzardparking.buzzard.models.Place;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import org.parceler.Parcels;

import java.util.ArrayList;

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
        mPlaces.add(newPlace);
        mMarkerManager.addMarker(map, title, latLng, true);
    }

    public void clearPlaces() {
        mMarkerManager.removeMarkers();
    }

    /*
        Loads from active record.
        TODO: load from API as well
    */
    public void loadPlaces(GoogleMap map) {
        mPlaces.addAll(Place.getAll());
        for (Place place: mPlaces) {
            mMarkerManager.addMarker(map, place.getTitle(), place.getLatLng(), false);
        }
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