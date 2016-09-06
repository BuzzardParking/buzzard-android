package com.buzzardparking.buzzard.models;

import com.facebook.Profile;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

/**
 * User model
 */
public class User {

    private String userId;
    private String name;
    boolean preferExternalNavigation;

    private static User instance;
    private ParseObject parseUser;

    public User(String userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    public User(ParseObject parseUser) {
        this.userId = parseUser.getObjectId();
        this.name = parseUser.getString("name");
        this.preferExternalNavigation = parseUser.getBoolean("preferExternalNavigation");
        this.parseUser = parseUser;
    }

    public static User getInstance() {
        if (instance == null) {
            synchronized (User.class) {
                if (instance == null) {
                    final Profile profile = Profile.getCurrentProfile();
                    String userId = profile.getId();
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("User");
                    // TODO: potential bug here.. may get null user if the response hasn't come back yet
                    query.whereEqualTo("userId", userId);
                    query.setLimit(1);
                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {
                            if (!objects.isEmpty()) {
                                instance = new User(objects.get(0));
                            } else {
                                instance = new User(profile.getId(), profile.getName());
                                instance.save();
                            }
                        }
                    });
                }
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

    public void save() {
        if (parseUser == null) {
            parseUser = new ParseObject("User");
        }

        parseUser.put("userId", userId);
        parseUser.put("name", name);
        parseUser.put("preferExternalNavigation", preferExternalNavigation);
        parseUser.saveInBackground();
    }
}
