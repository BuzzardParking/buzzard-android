package com.buzzardparking.buzzard.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * A timer service to keep track of parking duration even the user
 * closes the app.
 */
public class TimerService extends IntentService {

    public TimerService() {
        super("buzzard-timer");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("DEBUG", "timer service is running");
    }
}
