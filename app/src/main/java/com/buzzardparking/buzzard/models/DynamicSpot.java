package com.buzzardparking.buzzard.models;

import com.buzzardparking.buzzard.util.Utils;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.parceler.Parcel;
import org.parceler.Transient;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Dynamic Spot, which represents a one-time parking dynamicSpot.
 * It extends the basic properties from a Static Spot, e.g. lat, lng,
 * But also has its own one-time properties, such as
 * producer, consumer, createdAt, takenAt, duration, expiredAt, leftAt
 *
 * Its lifecycle starts from createdAt, and ends with expiredAt or LeftAt.
 * ExpiredAt is triggered by either a timeout(i.e. no one takes this dynamicSpot, and it's expired after
 * a certain period of time because it's most likely taken by some one who is not using the app).
 * LeftAt is triggered by a user who takes the dynamicSpot and tells us he/she is leaving.
 *
 * An expired parking dynamicSpot will just disappear from the map, while a left parking dynamicSpot will
 * automatically become another new dynamic dynamicSpot.
 */

@Parcel(analyze={DynamicSpot.class})
public class DynamicSpot implements ClusterItem {

    private static final long DEFAULT_EXPIRATION_DURATION_IN_MILL = 15 * 60 * 1000;
    private static final long DEFAULT_NEW_SPOT_REMAINING_IN_MILL = 5 * 60 * 1000;

    // Parceler requires that fields must be at least package public
    Date createdAt;
    Date lockedAt;
    Date takenAt;
    Date expiredAt;
    Date leftAt;

    public long durationInMill;
    User producer;
    User consumer;
    Spot staticSpot;

    @Transient
    public ParseFile snapshot;

    @Transient
    ParseObject parseSpot;

    // Default empty constructor required by the Parcel library
    public DynamicSpot() {

    }

    public DynamicSpot(LatLng latLng, User producer) {
        this.staticSpot = new Spot(latLng);
        this.producer = producer;
        this.createdAt = new Date();

        parseSpot = new ParseObject("DynamicSpot");
    }

    public DynamicSpot(Spot staticSpot, User producer) {
        this.staticSpot = staticSpot;
        this.producer = producer;
        this.createdAt = new Date();

        parseSpot = new ParseObject("DynamicSpot");
    }

    public DynamicSpot(ParseObject parseSpot) {
        this.parseSpot = parseSpot;

        this.staticSpot = new Spot(parseSpot.getParseObject("staticSpot"));
        this.createdAt = parseSpot.getCreatedAt();
        this.lockedAt = parseSpot.getDate("lockedAt");
        this.takenAt = parseSpot.getDate("takenAt");
        this.expiredAt = parseSpot.getDate("expiredAt");
        this.leftAt = parseSpot.getDate("leftAt");

        this.durationInMill = parseSpot.getLong("duration");
        this.producer = new User(parseSpot.getParseObject("producer"));

        ParseObject parseConsumer = parseSpot.getParseObject("consumer");
        if (parseConsumer != null) {
            this.consumer = new User(parseConsumer);
        }

        ParseFile snapshotFile = parseSpot.getParseFile("snapshot");
        if (snapshotFile != null) {
            this.snapshot = snapshotFile;
        }
    }

    public void saveParse() {
        // callback hell :(  but I don't have a better way to do this with current Parse implementation
        staticSpot.saveParse(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                producer.saveParse(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (consumer != null) {
                            consumer.saveParse(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    saveOnlySpot();
                               }
                            });
                        } else {
                            saveOnlySpot();
                        }
                   }
                });
            }
        });
    }

    private void saveOnlySpot() {
        parseSpot.put("staticSpot", staticSpot.parseSpot);
        parseSpot.put("producer", producer.parseUser);
        parseSpot.put("createdAt", createdAt);

        // replicate the location for easy query in Parse
        ParseGeoPoint point = new ParseGeoPoint(staticSpot.latitude, staticSpot.longitude);
        parseSpot.put("location", point);

        // Note: parse doesn't expect any null value
        if (lockedAt != null) {
            parseSpot.put("lockedAt", lockedAt);
        }

        if (takenAt != null) {
            parseSpot.put("takenAt", takenAt);
        }

       if (expiredAt != null) {
            parseSpot.put("expiredAt", expiredAt);
        }

        if (leftAt != null) {
            parseSpot.put("leftAt", leftAt);
        }

        if (consumer != null) {
            parseSpot.put("consumer", consumer.parseUser);
        }

        if (durationInMill > 0) {
            parseSpot.put("duration", durationInMill);
        }

        parseSpot.saveInBackground();
    }

    public static ArrayList<DynamicSpot> fromParseDynamicSpots(Object parseSpots) {
        ArrayList<ParseObject> spotsToConvert = (ArrayList<ParseObject>) parseSpots;
        ArrayList<DynamicSpot> spotsArr = new ArrayList<>();

        for (ParseObject parseSport: spotsToConvert) {
            spotsArr.add(new DynamicSpot(parseSport));
        }
        return spotsArr;
    }

    public void setDuration(int durationInMin) {
        this.durationInMill = durationInMin * 60 * 1000;
        saveParse();
    }

    public void unlock() {
        this.consumer = null;
        this.lockedAt = null;
        saveParse();
    }

    public void lockedBy(User consumer) {
        this.consumer = consumer;
        this.lockedAt = new Date();
        // have to explicitly set it to null for the back transition from PARKED to NAVIGATION
        // TODO: figure out a better way to do this
        this.takenAt = null;
        saveParse();
    }

    public void takenBy(User consumer) {
        this.consumer = consumer;
        this.takenAt = new Date();
        saveParse();
    }

    public String getTakenAtTimestamp() {
        if(takenAt == null) {
            return "";
        } else {
            return DateFormat.getDateTimeInstance().format(takenAt);
        }
    }

    /**
     * Leave a dynamicSpot, which means the current dynamic dynamicSpot is dead, and
     * a new dynamic dynamicSpot is reported (i.e. created) by the current consumer.
     * @return  a new {@link DynamicSpot}
     */
    public DynamicSpot leaveSpot() {
        leftAt = new Date();
        DynamicSpot newSpot = new DynamicSpot(staticSpot, consumer);

        saveParse();
        newSpot.saveParse();
        return newSpot;
    }

    public boolean isExpired() {
        long currentTime = (new Date()).getTime();
        return !isLocked()
                && !isTaken()
                && currentTime - createdAt.getTime() > DEFAULT_EXPIRATION_DURATION_IN_MILL;
    }

    public void expireSpot() {
        this.expiredAt = new Date();
        saveParse();
    }

    public long timeRemaining() {
        long currentTime = (new Date()).getTime();
        if (isTaken()) {
            if (durationInMill > 0) {
                return durationInMill - (currentTime - takenAt.getTime());
            } else {
                return Long.MAX_VALUE;
            }
        } else {
            return DEFAULT_EXPIRATION_DURATION_IN_MILL - (currentTime - createdAt.getTime());
        }
    }

    public boolean isNew() {
        return timeRemaining() > DEFAULT_NEW_SPOT_REMAINING_IN_MILL;
    }

    public boolean isSpotAvailable() {
        return timeRemaining() > 0 && takenAt == null;
    }

    public LatLng getLatLng() {
        return this.staticSpot.getLatLng();
    }

    public String getReporterFirstName() {
        return producer.getName().split(" ")[0];
    }

    public String getCreatedAtTimestamp() {
        return Utils.getRelativeTimeAgo(String.valueOf(createdAt));
    }

    public Spot getStaticSpot() {
        return staticSpot;
    }

    private boolean isTaken() {
        return takenAt != null;
    }

    private boolean isLocked() {
        return lockedAt != null;
    }

    @Override
    public LatLng getPosition() {
        return staticSpot.getLatLng();
    }

    public static void linkSnapshot(DynamicSpot dynamicSpot, ParseFile screenshotFile) {
        dynamicSpot.parseSpot.put("snapshot", screenshotFile);
        dynamicSpot.parseSpot.saveInBackground();
    }

    public static DynamicSpot loadLockedSpot(User user) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("DynamicSpot");
        try {
            // any time, there could only exist one locked spot for a particular user
            ParseObject parseSpot = query
                    .include("staticSpot")
                    .include("producer")
                    .include("consumer")
                    .whereNotEqualTo("lockedAt", null)
                    .whereEqualTo("takenAt", null)
                    .whereEqualTo("consumer", user.parseUser)
                    .getFirst();
            return new DynamicSpot(parseSpot);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static DynamicSpot loadTakenSpot(User user) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("DynamicSpot");
        try {
            ParseObject parseSpot = query
                    .include("staticSpot")
                    .include("producer")
                    .include("consumer")
                    .whereNotEqualTo("takenAt", null)
                    .whereEqualTo("consumer", user.parseUser)
                    .orderByDescending("takenAt")
                    .getFirst();
            return new DynamicSpot(parseSpot);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
