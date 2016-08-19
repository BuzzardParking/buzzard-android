package com.buzzardparking.buzzard.util;

import android.os.Bundle;

import com.buzzardparking.buzzard.models.Place;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import org.parceler.Parcels;

import java.util.ArrayList;

// PlaceManager saves marker state on device rotation.
public class PlaceManager implements
        OnActivity.Listener,
        OnMap.Listener {

    private static final String KEY = "places";

    private final AddToMap mAdder;

    private ArrayList<Place> mPlaces;

    public PlaceManager(AddToMap adder) {
        mAdder = adder;
        mPlaces = new ArrayList<>();
    }

    public void addPlace(GoogleMap map, String title, LatLng latLng) {
        Place newPlace = new Place(title, latLng);
        newPlace.save();
        mPlaces.add(newPlace);
        mAdder.addTo(map, title, latLng, true);
    }


    /*  Removes markers from UI.
        This will be leveraged to swap between different views
     */
    public void clearPlaces() {
        mAdder.removeMarkers();
    }

//    public void loadPlaces() {
//        mPlaces.addAll(Place.getAll());
//        for (Place place: mPlaces) {
//            mAdder.addTo(map, place.getTitle(), place.getLatLng(), false);
//        }
//    }

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
            mAdder.addTo(map, place.getTitle(), place.getLatLng(), false);
        }
    }

}