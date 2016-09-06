package com.buzzardparking.buzzard.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.buzzardparking.buzzard.R;
import com.buzzardparking.buzzard.models.DynamicSpot;
import com.squareup.picasso.Picasso;

import java.util.List;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

/**
 * {@link SpotsArrayAdapter} is an adapter to map parking history to a dynamicSpot list.
 */
public class SpotsArrayAdapter extends ArrayAdapter<DynamicSpot> {
    // View lookup cache
    private static class ViewHolder {
        public TextView tvParkingSpot;
        public TextView tvParkingTime;
        public ImageView ivParkingSpot;
    }

    public SpotsArrayAdapter(Context context, List<DynamicSpot> spots) {
        super(context, 0, spots);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final DynamicSpot spot = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.spot_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvParkingSpot = (TextView) convertView.findViewById(R.id.tvParkingSpot);
            viewHolder.tvParkingTime = (TextView) convertView.findViewById(R.id.tvParkingTime);
            viewHolder.ivParkingSpot = (ImageView) convertView.findViewById(R.id.ivParkingSpot);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String displayLocation =  String.format("Parking Location: %s, %s", spot.getLatLng().latitude, spot.getLatLng().longitude);
        viewHolder.tvParkingSpot.setText(displayLocation);
        viewHolder.tvParkingTime.setText(spot.getTakenAtTimestamp());

        if (spot.snapshot != null) {
            Picasso.with(getContext())
                    .load(spot.snapshot.getUrl())
                    .placeholder(R.drawable.bg_loading)
                    .transform(new RoundedCornersTransformation(2, 2))
                    .resize(500, 200)
                    .centerCrop()
                    .into(viewHolder.ivParkingSpot);
        }

        return convertView;
    }
}
