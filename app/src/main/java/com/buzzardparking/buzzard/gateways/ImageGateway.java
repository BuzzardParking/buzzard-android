package com.buzzardparking.buzzard.gateways;

import com.google.android.gms.maps.model.LatLng;


public class ImageGateway {
    public static String getPlaceImage(LatLng latLng) {

        int height = 300;
        int width = 400;
        String url = "https://maps.googleapis.com/maps/api/streetview?size="
                + width + "x" + height + "&location=" +
                latLng.latitude + "," + latLng.longitude +
                " &key=" + "AIzaSyAni2Vr0DPzCNu6YDE4_AFP2ZVZSxBx_us";
        return url;
    }
}
