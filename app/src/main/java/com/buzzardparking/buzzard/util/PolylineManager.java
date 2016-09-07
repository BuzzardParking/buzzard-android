package com.buzzardparking.buzzard.util;

import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.buzzardparking.buzzard.R;
import com.buzzardparking.buzzard.activities.MapActivity;
import com.buzzardparking.buzzard.models.Route;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by lee on 8/27/16.
 */
public class PolylineManager {
    private ArrayList<Polyline> polylines = new ArrayList<>();
    private GoogleMap mGoogleMap;
    private MapActivity context;

    public PolylineManager(MapActivity context) {
        this.context = context;
    }

    public void remove() {
        for (Polyline line : polylines) {
            line.remove();
        }
    }

    public void createAndDisplay(GoogleMap map, LatLng start, LatLng end) {
        mGoogleMap = map;
        String baseUrl = "https://maps.googleapis.com/maps/api/directions/json";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        String directions = "AIzaSyCZhPIIOMQjjZYMpOzujg5mS8qlO5u4T2Q";

        params.put("key", directions);
        params.put("origin", start.latitude + "," + start.longitude);
        params.put("destination", end.latitude + "," + end.longitude);
        client.get(baseUrl, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Route route = new Route(response);
                displayOnMap(route);
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("error", String.valueOf(errorResponse));
            }
        });
    }

    private void notifyUser(Route route) {
        // TODO: add nice animation here
        String message = "You are " + route.getDuration() + " away.";
        context.tvBottomSheetHeading.setText(message);
        context.tvBottomSheetSubheadingRight.setText(route.getDistance());
        context.hideProgressBar();
    }

    public void displayOnMap(Route route) {
        ArrayList<LatLng> points = new ArrayList<>();
        PolylineOptions rectOptions;
        Polyline polyline;
        rectOptions = new PolylineOptions();
        int lineColor = ContextCompat.getColor(context, R.color.nav_line_color);
        rectOptions.color(lineColor);
        rectOptions.width(12);


        for (int i = 0; i < route.getSteps().size(); i++) {
            LatLng start = new LatLng(route.getSteps().get(i).getStartLat(), route.getSteps().get(i).getStartLng());
            LatLng end = new LatLng(route.getSteps().get(i).getEndLat(), route.getSteps().get(i).getEndLng());
            points.add(start);
            points.add(end);
        }

        rectOptions.addAll(points);
        polyline = mGoogleMap.addPolyline(rectOptions);
        polylines.add(polyline);
        notifyUser(route);
    }

}
