package com.buzzardparking.buzzard.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.parse.ParseObject;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/*
 * Used to build markers.
 */
@Table(name = "Places")
@Parcel(analyze={Place.class})
public class Place extends Model implements ClusterItem {

    @Column(name = "Latitude")
    public double latitude;

    @Column(name = "Longitude")
    public double longitude;

    @Column(name = "Timestamp")
    public String timestamp;

    public Place(){ super();}

    public Place(LatLng latLng) {
        this.latitude = latLng.latitude;
        this.longitude = latLng.longitude;
        this.timestamp = formatter().print(DateTime.now());
    }

    public Place(ParseObject parsePlace) {
        this.longitude = parsePlace.getDouble("longitude");
        this.latitude = parsePlace.getDouble("latitude");
        this.timestamp = parsePlace.getString("timestamp");
    }


    public LatLng getLatLng() {
        return new LatLng(latitude, longitude);
    }

    public static List<Place> getAll() {
        return new Select()
                .from(Place.class)
                .execute();
    }

    public void saveParse() {
        ParseObject place = new ParseObject("Place");
        place.put("latitude", latitude);
        place.put("longitude", longitude);
        place.put("timestamp", timestamp);
        place.saveInBackground();
    }

    public long getAgeInMinutes() {
        DateTime d1 = formatter().parseDateTime(timestamp);
        DateTime d2 = DateTime.now();
        long diffInMillis = d2.getMillis() - d1.getMillis();
        long diffMinutes = diffInMillis / (60 * 1000) % 60;
        return diffMinutes;
    }

    public static ArrayList<Place> fromParse(Object parsePlaces) {
        ArrayList<ParseObject> placesToConvert = (ArrayList<ParseObject>) parsePlaces;
        ArrayList<Place> placesArr = new ArrayList<>();

        for (ParseObject place: placesToConvert) {
            placesArr.add(new Place(place));
        }
        return placesArr;
    }

    private DateTimeFormatter formatter() {
        return DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    }

    @Override
    public LatLng getPosition() {
        return getLatLng();
    }
}