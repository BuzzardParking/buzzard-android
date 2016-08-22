package com.buzzardparking.buzzard.activities;

import android.Manifest;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Toast;

import com.buzzardparking.buzzard.R;
import com.buzzardparking.buzzard.interfaces.UIStateMachine;
import com.buzzardparking.buzzard.models.AppState;
import com.buzzardparking.buzzard.states.LeavingState;
import com.buzzardparking.buzzard.states.LookingState;
import com.buzzardparking.buzzard.states.NavigatingState;
import com.buzzardparking.buzzard.states.OverviewState;
import com.buzzardparking.buzzard.states.ParkedState;
import com.buzzardparking.buzzard.states.UserState;
import com.buzzardparking.buzzard.util.AddLocationLayer;
import com.buzzardparking.buzzard.util.AddMarkerOnLongClick;
import com.buzzardparking.buzzard.util.LogLocation;
import com.buzzardparking.buzzard.util.MarkerManager;
import com.buzzardparking.buzzard.util.MoveToLocationFirstTime;
import com.buzzardparking.buzzard.util.OnActivity;
import com.buzzardparking.buzzard.util.OnClient;
import com.buzzardparking.buzzard.util.OnMap;
import com.buzzardparking.buzzard.util.OnPermission;
import com.buzzardparking.buzzard.util.PlaceManager;
import com.buzzardparking.buzzard.util.TrackLocation;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.maps.android.ui.IconGenerator;

public class MainActivity extends AppCompatActivity implements UIStateMachine {

    public static final String TAG = MainActivity.class.getSimpleName();

    // states
    private UserState currentState;
    private PlaceManager placeManager;

    /* UI ELEMENTS */
    public Button actionButton;
    public BottomSheetLayout bottomSheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionButton = (Button) findViewById(R.id.btn_action);
        bottomSheet = (BottomSheetLayout) findViewById(R.id.bottomsheet);


        if (savedInstanceState == null) {
            Toast.makeText(this, "Long tap on map to report parking space", Toast.LENGTH_LONG).show();
        }

        MarkerManager markerManager = new MarkerManager(getIconGenerator());
        placeManager = new PlaceManager(markerManager);

        AddMarkerOnLongClick click = new AddMarkerOnLongClick(this, placeManager);
        AddLocationLayer layer = new AddLocationLayer();
        MoveToLocationFirstTime move = new MoveToLocationFirstTime(savedInstanceState);
        TrackLocation track = new TrackLocation(getLocationRequest(), new LogLocation());

        new OnActivity.Builder(this, placeManager, track).build();

        // TODO: retrieve from DB or backend in the future
        goTo(AppState.OVERVIEW);

        // initialize the map system and view
        FragmentManager fm = getSupportFragmentManager();
        SupportMapFragment fragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        if (fragment != null) {
            fragment.getMapAsync(new OnMap(placeManager, click, layer, move, track, currentState));
        }

        // connect the google client
        GoogleApiClient client = getGoogleApiClient();
        client.registerConnectionCallbacks(new OnClient(client, move, track));

        // request permissions about current location
        int requestCode = 1001;
        String permission = Manifest.permission.ACCESS_FINE_LOCATION;
        OnPermission.Request location = new OnPermission.Request(requestCode, permission, layer, move, track);
        OnPermission onPermission = new OnPermission.Builder(this).build();
        onPermission.beginRequest(location);
    }

    private IconGenerator getIconGenerator() {
        IconGenerator generator = new IconGenerator(this);
        generator.setStyle(IconGenerator.STYLE_GREEN);
        generator.setTextAppearance(R.style.MarkerFont);
        return generator;
    }

    // Set priority, interval, and fastest interval of location updates
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

    @Override
    public void goTo(AppState state) {
        if (currentState != null) {
            currentState.stop();
        }

        switch (state) {
           case OVERVIEW:
                currentState = new OverviewState(this, placeManager);
                break;
            case LOOKING:
                currentState = new LookingState(this, placeManager);
                break;
            case NAVIGATING:
                currentState = new NavigatingState(this, placeManager);
                break;
            case PARKED:
                currentState = new ParkedState(this, placeManager);
                break;
            case LEAVING:
                currentState = new LeavingState(this, placeManager);
                break;
            default:
                break;
        }

        currentState.start();
    }
}