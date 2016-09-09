package com.buzzardparking.buzzard.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.buzzardparking.buzzard.gateways.ReverseGeocodingGateway;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;
import org.parceler.Transient;

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

    @Column(name = "Address")
    public String address;

    @Column(name = "City")
    public String city;

    @Column(name = "State")
    public String state;

    @Transient
    public ParseObject parseSpot;

    @Transient
    private final String default_address = "User Reported Space";
    @Transient
    private final String default_city = "Right Here!";
    @Transient
    private final String default_state = "USA";

    public Spot(){ super();}

    public Spot(LatLng latLng) {
        this.latitude = latLng.latitude;
        this.longitude = latLng.longitude;
        this.timestamp = formatter().print(DateTime.now());
        this.parseSpot = new ParseObject("StaticSpot");
        this.address = default_address;
        this.city = default_city;
        this.state = default_state;
        addAddressAsync();
    }

    public void addAddressAsync() {
        final Spot currentSpot = this;
        ReverseGeocodingGateway geocodingGateway = new ReverseGeocodingGateway();
        geocodingGateway.FetchAddressInBackground(this.getLatLng(), new ReverseGeocodingGateway.GetAddressCallback() {

            @Override
            public void done(JSONObject jsonObject) throws JSONException {
                try {
                    JSONArray addressComponents = jsonObject.getJSONArray("results").getJSONObject(0).getJSONArray("address_components");

                    String streetNum = addressComponents.getJSONObject(0).getString("long_name");
                    String streetName = addressComponents.getJSONObject(1).getString("short_name");
                    String city = addressComponents.getJSONObject(3).getString("long_name");
                    String state = addressComponents.getJSONObject(5).getString("short_name");

                    String address = streetNum + " " + streetName;

                    currentSpot.address = address;
                    currentSpot.city = city;
                    currentSpot.state = state;

                    if ((streetName == "null") || (streetName == null) || (streetName == "")) {
                        currentSpot.address = default_address;
                    }

                    if ((city == "null") || (city == null) || (city == "")) {
                        currentSpot.city = default_city;
                        currentSpot.state = default_state;
                    }

                    parseSpot.put("address", address);
                    parseSpot.put("city", city);
                    parseSpot.put("state", state);
                    parseSpot.saveInBackground();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public Spot(ParseObject parsePlace) {
        this.parseSpot = parsePlace;
        this.longitude = parsePlace.getDouble("longitude");
        this.latitude = parsePlace.getDouble("latitude");
        this.timestamp = parsePlace.getString("timestamp");
        this.address = parsePlace.getString("address");
        this.city = parsePlace.getString("city");
        this.state = parsePlace.getString("state");
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
        parseSpot.put("address", address);
        parseSpot.put("city", city);
        parseSpot.put("state", state);
        ParseGeoPoint point = new ParseGeoPoint(latitude, longitude);
        parseSpot.put("location", point);
        parseSpot.saveInBackground(callback);
    }

    public static String getImageUrl(LatLng latLng) {
        int height = 300;
        int width = 400;
        String url = "https://maps.googleapis.com/maps/api/streetview?size="
                + width + "x" + height + "&location=" +
                latLng.latitude + "," + latLng.longitude +
                " &key=" + "AIzaSyAni2Vr0DPzCNu6YDE4_AFP2ZVZSxBx_us";
        return url;
    }


    public long getAgeInMinutes() {
        DateTime d1 = formatter().parseDateTime(timestamp);
        DateTime d2 = DateTime.now();

        int diffMinutes = Minutes.minutesBetween(d1, d2).getMinutes();

        return diffMinutes;
    }

    private DateTimeFormatter formatter() {
        return DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    }
}
