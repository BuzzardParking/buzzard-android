package com.buzzardparking.buzzard.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.parceler.Parcel;
import org.parceler.Transient;

import java.util.ArrayList;
import java.util.List;

/**
 * Static dynamicSpot uniquely represents a parking dynamicSpot, in regardless of who/when it is created.
 * It only respects its geo location and certain parking rules (e.g. street cleaning)
 *
 * Currently we only show dynamic dynamicSpot on the map, but in the future, we might leverage static
 * dynamicSpot as well.
 */
@Table(name = "Spots")
@Parcel(analyze={Spot.class})
public class Spot extends Model {

    @Column(name = "Latitude")
    public double latitude;

    @Column(name = "Longitude")
    public double longitude;

    @Column(name = "Timestamp")
    public String timestamp;

    @Transient
    public ParseObject parseSpot;

    public Spot(){ super();}

    public Spot(LatLng latLng) {
        this.latitude = latLng.latitude;
        this.longitude = latLng.longitude;
        this.timestamp = formatter().print(DateTime.now());

        this.parseSpot = new ParseObject("StaticSpot");
    }

    public Spot(ParseObject parsePlace) {
        this.parseSpot = parsePlace;
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

    public void saveParse(SaveCallback callback) {
        parseSpot.put("latitude", latitude);
        parseSpot.put("longitude", longitude);
        parseSpot.put("timestamp", timestamp);
        ParseGeoPoint point = new ParseGeoPoint(latitude, longitude);
        parseSpot.put("location", point);
        parseSpot.saveInBackground(callback);
    }

    public void saveParkedSpot(String userId) {
        parseSpot.put("userId", userId);
        parseSpot.put("latitude", latitude);
        parseSpot.put("longitude", longitude);
        parseSpot.put("timestamp", timestamp);
        ParseGeoPoint point = new ParseGeoPoint(latitude, longitude);
        parseSpot.put("location", point);
        parseSpot.saveInBackground();
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
}
