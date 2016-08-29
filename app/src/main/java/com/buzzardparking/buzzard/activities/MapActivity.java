package com.buzzardparking.buzzard.activities;

import android.Manifest;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.buzzardparking.buzzard.R;
import com.buzzardparking.buzzard.interfaces.UIStateMachine;
import com.buzzardparking.buzzard.models.AppState;
import com.buzzardparking.buzzard.models.Map;
import com.buzzardparking.buzzard.models.Spot;
import com.buzzardparking.buzzard.services.OverlayService;
import com.buzzardparking.buzzard.states.LeavingState;
import com.buzzardparking.buzzard.states.LookingState;
import com.buzzardparking.buzzard.states.NavigatingState;
import com.buzzardparking.buzzard.states.ParkedState;
import com.buzzardparking.buzzard.states.UserState;
import com.buzzardparking.buzzard.util.AddLocationLayer;
import com.buzzardparking.buzzard.util.AddMarkerOnLongClick;
import com.buzzardparking.buzzard.util.BottomSheetManager;
import com.buzzardparking.buzzard.util.CameraManager;
import com.buzzardparking.buzzard.util.LogLocation;
import com.buzzardparking.buzzard.util.MarkerManager;
import com.buzzardparking.buzzard.util.OnActivity;
import com.buzzardparking.buzzard.util.OnClient;
import com.buzzardparking.buzzard.util.OnMap;
import com.buzzardparking.buzzard.util.OnPermission;
import com.buzzardparking.buzzard.util.PlaceManager;
import com.buzzardparking.buzzard.util.TrackLocation;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.maps.android.ui.IconGenerator;

public class MapActivity extends AppCompatActivity implements UIStateMachine {

    public static final String TAG = MapActivity.class.getSimpleName();
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final int OVERLAY_REQUEST_CODE = 99;

    // Singleton instance of map
    public Map buzzardMap;

    // states
    private UserState currentState;

    private PlaceManager placeManager;

    CameraManager cameraManager;

    /* UI ELEMENTS */
    private DrawerLayout mDrawer;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;
    private Toolbar toolbar;

    public TextView tvBottomSheetHeading;
    public TextView tvBottomSheetSubHeading;
    public TextView tvBottomSheetSubheadingRight;

    public BottomSheetBehavior bottomSheet;

    public Place googlePlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        setupToolbar();
        setupDrawer();

        bottomSheet = BottomSheetBehavior.from(findViewById(R.id.rlBottomSheet));

        tvBottomSheetHeading = (TextView) findViewById(R.id.tvBottomSheetHeading);
        tvBottomSheetSubHeading = (TextView) findViewById(R.id.tvBottomSheetSubheading);
        tvBottomSheetSubheadingRight = (TextView) findViewById(R.id.tvBottomSheetSubheadingRight);
        new BottomSheetManager(this, bottomSheet);

        if (savedInstanceState == null) {
            Toast.makeText(this, "Long tap on map to report parking space", Toast.LENGTH_LONG).show();
        }

        MarkerManager markerManager = new MarkerManager(getIconGenerator()); // Icongenerator currently not being used
        placeManager = new PlaceManager(markerManager, this);

        AddMarkerOnLongClick click = new AddMarkerOnLongClick(this, placeManager);
        AddLocationLayer layer = new AddLocationLayer();
        cameraManager = new CameraManager(savedInstanceState);
        TrackLocation track = new TrackLocation(getLocationRequest(), new LogLocation());

        new OnActivity.Builder(this, track).build();

        // save the map reference
        buzzardMap = new Map();

        // TODO: retrieve from DB or backend in the future
        goTo(AppState.LOOKING);

        // initialize the map system and view
        FragmentManager fm = getSupportFragmentManager();
        SupportMapFragment fragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        if (fragment != null) {
            fragment.getMapAsync(new OnMap(buzzardMap, placeManager, click, layer, cameraManager, track, currentState));
        }

        // connect the google client
        GoogleApiClient client = getGoogleApiClient();
        client.registerConnectionCallbacks(new OnClient(client, cameraManager, track));

        // request permissions about current location
        int requestCode = 1001;
        String permission = Manifest.permission.ACCESS_FINE_LOCATION;
        OnPermission.Request location = new OnPermission.Request(requestCode, permission, layer, cameraManager, track);
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
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, null)
                .addApi(LocationServices.API)
                .build();
    }

    public GoogleMap getMap() {
        return buzzardMap.get();
    }

    public BottomSheetBehavior getBottomSheet() {
        return bottomSheet;
    }

    @Override
    public void goTo(AppState state) { // TODO: switch to other goTo
        if (currentState != null) {
            currentState.stop();
        }

        switch (state) {
            case LOOKING:
                currentState = new LookingState(this, placeManager, cameraManager);
                break;
            case LEAVING:
                currentState = new LeavingState(this, placeManager, cameraManager);
                break;
            default:
                break;
        }

        currentState.start();
    }

    public void goTo(AppState state, Spot spot) {
        if (currentState != null) {
            currentState.stop();
        }

        switch (state) {
            case NAVIGATING:
                currentState = new NavigatingState(this, placeManager, cameraManager, spot);
                break;
            case PARKED:
                currentState = new ParkedState(this, placeManager, cameraManager, spot);
                break;
            default:
                break;
        }

        currentState.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        } else if (id == R.id.miSearch) {
            try {
                Intent intent =
                        new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                .build(this);
                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
            } catch (GooglePlayServicesRepairableException e) {
                // TODO: Handle the error.
            } catch (GooglePlayServicesNotAvailableException e) {
                // TODO: Handle the error.
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                googlePlace = PlaceAutocomplete.getPlace(this, data); // This is hoisted and then collected in goTo

                if (currentState.APP_STATE != AppState.LOOKING) {
                    goTo(AppState.LOOKING);
                }

                LookingState lookingState = (LookingState) currentState;
                lookingState.showDestinationDetails(googlePlace);

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        } else if (requestCode == OVERLAY_REQUEST_CODE) {
            startService(new Intent(this, OverlayService.class));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
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

    @Override
    protected void onStop() {
        super.onStop();
        checkDrawOverlayPermission();
    }

    @Override
    protected void onStart() {
        super.onStart();
        stopService(new Intent(this, OverlayService.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, OverlayService.class));
    }

    public void checkDrawOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, OVERLAY_REQUEST_CODE);
            } else {
                startService(new Intent(this, OverlayService.class));
            }
        } else {
            startService(new Intent(this, OverlayService.class));
        }
    }
}
