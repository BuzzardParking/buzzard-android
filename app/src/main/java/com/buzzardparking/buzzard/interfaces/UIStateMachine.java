package com.buzzardparking.buzzard.interfaces;

import com.buzzardparking.buzzard.models.AppState;

/**
 * Created by nathansass on 8/17/16.
 */
public interface UIStateMachine {
    public void gotTo(AppState state);
}
