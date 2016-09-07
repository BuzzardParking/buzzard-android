package com.buzzardparking.buzzard.states;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
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
        getContext().user.setCurrentState(AppState.OVERVIEW);

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
        // 1. a evaluation modal to ask user to give a thumb up/down about its parking experience

        getContext().prepareView();

        // Temporary marker to show the car location
        getPlaceManager().addCarParkedMarker(getContext().getMap(), dynamicSpot.getLatLng());
        getCameraManager().moveToLocation(getContext().getMap(), dynamicSpot.getLatLng());

        setBackButtonListener();
        bottomSheet.setFabIcon(R.drawable.ic_leave);

        getContext().tvBottomSheetHeading.setText(getContext().getString(R.string.tv_parked));
        getContext().tvBottomSheetSubHeading.setText(getContext().getString(R.string.tv_parked_subtitle));

        bottomSheet.setFabListener(new BottomSheetManager.FabListener() {
            @Override
            public void onClick() {
                showConfirmLeavingDialog();
            }
        });

        bottomSheet.expand();
        bottomSheet.viewRendered(new BottomSheetManager.SheetRendering() {
            @Override
            public void done() {
                bottomSheet.expand();
            }
        });

        bottomSheet.setBottomSheetStateListeners(new BottomSheetManager.BottomSheetListeners() {
            @Override
            public void onCollapsed() {
                bottomSheet.expand();
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
            showSetTimerDialog();
        }
    }

    private void showSetTimerDialog() {
        new MaterialDialog.Builder(getContext())
                .title(R.string.set_parking_duration_title)
                .content(R.string.set_parking_duration)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input(R.string.parking_duration_hint, R.string.parking_duration_prefill, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        // TODO: temporarily replay the takenBy to here so that we can reset
                        // the takenAt to now, and start counting from now
                        dynamicSpot.takenBy(getContext().user);
                        dynamicSpot.setDuration(Integer.parseInt(String.valueOf(input)));
                        startAlarmService(dynamicSpot.durationInMill);
                        startTimer(dynamicSpot.timeRemaining());
                    }
                })
                .positiveText(R.string.yes_set_timer)
                .negativeText(R.string.no_set_timer)
                .cancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        Log.d("DEBUG", "cancel the button");
                    }
                })
                .show();
    }

    private void showConfirmLeavingDialog() {
        new MaterialDialog.Builder(getContext())
                .title(R.string.confirm_leaving_title)
                .content(R.string.confirm_leaving_instruction)
                .positiveText(R.string.confirm_agree_leaving)
                .negativeText(R.string.confirm_cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        getContext().captureMapScreen(dynamicSpot);
                        dynamicSpot.leaveSpot();
                        getContext().goTo(AppState.OVERVIEW);
                    }
                })
                .show();
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
                getContext().tvParkingTimer.setText(formatTime(millisUntilFinished));
            }

            public void onFinish() {
                getContext().tvParkingTimer.setText("Time is up!");
            }
        }.start();
    }

    private String formatTime(long millis) {
        int seconds = (int) ((millis / 1000) % 60);
        int minutes = (int) ((millis / 1000) / 60 % 60);
        int hours = (int) ((millis / 1000) / 60 / 60);

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
        }

        getContext().tvParkingTimer.setText("");
    }

    private void cancelAlarm() {
        Intent intent = new Intent(getContext().getApplicationContext(), AlarmReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(getContext(), AlarmReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pIntent);
    }

    @Override
    public void stop() {
        super.stop();
        getPlaceManager().removeDestinationMarker();
        stopTimer();
        cancelAlarm();
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
