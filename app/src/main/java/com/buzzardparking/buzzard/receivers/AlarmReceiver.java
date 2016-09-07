package com.buzzardparking.buzzard.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.buzzardparking.buzzard.services.TimerService;

/**
 * Alarm receiver that receives the broadcast from the timer service.
 */
public class AlarmReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 12345;
    public static final String ACTION = "com.buzzardparking.buzzard.alarm";

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, TimerService.class);
        i.putExtra("remainingMill", "bar");
        Log.d("DEBUG", "come to alarm receiver");
        //TODO: show user notification
        context.startService(i);
    }
}
