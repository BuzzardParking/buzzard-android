package com.buzzardparking.buzzard.activities;

import android.Manifest;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.buzzardparking.buzzard.R;
import com.buzzardparking.buzzard.interfaces.UIStateMachine;
import com.buzzardparking.buzzard.models.AppState;
import com.buzzardparking.buzzard.models.Map;
import com.buzzardparking.buzzard.states.LeavingState;
import com.buzzardparking.buzzard.states.LookingState;
import com.buzzardparking.buzzard.states.NavigatingState;
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
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.maps.android.ui.IconGenerator;

public class MainActivity extends AppCompatActivity implements UIStateMachine {

    public static final String TAG = MainActivity.class.getSimpleName();

    // Singleton instance of map
    public Map buzzardMap;

    // states
    private UserState currentState;

    private PlaceManager placeManager;

    /* UI ELEMENTS */
    public Button actionButton;
    private DrawerLayout mDrawer;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;
    private Toolbar toolbar;

    public BottomSheetLayout bottomSheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupToolbar();
        setupDrawer();

        actionButton = (Button) findViewById(R.id.btn_action);
        bottomSheet = (BottomSheetLayout) findViewById(R.id.bottomsheet);

        if (savedInstanceState == null) {
            Toast.makeText(this, "Long tap on map to report parking space", Toast.LENGTH_LONG).show();
        }

        MarkerManager markerManager = new MarkerManager(getIconGenerator()); // Icongenerator currently not being used
        placeManager = new PlaceManager(markerManager, this);

        AddMarkerOnLongClick click = new AddMarkerOnLongClick(this, placeManager);
        AddLocationLayer layer = new AddLocationLayer();
        MoveToLocationFirstTime move = new MoveToLocationFirstTime(savedInstanceState);
        TrackLocation track = new TrackLocation(getLocationRequest(), new LogLocation());

        new OnActivity.Builder(this, placeManager, track).build();

        // save the map reference
        buzzardMap = new Map();

        // TODO: retrieve from DB or backend in the future
        goTo(AppState.LOOKING);

        // initialize the map system and view
        FragmentManager fm = getSupportFragmentManager();
        SupportMapFragment fragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        if (fragment != null) {
            fragment.getMapAsync(new OnMap(buzzardMap, placeManager, click, layer, move, track, currentState));
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

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(R.string.toolbarTitle);
    }

    private void setupDrawer() {
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();
        mDrawer.addDrawerListener(drawerToggle);
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

    public GoogleMap getMap() {
        return buzzardMap.get();
    }

    @Override
    public void goTo(AppState state) {
        if (currentState != null) {
            currentState.stop();
        }

        switch (state) {
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }
}