package com.buzzardparking.buzzard;

import android.content.Context;
import android.util.Log;

import com.parse.Parse;

/**
 * Created by lee on 8/22/16.
 */
public class BuzzardApplication extends com.activeandroid.app.Application {
    private static Context context;
    private boolean parseInit = false;

    @Override
    public void onCreate() {
        super.onCreate();
        BuzzardApplication.context = this;
        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId(getString(R.string.parse_application_id))
                .server("https://buzzard-parking.herokuapp.com/parse/")
                .build());
    }
}

}
