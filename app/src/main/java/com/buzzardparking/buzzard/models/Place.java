package com.buzzardparking.buzzard.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.android.gms.maps.model.LatLng;

import org.parceler.Parcel;

import java.util.List;

/*
 * Used to build markers.
 */
@Table(name = "Places")
@Parcel(analyze={Place.class})
public class Place extends Model{

    @Column(name = "Title")
    public String title;

    @Column(name = "Latitude")
    public double latitude;

    @Column(name = "Longitude")
    public double longitude;

    public Place(){ super();}

    public Place(String title, LatLng latLng) {
        this.title = title;
        this.latitude = latLng.latitude;
        this.longitude = latLng.longitude;
    }

    public String getTitle() {
        return title;
    }

    public LatLng getLatLng() {
        return new LatLng(latitude, longitude);
    }

    public static List<Place> getAll() {
        return new Select()
                .from(Place.class)
                .execute();
    }

}