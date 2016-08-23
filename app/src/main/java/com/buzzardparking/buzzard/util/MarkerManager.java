package com.buzzardparking.buzzard.util;

import android.animation.ValueAnimator;
import android.graphics.Point;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.buzzardparking.buzzard.R;
import com.buzzardparking.buzzard.activities.MainActivity;
import com.buzzardparking.buzzard.models.Place;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;
import java.util.List;

public class MarkerManager {

    private ArrayList<Marker> markers;
    private ClusterManager<Place> clusterManager;

    /**
     * MarkerManager: manage parking space markers on the map
     * @param generator an {@link IconGenerator} used to generate parking space markers
     */
    public MarkerManager(IconGenerator generator) {
        markers = new ArrayList<>();
    }

    /**
     * Add a parking space marker to the map
     *
     * @param place all the details will be used the onBeforeItemClustered method
     */
    public void addMarker(Place place) {
        clusterManager.addItem(place);
        clusterManager.cluster();
    }

    public void addAll(List<Place> places) {
        clusterManager.addItems(places);
        clusterManager.cluster();
    }

    public void removeMarkers() {
        clusterManager.clearItems(); //these used to be seperate
        clusterManager.cluster();
    }

    // Get the marker's position and the map's projection.
    // Create start and stop LatLng's to animate with.
    // Create a ValueAnimator and add an update listener.
    // Use SphericalUtil to calculate interpolated LatLng.
    // Set the marker's position to this LatLng.
    // Set the animator's interpolator and duration.
    // Start animator.
    // TODO: make a better animation
    private void animate(GoogleMap map, final Marker marker) {
        final LatLng target = marker.getPosition();
        Projection projection = map.getProjection();
        Point endPoint = projection.toScreenLocation(target);
        Point startPoint = new Point(endPoint.x, 0);
        final LatLng offscreen = projection.fromScreenLocation(startPoint);
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator an) {
                float value = (float) an.getAnimatedValue();
                double fraction = Float.valueOf(value).doubleValue();
                LatLng latLng = SphericalUtil.interpolate(offscreen, target, fraction);
                marker.setPosition(latLng);
            }
        });

        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(2500);
        animator.start();
    }

    private class PlaceRenderer extends DefaultClusterRenderer<Place> {
        public PlaceRenderer(MainActivity context) {
            super(context, context.getMap(), clusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(Place item, MarkerOptions markerOptions) {
            markerOptions
                    .position(item.getLatLng())
//                    .alpha(0.5f) //TODO: use alpha
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.carmarker));
        }
    }

    public void setUpClusterer(GoogleMap map, MainActivity context) {

        clusterManager = new ClusterManager<>(context, map);
        clusterManager.setRenderer(new PlaceRenderer(context));

        map.setOnCameraChangeListener(clusterManager);
        map.setOnMarkerClickListener(clusterManager);
    }

}
