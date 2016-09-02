package com.buzzardparking.buzzard.models;

import com.buzzardparking.buzzard.util.OnPermission;

/**
 * Created by nathansass on 9/1/16.
 */
public class Permission implements OnPermission.Listener {
    private OnPermission.Result permissionResult;
    public boolean isGranted() {
        return permissionResult == OnPermission.Result.GRANTED;
    }
    @Override
    public void onResult(int requestCode, OnPermission.Result result) {
        this.permissionResult = result;
    }
}
