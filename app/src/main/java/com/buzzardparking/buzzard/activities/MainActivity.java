package com.buzzardparking.buzzard.activities;

import android.Manifest;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.buzzardparking.buzzard.R;
import com.buzzardparking.buzzard.interfaces.UIStateMachine;
import com.buzzardparking.buzzard.models.AppState;
import com.buzzardparking.buzzard.states.Looking;
import com.buzzardparking.buzzard.util.AddLocationLayer;
import com.buzzardparking.buzzard.util.AddMarkerOnLongClick;
import com.buzzardparking.buzzard.util.AddToMap;
import com.buzzardparking.buzzard.util.LogLocation;
import com.buzzardparking.buzzard.util.MoveToLocationFirstTime;
import com.buzzardparking.buzzard.util.OnActivity;
import com.buzzardparking.buzzard.util.OnClient;
import com.buzzardparking.buzzard.util.OnMap;
import com.buzzardparking.buzzard.util.OnPermission;
import com.buzzardparking.buzzard.util.PlaceManager;
import com.buzzardparking.buzzard.util.TrackLocation;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.maps.android.ui.IconGenerator;

public class MainActivity extends AppCompatActivity implements UIStateMachine {

    public static final String TAG = MainActivity.class.getSimpleName();

    public AppState appState;

    /* States */
    Looking lookingState;

    /* UI ELEMENTS */
    public Button mainButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            // Things here will happen on initial load
        }

        AddToMap adder = new AddToMap(getIconGenerator());
        PlaceManager manager = new PlaceManager(adder);
        AddMarkerOnLongClick click = new AddMarkerOnLongClick(this, manager);

        AddLocationLayer layer = new AddLocationLayer();
        MoveToLocationFirstTime move = new MoveToLocationFirstTime(savedInstanceState);
        TrackLocation track = new TrackLocation(getLocationRequest(), new LogLocation());

        new OnActivity.Builder(this, manager, track).build();

        FragmentManager fm = getSupportFragmentManager();
        SupportMapFragment fragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        if (fragment != null) {
            getMapAsync(fragment, new OnMap(manager, click, layer, move, track));
        }

        GoogleApiClient client = getGoogleApiClient();
        addConnectionCallbacks(client, new OnClient(client, move, track));

        int requestCode = 1001;
        String permission = Manifest.permission.ACCESS_FINE_LOCATION;
        OnPermission.Request location = new OnPermission.Request(requestCode, permission, layer, move, track);
        OnPermission onPermission = new OnPermission.Builder(this).build();
        onPermission.beginRequest(location);

        //// Get UI elements
        mainButton = (Button) findViewById(R.id.mainButton);

        /* Initialize States */
        appState = AppState.LOOKING; // This will be retrieved from DB
        lookingState = new Looking(this, manager);

        gotTo(appState);
    }

    // Set IconGenerator attributes.
    // Use the MarkerFont text appearance style.
    // Use it to build custom markers.
    private IconGenerator getIconGenerator() {
        IconGenerator generator = new IconGenerator(this);
        generator.setStyle(IconGenerator.STYLE_GREEN);
        generator.setTextAppearance(R.style.MarkerFont);
        return generator;
    }

    // Set priority, interval, and fastest interval.
    // Use it to start location updates.
    private LocationRequest getLocationRequest() {
        LocationRequest request = new LocationRequest();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(10000);        // 10 seconds
        request.setFastestInterval(5000);  // 5 seconds
        return request;
    }

    // Enable auto manage and add LocationServices API
    private GoogleApiClient getGoogleApiClient() {
        return new GoogleApiClient.Builder(this)
                .enableAutoManage(this, null)
                .addApi(LocationServices.API)
                .build();
    }

    private void getMapAsync(SupportMapFragment fragment, OnMapReadyCallback callback) {
        fragment.getMapAsync(callback);
    }

    private void addConnectionCallbacks(GoogleApiClient client, GoogleApiClient.ConnectionCallbacks callbacks) {
        client.registerConnectionCallbacks(callbacks);
    }

    @Override
    public void gotTo(AppState state) {
        switch (state) {
            case LOOKING:
                lookingState.start();
                break;
            default:
                break;
        }
    }
}