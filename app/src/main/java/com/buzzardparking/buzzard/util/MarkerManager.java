package com.buzzardparking.buzzard.util;

import android.animation.ValueAnimator;
import android.graphics.Point;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.buzzardparking.buzzard.activities.MapActivity;
import com.buzzardparking.buzzard.models.DynamicSpot;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.clustering.ClusterManager;

import java.util.List;

public class MarkerManager {

    private com.google.maps.android.clustering.ClusterManager clusterManager;
    private IconManager iconManager;

    Marker destinationMarker;
    Marker carParkedMarker;
    Marker parkingSpotMarker;

    /**
     * MarkerManager: manage parking space markers on the map
     * @param iconManager an {@link IconManager} used to generate parking space markers
     */
    public MarkerManager(IconManager iconManager) {
        this.iconManager = iconManager;
    }

    /**
     * Add a parking space marker to the map
     *
     * @param dynamicSpot all the details will be used the onBeforeItemClustered method
     */
    public void addMarker(DynamicSpot dynamicSpot) {
        clusterManager.addItem(dynamicSpot);
        clusterManager.cluster();
    }

    public void addDestinationMarker(GoogleMap map, LatLng latLng) {
        MarkerOptions opts = new MarkerOptions()
                .position(latLng);
        destinationMarker = map.addMarker(opts);

        MarkerManager.animate(map, destinationMarker, 1000);
    }

    public void addCarParkedMarker(GoogleMap map, LatLng latLng) {
        MarkerOptions opts = new MarkerOptions()
            .position(latLng)
            .icon(BitmapDescriptorFactory.fromBitmap(iconManager.getCarmarker()));
        carParkedMarker = map.addMarker(opts);

        MarkerManager.animate(map, carParkedMarker, 1000);
    }

    public void addParkingSpotMarker(GoogleMap map, LatLng latLng) {
        MarkerOptions opts = new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromBitmap(iconManager.getSpotIcon()));
        parkingSpotMarker = map.addMarker(opts);

        MarkerManager.animate(map, parkingSpotMarker, 1000);
    }

    public void addParkingSpotMarkerNew(GoogleMap map, LatLng latLng) {
        MarkerOptions opts = new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromBitmap(iconManager.getDecoratedSpotIcon()));
        parkingSpotMarker = map.addMarker(opts);

        MarkerManager.animate(map, parkingSpotMarker, 1000);
    }

    public void addParkingSpotMarkerOld(GoogleMap map, LatLng latLng) {
        MarkerOptions opts = new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromBitmap(iconManager.getSpotIcon()));
        parkingSpotMarker = map.addMarker(opts);

        MarkerManager.animate(map, parkingSpotMarker, 1000);
    }

    public void removeDestinationMarker() {
        if (destinationMarker != null) {
            destinationMarker.remove();
        }
        if (carParkedMarker != null) {
            carParkedMarker.remove();
        }
        if (parkingSpotMarker != null) {
            parkingSpotMarker.remove();
        }
    }

    public void addAll(List<DynamicSpot> spots) {
        clusterManager.addItems(spots);
        clusterManager.cluster();
    }

    public void removeMarkers() {
        clusterManager.clearItems(); //these used to be seperate
        clusterManager.cluster();
    }

    public static void animate(GoogleMap map, final Marker marker, int duration) {
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
        animator.setDuration(duration);
        animator.start();
    }

    public void setUpClusterer(GoogleMap map, MapActivity context) {

        clusterManager = new com.google.maps.android.clustering.ClusterManager(context, map);
        clusterManager.setRenderer(new ClusterPlaceManager(context, clusterManager, iconManager));
        map.setOnMarkerClickListener(clusterManager); // This must be set so onClusterItemClick will work
        map.setOnCameraChangeListener(clusterManager);
    }

    public ClusterManager getClusterManager() {
        return clusterManager;
    }

}
