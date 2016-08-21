package com.buzzardparking.buzzard.util;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.buzzardparking.buzzard.activities.MainActivity;

/**
 * OnActivity is a headless fragment.
 */
public class OnActivity extends Fragment {

    private Listener[] mListeners;

    public static class Builder {

        private static final String TAG = "activity_lifecycle";

        private final FragmentActivity mActivity;
        private final Listener[] mListeners;

        public Builder(FragmentActivity activity, Listener... listeners) {
            mActivity = activity;
            mListeners = listeners;
        }

        public OnActivity build() {
            FragmentManager fm = mActivity.getSupportFragmentManager();
            OnActivity onActivity = (OnActivity) fm.findFragmentByTag(TAG);
            if (onActivity != null) {
                onActivity.setListeners(mListeners);
                return onActivity;
            } else {
                onActivity = new OnActivity();
                onActivity.setListeners(mListeners);
                fm.beginTransaction().add(onActivity, TAG).commit();
                return onActivity;
            }
        }

    }

    private void setListeners(Listener[] listeners) {
        mListeners = listeners;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        notifyOnStatus(Status.CREATED);
        if (savedInstanceState != null) {
            notifyOnRestore(savedInstanceState);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        notifyOnStatus(Status.RESUMED);
    }

    @Override
    public void onPause() {
        super.onPause();
        notifyOnStatus(Status.PAUSED);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        notifyOnSave(outState);
    }

    private void notifyOnStatus(Status status) {
        Log.d(MainActivity.TAG, "Activity status is " + status);
        for (Listener listener : mListeners) {
            listener.onStatus(status);
        }
    }

    private void notifyOnSave(Bundle state) {
        for (Listener listener : mListeners) {
            listener.onSave(state);
        }
    }

    private void notifyOnRestore(Bundle state) {
        for (Listener listener : mListeners) {
            listener.onRestore(state);
        }
    }

    public enum Status {
        CREATED,
        RESUMED,
        PAUSED
    }

    public interface Listener {
        void onStatus(Status status);
        void onSave(Bundle state);
        void onRestore(Bundle state);
    }

}