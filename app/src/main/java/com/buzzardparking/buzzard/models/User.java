package com.buzzardparking.buzzard.models;

import com.parse.ParseObject;

import org.parceler.Parcel;

import java.util.Collections;
import java.util.List;

/**
 * User model
 */
@Parcel(analyze={User.class})
public class User {

    String userId;
    String name;
    boolean preferExternalNavigation;

    // empty constructor required by Parcel
    public User() {

    }
    public User(String name) {
        this.name = name;
    }

    public User(ParseObject parseUser) {
        this.userId = parseUser.getObjectId();
        this.name = parseUser.getString("name");
        this.preferExternalNavigation = parseUser.getBoolean("preferExternalNavigation");
    }

    public boolean doesPreferExternalNavigation() {
        return preferExternalNavigation;
    }

    public void setPreferExternalNavigation(boolean preferExternalNavigation) {
        this.preferExternalNavigation = preferExternalNavigation;
    }

    public static List<User> getAll() {
        return Collections.emptyList();
    }
}
