package com.buzzardparking.buzzard.models;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by lee on 8/21/16.
 */
public class Route {
    public Route() {
    }

    public ArrayList<Step> getSteps() {
        return steps;
    }

    private ArrayList<Step> steps;

    public Route(JSONObject jsonObject) {
        try {
            JSONArray jsonRoutes = jsonObject.getJSONArray("routes");
            //take the first route option returned by Google
            JSONObject jsonRoute = (JSONObject) jsonRoutes.get(0);
            JSONArray jsonLegs = jsonRoute.getJSONArray("legs");
            JSONObject jsonLeg = (JSONObject) jsonLegs.get(0);
            JSONArray jsonSteps = jsonLeg.getJSONArray("steps");
            this.steps = Step.fromJSONArray(jsonSteps);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void createAndDisplayOnMap(final GoogleMap googleMap, Place start, Place end) {
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
                route.displayOnMap(googleMap);
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    public void displayOnMap(GoogleMap googleMap) {
        PolylineOptions rectOptions = new PolylineOptions();

        for (int i = 0; i < getSteps().size(); i++) {
            rectOptions.add(new LatLng(getSteps().get(i).getStartLat(), getSteps().get(i).getStartLng()));
        }

        // Get back the mutable Polyline
        Polyline polyline = googleMap.addPolyline(rectOptions);
    }
}