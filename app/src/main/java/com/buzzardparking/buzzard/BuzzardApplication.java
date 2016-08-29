package com.buzzardparking.buzzard;

import android.content.Context;

import com.buzzardparking.buzzard.util.Foreground;
import com.parse.Parse;

/**
 * {@link BuzzardApplication} Application that configures global state.
 */
public class BuzzardApplication extends com.activeandroid.app.Application {
    private static Context context;
    private boolean parseInit = false;

    @Override
    public void onCreate() {
        super.onCreate();
        BuzzardApplication.context = this;
        Foreground.init(this);
        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId(getString(R.string.parse_application_id))
                .server("https://buzzard-parking.herokuapp.com/parse/")
                .build());
    }
}
