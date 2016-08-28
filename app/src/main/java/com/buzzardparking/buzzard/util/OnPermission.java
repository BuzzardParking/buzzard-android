package com.buzzardparking.buzzard.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.buzzardparking.buzzard.activities.MapActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * OnPermission is a headless fragment that handles permission request.
 */
public class OnPermission extends Fragment {

    private final Map<Integer, Request> mRequestMap;
    private final List<Request> mPendingRequests;

    private Context mContext;

    public OnPermission() {
        mRequestMap = new ArrayMap<>();
        mPendingRequests = new ArrayList<>();
    }

    public static class Builder {

        private static final String TAG = "permissions_helper";

        private final FragmentActivity mActivity;

        public Builder(FragmentActivity activity) {
            mActivity = activity;
        }

        public OnPermission build() {
            FragmentManager fm = mActivity.getSupportFragmentManager();
            OnPermission onPermission = (OnPermission) fm.findFragmentByTag(TAG);
            if (onPermission == null) {
                onPermission = new OnPermission();
                fm.beginTransaction().add(onPermission, TAG).commit();
            }
            return onPermission;
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        for (Request request : mPendingRequests) {
            beginRequest(request);
        }
        mPendingRequests.clear();
    }

    public void beginRequest(Request request) {
        mRequestMap.put(request.mRequestCode, request);
        if (mContext != null) {
            int status = ContextCompat.checkSelfPermission(mContext, request.mPermission);
            if (status == PackageManager.PERMISSION_GRANTED) {
                notifyListeners(request, Result.GRANTED);
            } else {
                requestPermissions(new String[]{request.mPermission}, request.mRequestCode);
            }
        } else mPendingRequests.add(request);
    }

    private Request getRequest(int requestCode) {
        Request request = mRequestMap.get(requestCode);
        return request != null ? request : new Request(requestCode, "");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Request request = getRequest(requestCode);
        boolean match = permissions.length > 0 && permissions[0].equals(request.mPermission);
        boolean granted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
        if (match) {
            if (granted) {
                notifyListeners(request, Result.GRANTED);
            } else {
                notifyListeners(request, Result.DENIED);
            }
        }
    }

    private void notifyListeners(Request request, Result result) {
        Log.d(MapActivity.TAG, "Location permission request " + request.mRequestCode + " " + result);
        for (Listener listener : request.mListeners) {
            listener.onResult(request.mRequestCode, result);
        }
    }

    public static class Request {

        public final int mRequestCode;
        public final String mPermission;
        public final Listener[] mListeners;

        public Request(int requestCode, String permission, Listener... listeners) {
            mRequestCode = requestCode;
            mPermission = permission;
            mListeners = listeners;
        }

    }

    public enum Result {
        GRANTED,
        DENIED
    }

    public interface Listener {
        void onResult(int requestCode, Result result);
    }

}
