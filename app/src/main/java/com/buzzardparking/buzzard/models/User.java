package com.buzzardparking.buzzard.models;

import com.facebook.Profile;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.parceler.Parcel;
import org.parceler.Transient;

import java.util.List;

/**
 * User model
 */
@Parcel(analyze={User.class})
public class User {

    String userId;
    String name;
    boolean preferExternalNavigation;
    AppState currentState;

    public String getUserId() {
        return userId;
    }

    public boolean isPreferExternalNavigation() {
        return preferExternalNavigation;
    }

    public String getName() {
        return name;
    }

    @Transient
    private static User instance;

    @Transient
    public ParseObject parseUser;

    // empty constructor required by Parcel
    public User() {

    }

    public User(String userId, String name) {
        this.userId = userId;
        this.name = name;
        this.currentState = AppState.OVERVIEW;
        this.parseUser = new ParseObject("User");
    }

    public User(ParseObject parseUser) {
        this.userId = parseUser.getString("userId");
        this.name = parseUser.getString("name");
        this.preferExternalNavigation = parseUser.getBoolean("preferExternalNavigation");
        this.currentState = AppState.values()[parseUser.getInt("currentState")];
        this.parseUser = parseUser;
    }

    public static User getInstance() {
        if (instance == null) {
            final Profile profile = Profile.getCurrentProfile();
            String userId = profile.getId();
            ParseQuery<ParseObject> query = ParseQuery.getQuery("User");
            query.whereEqualTo("userId", userId);
            query.setLimit(1);
            try {
                // TODO: better to use the findInBackground, but how to wire up the background job?
                List<ParseObject> objects = query.find();
                if (!objects.isEmpty()) {
                    instance = new User(objects.get(0));
                } else {
                    instance = new User(profile.getId(), profile.getName());
                    instance.saveParse(null);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return instance;
    }

    public boolean doesPreferExternalNavigation() {
        return preferExternalNavigation;
    }

    public void setPreferExternalNavigation(boolean preferExternalNavigation) {
        this.preferExternalNavigation = preferExternalNavigation;
    }

    public void saveParse(SaveCallback saveCallback) {
        parseUser.put("userId", userId);
        parseUser.put("name", name);
        parseUser.put("preferExternalNavigation", preferExternalNavigation);
        parseUser.put("currentState", currentState.ordinal());
        parseUser.saveInBackground(saveCallback);
    }

    public void setCurrentState(AppState state) {
        currentState = state;
        saveParse(null);
    }

    public AppState getCurrentState() {
        return currentState;
    }

}
