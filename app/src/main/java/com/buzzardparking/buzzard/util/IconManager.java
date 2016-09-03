package com.buzzardparking.buzzard.util;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;
import android.view.ViewGroup;

import com.buzzardparking.buzzard.R;
import com.buzzardparking.buzzard.activities.MapActivity;
import com.google.maps.android.ui.IconGenerator;

/**
 * {@link IconManager}: Turns vectors into bitmaps. These bitmaps are consumed for map markers.
 */
public class IconManager {
    private MapActivity context;

    public IconManager(MapActivity context) {
        this.context = context;
    }

    public Bitmap getCarmarker() {
        IconGenerator iconGen = new IconGenerator(context);

        // Define the size you want from dimensions file
        int shapeSize = context.getResources().getDimensionPixelSize(R.dimen.carmarker_size);

        Drawable shapeDrawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.spot_parked_marker, null);
        iconGen.setBackground(shapeDrawable);

        // Create a view container to set the size
        View view = new View(context);
        view.setLayoutParams(new ViewGroup.LayoutParams(shapeSize, shapeSize));
        iconGen.setContentView(view);

        // Create the bitmap
        Bitmap bitmap = iconGen.makeIcon();

        return bitmap;
    }

    public Bitmap getDecoratedSpotIcon() {
        IconGenerator iconGen = new IconGenerator(context);

        // Define the size you want from dimensions file
        int shapeSize = context.getResources().getDimensionPixelSize(R.dimen.carmarker_size);

        Drawable shapeDrawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.spot_decorated, null);
        iconGen.setBackground(shapeDrawable);

        // Create a view container to set the size
        View view = new View(context);
        view.setLayoutParams(new ViewGroup.LayoutParams(shapeSize, shapeSize));
        iconGen.setContentView(view);

        // Create the bitmap
        Bitmap bitmap = iconGen.makeIcon();

        return bitmap;
    }

    public Bitmap getSpotIcon() {
        IconGenerator iconGen = new IconGenerator(context);

        // Define the size you want from dimensions file
        int shapeSize = context.getResources().getDimensionPixelSize(R.dimen.carmarker_size);

        Drawable shapeDrawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.spot_icon, null);
        iconGen.setBackground(shapeDrawable);

        // Create a view container to set the size
        View view = new View(context);
        view.setLayoutParams(new ViewGroup.LayoutParams(shapeSize, shapeSize));
        iconGen.setContentView(view);

        // Create the bitmap
        Bitmap bitmap = iconGen.makeIcon();

        return bitmap;
    }
}
