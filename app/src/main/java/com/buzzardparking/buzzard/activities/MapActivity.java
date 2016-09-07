package com.buzzardparking.buzzard.activities;

import android.Manifest;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.buzzardparking.buzzard.R;
import com.buzzardparking.buzzard.interfaces.UIStateMachine;
import com.buzzardparking.buzzard.models.AppState;
import com.buzzardparking.buzzard.models.Client;
import com.buzzardparking.buzzard.models.DynamicSpot;
import com.buzzardparking.buzzard.models.Map;
import com.buzzardparking.buzzard.models.Permission;
import com.buzzardparking.buzzard.models.User;
import com.buzzardparking.buzzard.services.OverlayService;
import com.buzzardparking.buzzard.states.LookingState;
import com.buzzardparking.buzzard.states.NavigatingState;
import com.buzzardparking.buzzard.states.OverviewState;
import com.buzzardparking.buzzard.states.ParkedState;
import com.buzzardparking.buzzard.states.UserState;
import com.buzzardparking.buzzard.util.AddLocationLayer;
import com.buzzardparking.buzzard.util.AddMarkerManager;
import com.buzzardparking.buzzard.util.BottomSheetManager;
import com.buzzardparking.buzzard.util.CameraManager;
import com.buzzardparking.buzzard.util.IconManager;
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
import com.google.android.gms.maps.GoogleMap.SnapshotReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.io.ByteArrayOutputStream;

import cn.refactor.smileyloadingview.lib.SmileyLoadingView;

public class MapActivity extends AppCompatActivity
        implements UIStateMachine {

    public static final String TAG = MapActivity.class.getSimpleName();
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final int SETTING_REQUEST_CODE = 2;
    private static final int OVERLAY_REQUEST_CODE = 99;

    // Singleton instance for permissions
    public Map buzzardMap;
    public Permission permissions;
    public Client googleClient;
    // states
    public UserState currentState;

    private PlaceManager placeManager;
    private CameraManager cameraManager;

    /* UI ELEMENTS */
    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle drawerToggle;
    private SmileyLoadingView smileyLoadingView;
    private RelativeLayout rlProgressBar;


    private Toolbar toolbar;
    // TODO: public UI element doesn't smell good, maybe refactor later...
    public TextView tvBottomSheetHeading;
    public TextView tvBottomSheetSubHeading;
    public TextView tvBottomSheetSubheadingRight;
    public TextView tvParkingTimer;
    public RelativeLayout rlTopPieceContainer;
    public Button btnFindParking;
    public FloatingActionButton fabBtnSecondary;
    public FloatingActionButton fabBack;
    public RelativeLayout rlAddMarker;
    public ImageView ivStreetView;

    public BottomSheetBehavior bottomSheetBehavior;
    private  BottomSheetManager bottomSheetManager;

    // TODO: refactor these public instance variables
    public Place googlePlace;

    public DynamicSpot targetSpot;
    // TODO: scope user with sessions
    public User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = User.getInstance();
        setContentView(R.layout.activity_map);
        setupToolbar();
        setupDrawer();
        setupProgressbar();
        setupBottomSheet();
        setUpAddMarkerLayer();

        IconManager iconManager = new IconManager(this);
        MarkerManager markerManager = new MarkerManager(iconManager);

        placeManager = new PlaceManager(markerManager, this);

        AddLocationLayer layer = new AddLocationLayer();
        cameraManager = new CameraManager(savedInstanceState);
        AddMarkerManager click = new AddMarkerManager(this, placeManager, cameraManager);
        TrackLocation track = new TrackLocation(getLocationRequest(), new LogLocation());

        new OnActivity.Builder(this, track).build();

        // These are for kept for checking if resources are loaded
        buzzardMap = new Map();
        permissions = new Permission();
        googleClient = new Client();

        // initial state fetched from the server
        goTo(user.getCurrentState());

        // initialize the map system and view
        FragmentManager fm = getSupportFragmentManager();
        SupportMapFragment fragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        if (fragment != null) {
            fragment.getMapAsync(new OnMap(buzzardMap, placeManager, click, layer, cameraManager, track, currentState));
        }

        // connect the google client
        GoogleApiClient client = getGoogleApiClient();
        client.registerConnectionCallbacks(new OnClient(client, cameraManager, track, googleClient, currentState));

        // request permissions about current location
        int requestCode = 1001;
        String permission = Manifest.permission.ACCESS_FINE_LOCATION;
        OnPermission.Request location = new OnPermission.Request(requestCode, permission, layer, cameraManager, track, permissions, currentState);

        OnPermission onPermission = new OnPermission.Builder(this).build();
        onPermission.beginRequest(location);

        checkDrawOverlayPermission();

//        streetViewPanoramaFragment = (StreetViewPanoramaFragment)getFragmentManager()
//                .findFragmentById(R.id.streetviewpanorama);
//        FragmentTransaction ft = getFragmentManager().beginTransaction();
//        ft.hide(streetViewPanoramaFragment);
//        ft.commit();
    }


    private void setupProgressbar() {
        smileyLoadingView = (SmileyLoadingView) findViewById(R.id.smileyLoadingView);
        rlProgressBar = (RelativeLayout) findViewById(R.id.rlProgressBar);
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
        drawerToggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawer.addDrawerListener(drawerToggle);

        NavigationView navView = (NavigationView) findViewById(R.id.navView);
        navView.setNavigationItemSelectedListener(
            new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem menuItem) {
                    selectDrawerItem(menuItem);
                    return true;
                }
            });
    }

    private void selectDrawerItem(MenuItem menuItem) {
        switch(menuItem.getItemId()) {
            case R.id.nav_parking_history:
                startActivity(new Intent(this, HistoryActivity.class));
                break;
            case R.id.nav_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            default:
                break;
        }
        mDrawer.closeDrawers();
    }

    private void setUpAddMarkerLayer() {
        rlAddMarker = (RelativeLayout) findViewById(R.id.rlAddMarker);
    }

    private void setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.rlBottomSheet));
        tvBottomSheetHeading = (TextView) findViewById(R.id.tvBottomSheetHeading);
        tvBottomSheetSubHeading = (TextView) findViewById(R.id.tvBottomSheetSubheading);
        tvBottomSheetSubheadingRight = (TextView) findViewById(R.id.tvBottomSheetSubheadingRight);
        tvParkingTimer = (TextView) findViewById(R.id.tvParkingTimer);
        rlTopPieceContainer = (RelativeLayout) findViewById(R.id.rlTopPieceContainer);
        btnFindParking = (Button) findViewById(R.id.btnFindParking);
        fabBtnSecondary = (FloatingActionButton) findViewById(R.id.fabActionSecondary);
        fabBack = (FloatingActionButton) findViewById(R.id.fabBack);
        ivStreetView = (ImageView) findViewById(R.id.ivStreetView);

        bottomSheetManager = new BottomSheetManager(this, bottomSheetBehavior);
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

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable("dynamicSpot", Parcels.wrap(currentState.getDynamicSpot()));
        super.onSaveInstanceState(savedInstanceState);
    }

    public GoogleMap getMap() {
        return buzzardMap.get();
    }

    public BottomSheetBehavior getBottomSheetBehavior() {
        return bottomSheetBehavior;
    }

    @Override
    public void goTo(AppState state) { // TODO: switch to other goTo
        if (currentState != null) {
            currentState.stop();
        }

        switch (state) {
            case OVERVIEW:
                currentState = new OverviewState(this, placeManager, cameraManager);
                break;
            case LOOKING:
                currentState = new LookingState(this, placeManager, cameraManager);
                break;
            case NAVIGATING:
                currentState = new NavigatingState(this, placeManager, cameraManager);
                break;
            case PARKED:
                currentState = new ParkedState(this, placeManager, cameraManager);
                break;
            default:
                break;
        }

        currentState.start();
    }

    public void goTo(AppState state, DynamicSpot spot) {
        if (currentState != null) {
            currentState.stop();
        }

        switch (state) {
            case LOOKING:
                currentState = new LookingState(this, placeManager, cameraManager, spot);
                break;
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
                Intent intent = new PlaceAutocomplete
                        .IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                        .build(this);
                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            } catch (GooglePlayServicesRepairableException e) {
                // TODO: Handle the error.
            } catch (GooglePlayServicesNotAvailableException e) {
                // TODO: Handle the error.
                e.printStackTrace();
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

                if (currentState.appState != AppState.LOOKING) {
                    goTo(AppState.LOOKING);
                }

                ((LookingState) currentState).showDestinationDetails(googlePlace);

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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, OverlayService.class));
    }

    private void checkDrawOverlayPermission() {
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

    public void clearBottomSheetHeadings() {
        tvBottomSheetHeading.setText("");
        tvBottomSheetSubHeading.setText("");
        tvBottomSheetSubheadingRight.setText("");
    }

    public void prepareView() {
        // Shift the screen orientation after making changes here
        this.clearBottomSheetHeadings();
        this.rlTopPieceContainer.setVisibility(View.VISIBLE);
        this.btnFindParking.setVisibility(View.GONE);
        this.tvParkingTimer.setVisibility(View.GONE);
        this.bottomSheetManager.showFab();
        this.ivStreetView.setVisibility(View.GONE);
    }

    public void hideProgressBar() {
        rlProgressBar.setVisibility(View.GONE);
        smileyLoadingView.stop();
    }
    public void showProgressBar() {
        rlProgressBar.setVisibility(View.VISIBLE);
        smileyLoadingView.start();
    }



    public void captureMapScreen(final DynamicSpot spot) {
        SnapshotReadyCallback callback = new SnapshotReadyCallback() {
            Bitmap bitmap;

            @Override
            public void onSnapshotReady(final Bitmap snapshot) {
                bitmap = snapshot;
                try {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
                    final ParseFile snapShotFile = new ParseFile(System.currentTimeMillis() + ".png", stream.toByteArray());
                    snapShotFile.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            DynamicSpot.linkSnapshot(spot, snapShotFile);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        getMap().snapshot(callback);
    }

    @Override
    public void onBackPressed() {
        fabBack.performClick();
    }

    public BottomSheetManager getBottomSheetManager() {
        return bottomSheetManager;
    }
}
