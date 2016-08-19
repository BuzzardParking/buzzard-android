package com.buzzardparking.buzzard.util;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

// TODO Understand how PlaceManager works
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
        mPlaces.add(new Place(title, latLng));
        mAdder.addTo(map, title, latLng, true);
    }

    public void clearPlaces() {
        mAdder.removeMarkers();
    }

    @Override
    public void onStatus(OnActivity.Status status) {

    }

    @Override
    public void onSave(Bundle state) {
        state.putParcelableArrayList(KEY, mPlaces);
    }

    @Override
    public void onRestore(Bundle state) {
        ArrayList<Place> places = state.getParcelableArrayList(KEY);
        if (places != null) {
            mPlaces = places;
        }
    }

    @Override
    public void onMap(GoogleMap map) {
        for (Place place : mPlaces) {
            mAdder.addTo(map, place.mTitle, place.mLatLng, false);
        }
    }

    private static class Place implements Parcelable {

        private final String mTitle;
        private final LatLng mLatLng;

        private Place(String title, LatLng latLng) {
            mTitle = title;
            mLatLng = latLng;
        }

        private Place(Parcel in) {
            mTitle = in.readString();
            mLatLng = in.readParcelable(LatLng.class.getClassLoader());
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(mTitle);
            dest.writeParcelable(mLatLng, flags);
        }

        public static final Creator<Place> CREATOR =
                new Creator<Place>() {
                    @Override
                    public Place createFromParcel(Parcel in) {
                        return new Place(in);
                    }

                    @Override
                    public Place[] newArray(int size) {
                        return new Place[size];
                    }
                };

    }
}