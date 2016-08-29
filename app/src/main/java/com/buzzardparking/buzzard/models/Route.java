package com.buzzardparking.buzzard.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

// Routes are used to show direction lines on the map

public class Route {
    public Route() {
    }

    public ArrayList<Step> getSteps() {
        return steps;
    }

    public String getDuration() {
        return duration;
    }

    public String getDistance() {
        return distance;
    }

    private ArrayList<Step> steps;

    private String distance;

    private String duration;

    public Route(JSONObject jsonObject) {
        try {
            JSONArray jsonRoutes = jsonObject.getJSONArray("routes");
            //take the first route option returned by Google
            JSONObject jsonRoute = (JSONObject) jsonRoutes.get(0);
            JSONArray jsonLegs = jsonRoute.getJSONArray("legs");
            JSONObject jsonLeg = (JSONObject) jsonLegs.get(0);
            this.distance = jsonLeg.getJSONObject("distance").getString("text");
            this.duration = jsonLeg.getJSONObject("duration").getString("text");
            JSONArray jsonSteps = jsonLeg.getJSONArray("steps");
            this.steps = Step.fromJSONArray(jsonSteps);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("error", e.toString());
        }
    }
}