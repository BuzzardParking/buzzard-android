package com.buzzardparking.buzzard.util;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.v4.content.ContextCompat;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.buzzardparking.buzzard.R;
import com.buzzardparking.buzzard.activities.MainActivity;
import com.buzzardparking.buzzard.models.Place;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.google.maps.android.ui.SquareTextView;

import java.util.List;

public class MarkerManager {

    private ClusterManager<Place> clusterManager;
    private MainActivity context;

    /**
     * MarkerManager: manage parking space markers on the map
     * @param generator an {@link IconGenerator} used to generate parking space markers
     */
    public MarkerManager(IconGenerator generator) {
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

    public void onMarkerClick(GoogleMap map, final MainActivity context) {
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                BottomSheetLayout bottomSheet = context.bottomSheet;
                bottomSheet.showWithSheetView(
                    LayoutInflater
                        .from(context)
                        .inflate(R.layout.image_card, bottomSheet, false)
                );
                return true;
            }
        });
    }

    private class PlaceClusterRenderer extends DefaultClusterRenderer<Place> {
        MainActivity context;
        private final IconGenerator iconGenerator;
        private SparseArray<BitmapDescriptor> mIcons = new SparseArray();
        private final float density;
        private ShapeDrawable coloredCircleBackground;

        public PlaceClusterRenderer(MainActivity context) {
            super(context, context.getMap(), clusterManager);
            this.context = context;
            this.density = context.getResources().getDisplayMetrics().density;
            this.iconGenerator =  new IconGenerator(context);
            this.iconGenerator.setContentView(this.makeSquareTextView(context));
            this.iconGenerator.setTextAppearance(
                    com.google.maps.android.R.style.ClusterIcon_TextAppearance);
            this.iconGenerator.setBackground(this.makeClusterBackground());
        }

        @Override
        protected void onBeforeClusterItemRendered(Place place, MarkerOptions markerOptions) {
            markerOptions
                    .position(place.getLatLng())
                    .alpha(getAlpha(place))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.carmarker));
        }

        @Override
        protected void onBeforeClusterRendered(Cluster<Place> cluster, MarkerOptions markerOptions) {

            int clusterColor = ContextCompat.getColor(context, R.color.colorPrimary);

            int bucket = this.getBucket(cluster);
            BitmapDescriptor descriptor = this.mIcons.get(bucket);
            if(descriptor == null) {
                this.coloredCircleBackground.getPaint().setColor(clusterColor);
                descriptor = BitmapDescriptorFactory.fromBitmap(
                        this.iconGenerator.makeIcon(this.getClusterText(bucket)));
                this.mIcons.put(bucket, descriptor);
            }

            markerOptions.icon(descriptor);
        }

        private SquareTextView makeSquareTextView(Context context) {
            SquareTextView squareTextView = new SquareTextView(context);
            squareTextView.setTextSize(50.0F);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(-2, -2);
            squareTextView.setLayoutParams(layoutParams);
            squareTextView.setId(com.google.maps.android.R.id.text);

            int textPadding = (int)(20.0F * this.density);
            squareTextView.setPadding(textPadding, textPadding, textPadding, textPadding);
            return squareTextView;
        }

        private LayerDrawable makeClusterBackground() {
            // Outline color
            int clusterOutlineColor = ContextCompat.getColor(context, android.R.color.white);

            this.coloredCircleBackground = new ShapeDrawable(new OvalShape());
            ShapeDrawable outline = new ShapeDrawable(new OvalShape());
            outline.getPaint().setColor(clusterOutlineColor);
            LayerDrawable background = new LayerDrawable(
                    new Drawable[]{outline, this.coloredCircleBackground});
            int strokeWidth = (int)(this.density * 3.0F);
            background.setLayerInset(1, strokeWidth, strokeWidth, strokeWidth, strokeWidth);
            return background;
        }
    }

    public float getAlpha(Place place) {
        long age = place.getAgeInMinutes();
//        Log.v("DEBUG", "Marker Age: " + age);
        if (age < 5 ) {
            return 1.0f;
        } else if (age < 10) {
            return 0.7f;
        } else {
            return 0.5f;
        }
    }

    public void setUpClusterer(GoogleMap map, MainActivity context) {

        clusterManager = new ClusterManager<>(context, map);
        clusterManager.setRenderer(new PlaceClusterRenderer(context));

        map.setOnCameraChangeListener(clusterManager);
//        onMarkerClick(map, context);

    }

}
