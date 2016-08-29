package com.buzzardparking.buzzard.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.buzzardparking.buzzard.R;
import com.buzzardparking.buzzard.models.Spot;

import java.util.List;

/**
 * {@link SpotsArrayAdapter} is an adapter to map parking history to a spot list.
 */
public class SpotsArrayAdapter extends ArrayAdapter<Spot> {
    // View lookup cache
    private static class ViewHolder {
        public TextView tvParkingSpot;
        public TextView tvParkingTime;
    }

    public SpotsArrayAdapter(Context context, List<Spot> spots) {
        super(context, 0, spots);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Spot spot = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.spot_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvParkingSpot = (TextView) convertView.findViewById(R.id.tvParkingSpot);
            viewHolder.tvParkingTime = (TextView) convertView.findViewById(R.id.tvParkingTime);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String displayLocation =  String.format("Parking Location: %s, %s", spot.getLatLng().latitude, spot.getLatLng().longitude);
        viewHolder.tvParkingSpot.setText(displayLocation);
        viewHolder.tvParkingTime.setText(spot.getTimestampStr());
        return convertView;
    }
}
