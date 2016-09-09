package com.buzzardparking.buzzard.gateways;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ReverseGeocodingGateway {


    public void FetchAddressInBackground(LatLng latLng, GetAddressCallback callback ) {
        new FetchAdressAsyncTask(latLng, callback).execute();
    }

    public class FetchAdressAsyncTask extends AsyncTask<Void, Void, JSONObject> {
        private String GEOCODINGKEY = "&key=AIzaSyAioPp8n-B9GiEaoIZTbTE0KwJms6_cFqI";
        private String REVERSE_GEOCODING_URL = "https://maps.googleapis.com/maps/api/geocode/json?latlng=";


        GetAddressCallback callback;
        LatLng latLng;

        public FetchAdressAsyncTask(LatLng latLng, GetAddressCallback callback) {
            this.latLng = latLng;
            this.callback = callback;
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            JSONObject jsonResults = new JSONObject();

            try {
                String mUrl = REVERSE_GEOCODING_URL + latLng.latitude + ","
                        + latLng.longitude + GEOCODINGKEY;

                URL url = new URL(mUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setDoInput(true);
                conn.setRequestMethod("GET");
                conn.connect();
                int mStatus = conn.getResponseCode();

                if (mStatus == 200) {
                    String stringResults = readResponse(conn.getInputStream()).toString();
                    jsonResults = new JSONObject(stringResults);
                }

                return jsonResults;

            } catch (Exception e) {
                e.printStackTrace();

            }
            return null;

        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
                callback.done(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public interface GetAddressCallback {
        void done(JSONObject jsonObject) throws JSONException;
    }

    private static StringBuilder readResponse(InputStream inputStream) throws IOException, NullPointerException {
        BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            stringBuilder.append(line);
        }
        return stringBuilder;
    }
}
