package com.buzzardparking.buzzard.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import android.widget.TextView;

import com.buzzardparking.buzzard.R;
import com.buzzardparking.buzzard.adapters.SpotsArrayAdapter;
import com.buzzardparking.buzzard.models.DynamicSpot;
import com.buzzardparking.buzzard.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private ArrayList<DynamicSpot> spotsArray;
    private SpotsArrayAdapter spotsArrayAdapter;
    private ListView lvSpots;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        setupToolbar();
        spotsArray = new ArrayList<>();
        spotsArrayAdapter = new SpotsArrayAdapter(this, spotsArray);
        lvSpots = (ListView) findViewById(R.id.lvParkingHistory);
        lvSpots.setAdapter(spotsArrayAdapter);

        loadParkingHistory();
    }

    private void loadParkingHistory() {
        ParseQuery query = new ParseQuery("DynamicSpot");
        query
                .include("staticSpot")
                .include("producer")
                .include("consumer")
                .include("snapshot")
                .whereEqualTo("consumer", User.getInstance().parseUser)
                .findInBackground(new FindCallback() {
                    @Override
                    public void done(Object spots, Throwable throwable) {
                        spotsArray.clear();
                        spotsArrayAdapter.addAll(DynamicSpot.fromParseDynamicSpots(spots));
                    }

                    @Override
                    public void done(List spots, ParseException e) {

                    }
                });
    }


    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(R.string.parkingHistoryToolbarTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
