package com.buzzardparking.buzzard.util;

/**
 * Created by lee on 9/2/16.
 */
public final class Constants {
    public static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS = GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;
    public static final float GEOFENCE_RADIUS_IN_METERS = 160; // .1 mile, .16 km
}
