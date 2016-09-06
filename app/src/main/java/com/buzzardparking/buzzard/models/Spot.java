package com.buzzardparking.buzzard.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/*
 * Used to build markers.
 */
@Table(name = "Spots")
@Parcel(analyze={Spot.class})
public class Spot extends Model implements ClusterItem {

    @Column(name = "Latitude")
    public double latitude;

    @Column(name = "Longitude")
    public double longitude;

    @Column(name = "Timestamp")
    public String timestamp;

    public Spot(){ super();}

    public Spot(LatLng latLng) {
        this.latitude = latLng.latitude;
        this.longitude = latLng.longitude;
        this.timestamp = formatter().print(DateTime.now());
    }

    public Spot(ParseObject parsePlace) {
        this.longitude = parsePlace.getDouble("longitude");
        this.latitude = parsePlace.getDouble("latitude");
        this.timestamp = parsePlace.getString("timestamp");
    }


    public LatLng getLatLng() {
        return new LatLng(latitude, longitude);
    }

    public static List<Spot> getAll() {
        return new Select()
                .from(Spot.class)
                .execute();
    }

    public void saveParse() {
        ParseObject place = new ParseObject("Spot");
        place.put("latitude", latitude);
        place.put("longitude", longitude);
        place.put("timestamp", timestamp);
        ParseGeoPoint point = new ParseGeoPoint(latitude, longitude);
        place.put("location", point);
        place.saveInBackground();
    }

    public void saveParkedSpot(String userId) {
        ParseObject place = new ParseObject("Spot");
        place.put("userId", userId);
        place.put("latitude", latitude);
        place.put("longitude", longitude);
        place.put("timestamp", timestamp);
        ParseGeoPoint point = new ParseGeoPoint(latitude, longitude);
        place.put("location", point);
        place.saveInBackground();
    }

    public String getTimestampStr() {
        return timestamp;
    }

    public long getAgeInMinutes() {
        DateTime d1 = formatter().parseDateTime(timestamp);
        DateTime d2 = DateTime.now();

        int diffMinutes = Minutes.minutesBetween(d1, d2).getMinutes();

        return diffMinutes;
    }

    public static ArrayList<Spot> fromParse(Object parsePlaces) {
        ArrayList<ParseObject> placesToConvert = (ArrayList<ParseObject>) parsePlaces;
        ArrayList<Spot> placesArr = new ArrayList<>();

        for (ParseObject place: placesToConvert) {
            placesArr.add(new Spot(place));
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
