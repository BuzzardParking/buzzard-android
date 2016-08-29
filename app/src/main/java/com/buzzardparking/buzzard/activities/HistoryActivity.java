package com.buzzardparking.buzzard.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.buzzardparking.buzzard.R;
import com.buzzardparking.buzzard.adapters.SpotsArrayAdapter;
import com.buzzardparking.buzzard.models.Spot;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private ArrayList<Spot> spotsArray;
    private SpotsArrayAdapter spotsArrayAdapter;
    private ListView lvSpots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        spotsArray = new ArrayList<>();
        spotsArrayAdapter = new SpotsArrayAdapter(this, spotsArray);
        lvSpots = (ListView) findViewById(R.id.lvParkingHistory);
        lvSpots.setAdapter(spotsArrayAdapter);

        loadParkingHistory();
    }

    private void loadParkingHistory() {
        ParseQuery query = new ParseQuery("Spot");
        query.findInBackground(new FindCallback() {
            @Override
            public void done(Object spots, Throwable throwable) {
                spotsArray.clear();
                spotsArrayAdapter.addAll(Spot.fromParse(spots));
            }

            @Override
            public void done(List spots, ParseException e) {

            }
        });
    }
}
