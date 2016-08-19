package com.buzzardparking.buzzard.models;

import com.google.android.gms.maps.model.LatLng;

import org.parceler.Parcel;

/*
 * Used to build markers.
 */
@Parcel
public class Place {

    public String title;

    public double lat;

    public double lng;

    public Place(){}

    public Place(String title, LatLng latLng) {
        this.title = title;
        this.lat = latLng.latitude;
        this.lng = latLng.longitude;
    }

    public String getTitle() {
        return title;
    }

    public LatLng getLatLng() {
        return new LatLng(lat, lng);
    }

}