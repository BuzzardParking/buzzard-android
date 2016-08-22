package com.buzzardparking.buzzard.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lee on 8/21/16.
 */
public class Step {
    private String instructions;
    private Double startLat;
    private Double startLng;
    private Double endLng;
    private Double endLat;

    public Double getStartLat() {
        return startLat;
    }

    public String getInstructions() {
        return instructions;
    }

    public Double getEndLat() {
        return endLat;
    }

    public Double getEndLng() {
        return endLng;
    }

    public Double getStartLng() {
        return startLng;
    }

    public Step(JSONObject jsonStep) {
        try {
            this.instructions = jsonStep.getString("html_instructions");
            JSONObject startLocation = jsonStep.getJSONObject("start_location");
            this.startLng = startLocation.getDouble("lng");
            this.startLat = startLocation.getDouble("lat");
            JSONObject endLocation = jsonStep.getJSONObject("end_location");
            this.endLng = endLocation.getDouble("lng");
            this.endLat = endLocation.getDouble("lat");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Step> fromJSONArray(JSONArray array) {
        ArrayList<Step> steps = new ArrayList<>();

        for (int x = 0; x < array.length(); x++) {
            try {
                steps.add(new Step(array.getJSONObject(x)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return steps;
    }
}
