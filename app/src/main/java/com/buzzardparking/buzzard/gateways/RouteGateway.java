package com.buzzardparking.buzzard.gateways;

import android.util.Log;

import com.buzzardparking.buzzard.models.Route;
import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * {@link RouteGateway} is a gateway that talks to Google APIs to fetch route information.
 */
public class RouteGateway {

    public static void getRoute(LatLng start, LatLng end, final RouteGatewayListener routeGateWayListener) {
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
                super.onSuccess(statusCode, headers, response);
                Route route = new Route(response);

                routeGateWayListener.onReturn(route);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("error", String.valueOf(errorResponse));
            }
        });

    }

    public interface RouteGatewayListener {
        void onReturn(Route returnedRoute);
    }

}
