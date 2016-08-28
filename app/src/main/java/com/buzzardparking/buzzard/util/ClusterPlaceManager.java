package com.buzzardparking.buzzard.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.v4.content.ContextCompat;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.buzzardparking.buzzard.R;
import com.buzzardparking.buzzard.activities.MapActivity;
import com.buzzardparking.buzzard.models.Spot;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.google.maps.android.ui.SquareTextView;

/**
 * {@link ClusterPlaceManager}: Manages the display and rendering of place clusters on the map
 */
public class ClusterPlaceManager extends DefaultClusterRenderer<Spot>{
    MapActivity context;
    private final IconGenerator iconGenerator;
    private SparseArray<BitmapDescriptor> mIcons = new SparseArray();
    private final float density;
    private ShapeDrawable coloredCircleBackground;

    public ClusterPlaceManager(MapActivity context, ClusterManager<Spot> clusterManager) {
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
    protected void onBeforeClusterItemRendered(Spot spot, MarkerOptions markerOptions) {
        markerOptions
                .position(spot.getLatLng())
                .alpha(getAlpha(spot))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.carmarker));
    }

    @Override
    protected void onBeforeClusterRendered(Cluster<Spot> cluster, MarkerOptions markerOptions) {

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

    public float getAlpha(Spot spot) {
        long age = spot.getAgeInMinutes();
//        Log.v("DEBUG", "Marker Age: " + age);
        if (age < 5 ) {
            return 1.0f;
        } else if (age < 10) {
            return 0.7f;
        } else {
            return 0.5f;
        }
    }
}