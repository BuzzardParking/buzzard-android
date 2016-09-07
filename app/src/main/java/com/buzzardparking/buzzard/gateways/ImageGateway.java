package com.buzzardparking.buzzard.gateways;

import com.buzzardparking.buzzard.R;
import com.buzzardparking.buzzard.activities.MapActivity;
import com.google.android.gms.maps.model.LatLng;


public class ImageGateway {
    public static String getPlaceImage(MapActivity context, LatLng latLng) {

        int height = 300;
        int width = 400;
        String url = "https://maps.googleapis.com/maps/api/streetview?size="
                + width + "x" + height + "&location=" +
                latLng.latitude + "," + latLng.longitude +
                " &key=" + context.getResources().getString(R.string.secret_streetView_api_key);
        return url;
    }
}
