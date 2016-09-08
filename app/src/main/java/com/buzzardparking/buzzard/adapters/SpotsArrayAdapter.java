package com.buzzardparking.buzzard.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.buzzardparking.buzzard.R;
import com.buzzardparking.buzzard.gateways.ReverseGeocodingGateway;
import com.buzzardparking.buzzard.models.DynamicSpot;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        final ViewHolder viewHolder;

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

        String displayLocation = String.format("%s, %s", String.format("%.4f", spot.getLatLng().latitude), String.format("%.4f", spot.getLatLng().longitude));
        viewHolder.tvParkingSpot.setText(displayLocation);

        displayAddress(spot.getLatLng(), viewHolder); // This will happen async
        viewHolder.tvParkingTime.setText(spot.getTakenAtTimestamp());

        if (spot.snapshot != null) {
            Picasso.with(getContext())
                    .load(spot.snapshot.getUrl())
                    .placeholder(R.drawable.spot_parked_marker)
                    .transform(new RoundedCornersTransformation(2, 2))
                    .resize(1050, 400)
                    .centerCrop()
                    .into(viewHolder.ivParkingSpot);
        } else {
            viewHolder.ivParkingSpot.setMinimumHeight(100);
        }

        return convertView;
    }

    private void displayAddress(LatLng latLng, final ViewHolder viewHolder) {
        ReverseGeocodingGateway geocodingGateway = new ReverseGeocodingGateway();
        geocodingGateway.FetchAddressInBackground(latLng, new ReverseGeocodingGateway.GetAddressCallback() {
            @Override
            public void done(JSONObject jsonObject) throws JSONException {
                try {
                    JSONArray resultsArr = jsonObject.getJSONArray("results");
                    JSONObject first = resultsArr.getJSONObject(0);
                    JSONArray addressComponents = first.getJSONArray("address_components");

                    String streetNum = addressComponents.getJSONObject(0).getString("long_name");
                    String streetName = addressComponents.getJSONObject(1).getString("short_name");
                    String city = addressComponents.getJSONObject(3).getString("long_name");
                    String state = addressComponents.getJSONObject(5).getString("short_name");

                    String address = streetNum + " " + streetName;

                    if ((streetName == "null") || (city == "null")) {
                        return;
                    }

                    viewHolder.tvParkingSpot.setText(address + "., " + city + ", " + state);



                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
