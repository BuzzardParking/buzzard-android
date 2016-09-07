package com.buzzardparking.buzzard.states;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.text.InputType;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.buzzardparking.buzzard.R;
import com.buzzardparking.buzzard.models.AppState;
import com.buzzardparking.buzzard.models.DynamicSpot;
import com.buzzardparking.buzzard.receivers.AlarmReceiver;
import com.buzzardparking.buzzard.util.BottomSheetManager;
import com.buzzardparking.buzzard.util.CameraManager;
import com.buzzardparking.buzzard.util.PlaceManager;

/**
 * {@link ParkedState}: a user's car is parked at a parking dynamicSpot.
 */
public class ParkedState extends UserState {

    private boolean isFromTransition;
    private CountDownTimer timer;

    public ParkedState(Context context, PlaceManager placeManager, CameraManager cameraManager) {
        super(context, placeManager, cameraManager);
        appState = AppState.PARKED;
        this.dynamicSpot = DynamicSpot.loadTakenSpot(getContext().user);
        isFromTransition = false;
    }

    public ParkedState(Context context, PlaceManager placeManager, CameraManager cameraManager, DynamicSpot spot) {
        super(context, placeManager, cameraManager);
        appState = AppState.PARKED;
        this.dynamicSpot = spot;
        isFromTransition = true;
    }

    @Override
    public void start() {
        if (isFromTransition) {
            getContext().user.setCurrentState(AppState.PARKED);
            dynamicSpot.takenBy(getContext().user);
        }

        if (isReady() || isReadyCache()) {
            updateUI();
        }
    }

    public void updateUI() {
        // TODO:
        // 1. show only your car location and your current location on the map
        // 2. a timer hovers above the car starting counting the time
        // 3. a button to switch to leaving state
        // 4. a evaluation modal to ask user to give a thumb up/down about its parking experience
        // 5. able to set up an alarm clock to remind the parking duration
        // 6. able to fav the parking location, and revisit your parking history

        getContext().prepareView();

        bottomSheet.expand();

        // Temporary marker to show the car location
        getPlaceManager().addCarParkedMarker(getContext().getMap(), dynamicSpot.getLatLng());
        getCameraManager().moveToLocation(getContext().getMap(), dynamicSpot.getLatLng());

        setBackButtonListener();

        getContext().tvBottomSheetHeading.setText(getContext().getString(R.string.tv_parked));
        getContext().tvBottomSheetSubHeading.setText(getContext().getString(R.string.tv_parked_subtitle));

        bottomSheet.setFabIcon(R.drawable.ic_parked);
        bottomSheet.setFabListener(new BottomSheetManager.FabListener() {
            @Override
            public void onClick() {
                getContext().captureMapScreen(dynamicSpot);
                getContext().goTo(AppState.LEAVING);
            }
        });

        bottomSheet.setBottomSheetStateListeners(new BottomSheetManager.BottomSheetListeners() {
            @Override
            public void onCollapsed() {

            }

            @Override
            public void onDragging() {
                bottomSheet.expand();
            }

            @Override
            public void onExpanded() {

            }

            @Override
            public void onHidden() {
                bottomSheet.expand();
            }

            @Override
            public void onSettling() {

            }
        });

        if (!isFromTransition) {
            startTimer(dynamicSpot.timeRemaining());
        } else {
            new MaterialDialog.Builder(getContext())
                    .title(R.string.set_parking_duration_title)
                    .content(R.string.set_parking_duration)
                    .inputType(InputType.TYPE_CLASS_TEXT)
                    .input(R.string.parking_duration_hint, R.string.parking_duration_prefill, new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(MaterialDialog dialog, CharSequence input) {
                            dynamicSpot.setDuration(Integer.parseInt(String.valueOf(input)));
                            startAlarmService(dynamicSpot.durationInMill);
                            startTimer(dynamicSpot.timeRemaining());
                        }
                    })
                    .negativeText(R.string.no)
                    .cancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            Log.d("DEBUG", "cancel the button");
                        }
                    })
                    .show();
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void startAlarmService(long durationInMill) {
        Intent intent = new Intent(getContext().getApplicationContext(), AlarmReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(getContext(), AlarmReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarm = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        alarm.setExact(AlarmManager.RTC_WAKEUP, durationInMill, pIntent);
    }

    private void startTimer(long timeRemaining) {
        // hacky check: duration is not set or too little time left
        if (timeRemaining == Long.MAX_VALUE || timeRemaining <= 1000) {
            return;
        }
        timer = new CountDownTimer(timeRemaining, 1000) {

            public void onTick(long millisUntilFinished) {
                getContext().tvParkingTimer.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                getContext().tvParkingTimer.setText("done!");
            }
        }.start();
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
        }

        getContext().tvParkingTimer.setText("");
    }

    @Override
    public void stop() {
        super.stop();
        getPlaceManager().removeDestinationMarker();
        stopTimer();
    }

    private void setBackButtonListener() {
        getContext().fabBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getContext().goTo(appState.NAVIGATING, dynamicSpot);
            }
        });
    }
}
